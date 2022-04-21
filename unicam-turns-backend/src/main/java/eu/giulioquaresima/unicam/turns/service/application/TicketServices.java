package eu.giulioquaresima.unicam.turns.service.application;

import eu.giulioquaresima.unicam.turns.domain.entities.Ticket;
import eu.giulioquaresima.unicam.turns.domain.entities.TicketDispenser;

public interface TicketServices
{
	/**
	 * Withdraw a new ticket from the currently active session
	 * of the dispenser, if any.
	 * 
	 * @param ticketDispenser
	 * 
	 * @return the new ticket, or <code>null</code> if there
	 * is no currently active session.
	 */
	Ticket withdraw(TicketDispenser ticketDispenser);
}
