package eu.giulioquaresima.unicam.turns.domain.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import eu.giulioquaresima.unicam.turns.domain.entities.User;
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

}
