package eu.giulioquaresima.unicam.turns.security;

import java.io.Serializable;
import java.util.List;
import java.util.function.Predicate;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ClassUtils;

import eu.giulioquaresima.unicam.turns.domain.entities.AbstractEntity;
import eu.giulioquaresima.unicam.turns.domain.entities.User;
import eu.giulioquaresima.unicam.turns.repository.UserRepository;

@Service
public class PermissionEvaluatorImpl implements PermissionEvaluator
{
	private static final Logger LOGGER = LoggerFactory.getLogger(PermissionEvaluatorImpl.class);
	
	@PersistenceContext
	private EntityManager entityManager;
	
	@Autowired
	private UserRepository userRepository;
	
	@SuppressWarnings("rawtypes")
	@Autowired
	private List<EntityPermissionEvaluator> entityPermissionEvaluators;

	@Override
	public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission)
	{
		final User user = from(authentication);
		final Predicate<EntityPermissionEvaluator> filter = filter(authentication, user, targetDomainObject, permission);
		return entityPermissionEvaluators.stream().anyMatch(filter);
	}

	private User from(Authentication authentication)
	{
		if (authentication != null)
		{
			return userRepository.findOneByUsername(authentication.getName()).orElse(null);
		}
		return null;
	}

	private Predicate<EntityPermissionEvaluator> filter(Authentication authentication, User user, Object targetDomainObject, Object permission)
	{
		return entityPermissionEvaluator -> {
			return 
					entityPermissionEvaluator.supports(targetDomainObject)
					&&
					entityPermissionEvaluator.hasPermission(authentication, user, targetDomainObject, permission)
					;
		};
	}

	@Override
	@Transactional (readOnly = true, propagation = Propagation.SUPPORTS)
	public boolean hasPermission(Authentication authentication, Serializable targetId, String targetTypeStr, Object permission)
	{
		if (targetId != null && targetTypeStr != null)
		{
			Integer id = null;
			if (targetId instanceof Number)
			{
				if (targetId instanceof Integer)
				{
					id = (Integer) targetId;
				}
				else
				{
					id = ((Number) targetId).intValue();
				}
			}
			else 
			{
				String targetIdStr = targetId.toString().trim();
				if (targetIdStr.matches("\\d+"))
				{
					id = Integer.parseInt(targetIdStr);
				}
			}
			if (id != null)
			{
				try
				{
					Class<?> targetType = ClassUtils.forName(targetTypeStr, null);
					if (AbstractEntity.class.isAssignableFrom(targetType))
					{
						Object targetDomainObject = entityManager.find(targetType, id);
						if (targetDomainObject != null)
						{
							return hasPermission(authentication, targetDomainObject, permission);
						}
					}
				}
				catch (ClassNotFoundException | LinkageError e)
				{
					LOGGER.error(e.getMessage(), e);
				}
			}
		}
		
		return false;
	}

}
