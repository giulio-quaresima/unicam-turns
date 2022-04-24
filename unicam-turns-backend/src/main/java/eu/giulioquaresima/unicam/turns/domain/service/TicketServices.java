package eu.giulioquaresima.unicam.turns.domain.service;

import java.util.List;

import eu.giulioquaresima.unicam.turns.domain.entities.Ticket;

/**
 * 
 * 
 * @author Giulio Quaresima (giulio.quaresima--at--gmail.com)
 */
public interface TicketServices
{
	/**
	 * @return The list of {@link Ticket#isAlive()} tickets
	 * of the current user, ordered by {@link Ticket#getWithdrawTime()},
	 * reversed.
	 */
	List<Ticket> currentUserTickets();
}
