package eu.giulioquaresima.unicam.turns.service.infrastructure;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;

@Service
public class FirebaseServicesImpl implements FirebaseServices, InitializingBean
{
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
			path = Paths.get(System.getProperty("user.home"), "serviceAccountKey.json");
			if (!Files.isRegularFile(path))
			{
				path = Paths.get(System.getProperty("user.dir"), "serviceAccountKey.json");
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
		new FirebaseServicesImpl().afterPropertiesSet();
		
		// This registration token comes from the client FCM SDKs.
		String registrationToken = "dmyXLV2k_W2-NLIWqphD9T:APA91bGNRYWFykkcHrzOvWQ88WMsaHSJF2LFONDGkXtfB0oDUhG_0VtkhBZ6L8P4WWqZsIJiOxr2TTcE2589e4QLvMNupql-GgRa7GJQJzjvR8QqGdtT3pG_OPeNjIXZhEwbrX339osZ";

		// See documentation on defining a message payload.
		Message message = Message.builder()
//		    .putData("score", "850")
//		    .putData("time", "2:45")
		    .setToken(registrationToken)
		    .setNotification(Notification.builder().setTitle("PROVA").setBody("Ciao ciao!").build())
		    .build();

		// Send a message to the device corresponding to the provided
		// registration token.
		String response;
		try
		{
			response = FirebaseMessaging.getInstance().send(message);
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
