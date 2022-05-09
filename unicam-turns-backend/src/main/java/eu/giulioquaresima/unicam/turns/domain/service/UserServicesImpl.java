package eu.giulioquaresima.unicam.turns.domain.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import eu.giulioquaresima.unicam.turns.domain.entities.FirebaseToken;
import eu.giulioquaresima.unicam.turns.domain.entities.User;
import eu.giulioquaresima.unicam.turns.repository.FirebaseTokenRepository;
import eu.giulioquaresima.unicam.turns.repository.UserRepository;

/**
 * 
 * 
 * @author Giulio Quaresima (giulio.quaresima--at--gmail.com)
 */
@Service
@Transactional (readOnly = false, isolation = Isolation.SERIALIZABLE)
public class UserServicesImpl implements UserServices
{
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private FirebaseTokenRepository firebaseTokenRepository;

	@Override
	public User getCurrentUser(boolean createIfAbsent)
	{
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null && authentication.isAuthenticated())
		{
			Optional<User> optionalUser = userRepository.findOneByUsername(authentication.getName());
			if (optionalUser.isPresent())
			{
				return optionalUser.get();
			}
			if (createIfAbsent)
			{
				User user = new User();
				user.setUsername(authentication.getName());
				return userRepository.saveAndFlush(user);
			}
		}
		return null;
	}

	@Override
	public FirebaseToken assignTokenToCurrentUser(FirebaseToken firebaseToken)
	{
		Assert.notNull(firebaseToken, "firebaseToken");
		Assert.hasText(firebaseToken.getToken(), "Un token vuoto che token è?");
		Assert.hasText(firebaseToken.getOrigin(), "empty firebaseToken.origin");
		Assert.isNull(firebaseToken.getId(), "Only transient instance!");
		
		User user = getCurrentUser(true);
		if (user != null)
		{
			Optional<FirebaseToken> optional = firebaseTokenRepository.findOneByToken(firebaseToken.getToken());
			if (optional.isPresent())
			{
				// Se il token era già presente, sovrascrive l'origine
				optional.get().setOrigin(firebaseToken.getOrigin());
			}
			else
			{
				optional = Optional.of(firebaseToken);
			}
			// In ogni caso, scrive o sovrascrive l'utente e aggiorna la data
			optional.get().setUser(user);
			optional.get().setToggleTime(LocalDateTime.now());
			return firebaseTokenRepository.save(optional.get());
		}
		return null;
	}

}
