package eu.giulioquaresima.unicam.turns.service.infrastructure;

import com.google.firebase.messaging.FirebaseMessagingException;

import eu.giulioquaresima.unicam.turns.domain.entities.Ticket;

public interface FirebaseServices
{
	boolean yourTicketCalled(Ticket ticket) throws FirebaseMessagingException;
}
