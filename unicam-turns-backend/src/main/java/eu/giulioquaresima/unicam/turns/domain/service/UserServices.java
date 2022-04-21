package eu.giulioquaresima.unicam.turns.domain.service;

import eu.giulioquaresima.unicam.turns.domain.entities.User;

/**
 * 
 * 
 * @author Giulio Quaresima (giulio.quaresima--at--gmail.com)
 */
public interface UserServices
{
	/**
	 * If there is a currently authenticated user, retrieve
	 * the user which {@link User#getUsername()} equals
	 * to the current user's username
	 * 
	 * @param createIfAbsent If <code>true</code> and the
	 * user does not exists in the DB, creates a new one.
	 * 
	 * @return The user, <code>null</code> if there is no currently
	 * authenticated user, or if she does not exists in the DB
	 * and {@code createIfAbsent} is false.
	 */
	User getCurrentUser(boolean createIfAbsent);
}
