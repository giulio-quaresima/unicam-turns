package eu.giulioquaresima.unicam.turns.domain.service;

import java.util.List;

import eu.giulioquaresima.unicam.turns.domain.entities.Session;
import eu.giulioquaresima.unicam.turns.domain.entities.Ticket;
import eu.giulioquaresima.unicam.turns.domain.entities.TicketDispenser;
import eu.giulioquaresima.unicam.turns.domain.entities.User;

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
	
	/**
	 * Draw the next ticket of the currently active session, if any.
	 * 
	 * @param ticketDispenser
	 * 
	 * @return The next ticket, <code>null</code> if there is no open session,
	 * or if there are no more withdrew ticket in the dispenser.
	 */
	Ticket draw(TicketDispenser ticketDispenser);
	
	/**
	 * Withdraw the next ticket of the currently active session, if any.
	 * 
	 * @param ticketDispenser
	 * 
	 * @return The next ticket, <code>null</code> if there is no open session.
	 */
	Ticket withdraw(TicketDispenser ticketDispenser);
	
	/**
	 * Find the current {@link User}'s ticket in the open session
	 * of the dispenser, if any.
	 * 
	 * @param ticketDispenser
	 * 
	 * @return The last ticket of the current user, or <code>null</code>
	 * if there is no open session or the current user hasn't withdraw any ticket yet.
	 */
	Ticket currentUserTicket(TicketDispenser ticketDispenser);
	
}
