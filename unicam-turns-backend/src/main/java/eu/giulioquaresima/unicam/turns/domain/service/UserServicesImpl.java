package eu.giulioquaresima.unicam.turns.domain.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
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
public class UserServicesImpl implements UserServices
{
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private FirebaseTokenRepository firebaseTokenRepository;

	@Override
	@Transactional (readOnly = false, propagation = Propagation.REQUIRED)
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
				return userRepository.save(user);
			}
		}
		return null;
	}

	@Override
	@Transactional (readOnly = false, propagation = Propagation.REQUIRED)
	public FirebaseToken assignTokenToCurrentUser(String token)
	{
		Assert.hasText(token, "Un token vuoto che token è?");
		
		User user = getCurrentUser(true);
		if (user != null)
		{
			Optional<FirebaseToken> optional = firebaseTokenRepository.findOneByToken(token);
			if (optional.isPresent())
			{
				optional.get().setUser(user);
			}
			else
			{
				FirebaseToken firebaseToken = new FirebaseToken();
				firebaseToken.setToken(token);
				firebaseToken.setUser(user);
				optional = Optional.of(firebaseToken);
			}
			return firebaseTokenRepository.save(optional.get());
		}
		return null;
	}

}
