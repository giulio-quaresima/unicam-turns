package eu.giulioquaresima.unicam.turns.repository;

import java.util.List;

import eu.giulioquaresima.unicam.turns.domain.entities.Ticket;
import eu.giulioquaresima.unicam.turns.domain.entities.User;

public interface TicketRepository extends LayerSupertype<Ticket>
{

	List<Ticket> findAllByOwnerAndCancelledOrderByWithdrawTimeDesc(User owner, boolean cancelled);

}
