package eu.giulioquaresima.unicam.turns.service.infrastructure;

import com.google.firebase.messaging.FirebaseMessagingException;

import eu.giulioquaresima.unicam.turns.domain.entities.Ticket;
import eu.giulioquaresima.unicam.turns.domain.entities.TicketDispenser;

/**
 * 
 * 
 * @author Giulio Quaresima (giulio.quaresima--at--gmail.com)
 */
public interface FirebaseServices
{
	/**
	 * Notify all the "stakeholders" that this dispenser
	 * is changed: the "stakeholders" are {@link TicketDispenser#getOwner()}
	 * and each waiting {@link Ticket#getOwner()}; the UI
	 * should refresh all the interested pages when receives such a message.
	 * @param ticketDispenserId TODO
	 * 
	 * @throws FirebaseMessagingException 
	 */
	void toggle(long ticketDispenserId) throws FirebaseMessagingException;
	
	/**
	 * Notify the ticket's owner that its ticket
	 * has been drew!
	 * @param ticketId TODO
	 * 
	 * @throws FirebaseMessagingException
	 */
	void yourTicketCalled(long ticketId) throws FirebaseMessagingException;
}
