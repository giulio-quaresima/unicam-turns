package eu.giulioquaresima.unicam.turns.domain.service;

import java.util.List;

import eu.giulioquaresima.unicam.turns.domain.entities.Session;
import eu.giulioquaresima.unicam.turns.domain.entities.TicketDispenser;

/**
 * 
 * 
 * @author Giulio Quaresima (giulio.quaresima--at--gmail.com)
 */
public interface TicketDispenserServices
{
	/**
	 * @return The list of the dispensers of the current user,
	 * empty list if no user or no dispensers.
	 */
	List<TicketDispenser> listOwnDispensers();
	
	/**
	 * Create a new TicketDispenser based on the template,
	 * with {@link TicketDispenser#getOwner()} corresponding
	 * to the current authenticated user if the owner
	 * in the template is <code>null</code>.
	 * 
	 * @param template
	 * 
	 * @return The new dispenser.
	 * 
	 * @throws IllegalArgumentException 
	 * 
	 * @throws IllegalStateException
	 */
	TicketDispenser create(TicketDispenser template) throws IllegalArgumentException, IllegalStateException;
	
	/**
	 * Start a new session now, NOOP if there is already
	 * an active session for the dispenser.
	 * 
	 * @param ticketDispenser
	 * 
	 * @return The started session.
	 */
	Session start(TicketDispenser ticketDispenser);
	
	/**
	 * If there is an active session for the dispenser,
	 * stops it, otherwise NOOP.
	 * 
	 * @param ticketDispenser
	 * 
	 * @return The stopped session, or <code>null</code> if there wasn't
	 * an active session.
	 */
	Session stop(TicketDispenser ticketDispenser);
	
}
