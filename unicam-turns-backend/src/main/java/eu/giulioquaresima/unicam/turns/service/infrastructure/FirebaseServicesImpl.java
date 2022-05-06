package eu.giulioquaresima.unicam.turns.service.infrastructure;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.MulticastMessage;
import com.google.firebase.messaging.Notification;
import com.google.firebase.messaging.SendResponse;
import com.google.firebase.messaging.WebpushConfig;
import com.google.firebase.messaging.WebpushFcmOptions;

import eu.giulioquaresima.unicam.turns.domain.entities.FirebaseToken;
import eu.giulioquaresima.unicam.turns.domain.entities.Ticket;
import eu.giulioquaresima.unicam.turns.domain.entities.User;

@Service
public class FirebaseServicesImpl implements FirebaseServices, InitializingBean
{
	private final static Logger LOGGER = LoggerFactory.getLogger(FirebaseServicesImpl.class);
	
	private FirebaseApp firebaseApp = null;

	@Override
	public void afterPropertiesSet() throws Exception
	{
		Path path = serviceAccountKey();
		if (path != null)
		{
			try (InputStream inputStream = Files.newInputStream(path))
			{
				FirebaseOptions options = FirebaseOptions.builder()
						.setCredentials(GoogleCredentials.fromStream(inputStream))
						.build();
				
				firebaseApp = FirebaseApp.initializeApp(options);
			}
		}
	}
	
	@Override
	@Async
	public void yourTicketCalled(Ticket ticket) throws FirebaseMessagingException
	{
		if (ticket != null)
		{
			User ticketOwner = ticket.getOwner();
			if (ticketOwner != null)
			{
				Collection<FirebaseToken> firebaseTokens = ticketOwner.getFirebaseTokens();
				Map<String, List<FirebaseToken>> firebaseTokensGroupByOrigins = 
						firebaseTokens
						.stream()
						.collect(Collectors.groupingBy(FirebaseToken::getOrigin));
				
				for (Map.Entry<String, List<FirebaseToken>> entry : firebaseTokensGroupByOrigins.entrySet())
				{
					if ( ! entry.getValue().isEmpty() )
					{
						MulticastMessage multicastMessage = build(
								entry.getKey(), 
								ticket, 
								entry.getValue()
								);
						BatchResponse batchResponse = FirebaseMessaging.getInstance(firebaseApp).sendMulticast(multicastMessage);
						if (LOGGER.isInfoEnabled())
						{
							for (SendResponse sendResponse : batchResponse.getResponses())
							{
								if (sendResponse.isSuccessful())
								{
									LOGGER.info("Successfully send push with id {}", sendResponse.getMessageId());
								}
								else
								{
									LOGGER.error("Firebase error", sendResponse.getException());
								}
							}
						}
					}
				}
			}
		}
	}
	
	protected MulticastMessage build(String origin, Ticket ticket, Collection<FirebaseToken> firebaseTokens)
	{
		return MulticastMessage
				.builder()
				.addAllTokens(firebaseTokens.stream().map(FirebaseToken::getToken).collect(Collectors.toSet()))
				.setNotification(Notification
						.builder()
						.setTitle("Il suo ticket è stato appena chiamato!")
						.setBody(String.format(""
								+ "Il suo ticket n. %d "
								+ "prelevato presso il distributore %s "
								+ "è stato appena chiamato.", 
								ticket.getPublicNumber(),
								ticket.getSession().getTicketDispenser().getLabel()))
						.build()
						)
				.setWebpushConfig(WebpushConfig
						.builder()
						.setFcmOptions(WebpushFcmOptions
								.builder()
								.setLink(origin + "/tabs/tab2") // FIXME Path del ticket dispenser
								.build())
						.build())
				.build();
	}

	protected Path serviceAccountKey()
	{
		Path path = null;
		String GOOGLE_APPLICATION_CREDENTIALS = System.getenv("GOOGLE_APPLICATION_CREDENTIALS");
		if (StringUtils.hasText(GOOGLE_APPLICATION_CREDENTIALS))
		{
			path = Paths.get(GOOGLE_APPLICATION_CREDENTIALS);
		}
		if (path == null || !Files.isRegularFile(path))
		{
			path = Paths.get(System.getProperty("user.dir"), "serviceAccountKey.json"); // Working directory
			if (!Files.isRegularFile(path))
			{
				path = Paths.get(System.getProperty("user.home"), "serviceAccountKey.json"); // User's home directory
			}
		}
		if (Files.isRegularFile(path))
		{
			return path;
		}
		
		return null;
	}
	
	public static void main(String[] args) throws Exception
	{
		FirebaseServicesImpl _self = new FirebaseServicesImpl();
		_self.afterPropertiesSet();
		
		// This registration token comes from the client FCM SDKs.
//		String registrationToken = "dmyXLV2k_W2-NLIWqphD9T:APA91bGNRYWFykkcHrzOvWQ88WMsaHSJF2LFONDGkXtfB0oDUhG_0VtkhBZ6L8P4WWqZsIJiOxr2TTcE2589e4QLvMNupql-GgRa7GJQJzjvR8QqGdtT3pG_OPeNjIXZhEwbrX339osZ";
		String registrationToken = "deeVWBr4Kke7YrYlE6PpzQ:APA91bHkVU-7_7jLs6S1LmprOR7rI06j-ePNZTSUBGMQOdfE2KDzaahp6q5gYwAyBNn8EiV5MK-R90jQ9PV33DbBlwaJ2ohpOZbyJC9RgMawaWIiNcIEOsFbFKHpTaWWV1_CyQrMfF6E";

		// See documentation on defining a message payload.
		Message message = Message.builder()
//		    .putData("score", "850")
//		    .putData("time", "2:45")
		    .setToken(registrationToken)
		    .setNotification(Notification.builder().setTitle("PROVA").setBody("Ciao ciao!").build())
		    .setWebpushConfig(WebpushConfig.builder()
//		    		.setNotification(WebpushNotification.builder()
//		    			.setTitle("PROVA")
//		    			.setBody("Ciao ciao!")
//		    			.build()
//		    		)
		    		.setFcmOptions(WebpushFcmOptions.builder()
		    				.setLink("https://unicam-turns-app.giulioquaresima.eu/tabs/tab2").build())
		    		.build())
		    .build();

		// Send a message to the device corresponding to the provided
		// registration token.
		String response;
		try
		{
			Thread.sleep(5000);
			response = FirebaseMessaging.getInstance(_self.firebaseApp).send(message);
			// Response is a message ID string.
			System.out.println("Successfully sent message: " + response);
		}
		catch (FirebaseMessagingException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
