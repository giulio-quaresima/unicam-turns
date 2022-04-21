package eu.giulioquaresima.unicam.turns.service.application;

import java.time.Clock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import eu.giulioquaresima.unicam.turns.domain.entities.Session;
import eu.giulioquaresima.unicam.turns.domain.entities.Ticket;
import eu.giulioquaresima.unicam.turns.domain.entities.TicketDispenser;
import eu.giulioquaresima.unicam.turns.domain.entities.User;
import eu.giulioquaresima.unicam.turns.service.infrastructure.TimeServices;

@Service
public class TicketServicesImpl implements TicketServices
{
	@Autowired
	private UserServices userServices;
	
	@Autowired
	private TimeServices timeServices;

	@Override
	@Transactional (readOnly = false, propagation = Propagation.REQUIRED)
	public Ticket withdraw(TicketDispenser ticketDispenser)
	{
		Assert.notNull(ticketDispenser, "ticketDispenser is required");
		
		Clock clock = timeServices.getSystemClock();
		Session session = ticketDispenser.getCurrentSession(clock);
		if (session != null)
		{
			User user = userServices.getCurrentUser(true);
			return session.withdraw(clock, user);
		}
		
		return null;
	}

}
