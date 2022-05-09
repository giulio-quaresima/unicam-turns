package eu.giulioquaresima.unicam.turns.domain.service;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import eu.giulioquaresima.unicam.turns.domain.entities.Ticket;
import eu.giulioquaresima.unicam.turns.domain.entities.User;
import eu.giulioquaresima.unicam.turns.repository.TicketRepository;

/**
 * 
 * 
 * @author Giulio Quaresima (giulio.quaresima--at--gmail.com)
 */
@Service
@Transactional (readOnly = false, isolation = Isolation.SERIALIZABLE)
public class TicketServicesImpl implements TicketServices
{
	@Autowired
	private TicketRepository ticketRepository;
	
	@Autowired
	private UserServices userServices;
	
	@Override
	public List<Ticket> currentUserTickets()
	{
		User user = userServices.getCurrentUser(false);
		
		if (user != null)
		{
			return ticketRepository.findAllByOwnerAndCancelledOrderByWithdrawTimeDesc(user, false);
		}
		
		return Collections.emptyList();
	}

}
