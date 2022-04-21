package eu.giulioquaresima.unicam.turns.security;

import org.springframework.lang.Nullable;
import org.springframework.security.core.Authentication;

import eu.giulioquaresima.unicam.turns.domain.entities.User;

/**
 * @param <E>
 * 
 * @author Giulio Quaresima (giulio.quaresima--at--gmail.com)
 */
public interface EntityPermissionEvaluator<E>
{
	/**
	 * @param entity
	 * @return <code>true</code> iff this permission evaluator applies to the entity.
	 */
	boolean supports(E entity);
	
	/**
	 * Evaluate the required permission.
	 * 
	 * @param authentication
	 *  
	 * @param user If {@code authentication != null}, the user 
	 * who has {@link User#getUsername()} equal to {@link Authentication#getName()},
	 * if exists.
	 * 
	 * @param entity
	 * 
	 * @param requiredPermission
	 * 
	 * @return
	 */
	public boolean hasPermission(
			@Nullable Authentication authentication, 
			@Nullable User user, 
			E entity, 
			Object requiredPermission
			);
}
