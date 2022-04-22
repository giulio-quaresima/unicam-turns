package eu.giulioquaresima.unicam.turns.domain.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import eu.giulioquaresima.unicam.turns.domain.entities.Session;
import eu.giulioquaresima.unicam.turns.domain.entities.Ticket;
import eu.giulioquaresima.unicam.turns.domain.entities.TicketDispenser;
import eu.giulioquaresima.unicam.turns.domain.entities.User;

/**
 * 
 * 
 * @author Giulio Quaresima (giulio.quaresima--at--gmail.com)
 */
@Service
public class TicketServicesImpl implements TicketServices
{
	@Autowired
	private UserServices userServices;
	
	@Override
	@Transactional (readOnly = false, propagation = Propagation.REQUIRED)
	public Ticket withdraw(TicketDispenser ticketDispenser)
	{
		Assert.notNull(ticketDispenser, "ticketDispenser is required");
		
		Session session = ticketDispenser.getCurrentSession();
		if (session != null)
		{
			User user = userServices.getCurrentUser(true);
			return session.withdraw(user);
		}
		
		return null;
	}

}
