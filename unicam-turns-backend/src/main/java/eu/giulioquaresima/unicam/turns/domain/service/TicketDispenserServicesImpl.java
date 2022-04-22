package eu.giulioquaresima.unicam.turns.domain.service;

import java.time.Clock;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import eu.giulioquaresima.unicam.turns.domain.entities.Owner;
import eu.giulioquaresima.unicam.turns.domain.entities.Session;
import eu.giulioquaresima.unicam.turns.domain.entities.Ticket;
import eu.giulioquaresima.unicam.turns.domain.entities.TicketDispenser;
import eu.giulioquaresima.unicam.turns.domain.entities.User;
import eu.giulioquaresima.unicam.turns.repository.OwnerRepository;
import eu.giulioquaresima.unicam.turns.repository.SessionRepository;
import eu.giulioquaresima.unicam.turns.repository.TicketDispenserRepository;
import eu.giulioquaresima.unicam.turns.repository.UserRepository;
import eu.giulioquaresima.unicam.turns.service.infrastructure.TimeServices;

@Service
@Transactional (readOnly = true, propagation = Propagation.SUPPORTS)
public class TicketDispenserServicesImpl implements TicketDispenserServices
{
	@Autowired
	private TicketDispenserRepository ticketDispenserRepository;
	
	@Autowired
	private OwnerRepository ownerRepository;
	
	@Autowired
	private SessionRepository sessionRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private UserServices userServices;
	
	@Autowired
	private TimeServices timeServices;

	@Override
	public List<TicketDispenser> listOwnDispensers()
	{
		User user = userServices.getCurrentUser(true);
		
		if (user != null)
		{
			return user
					.getOwners()
					.stream()
					.flatMap(owner -> owner.getTicketDispensers().stream())
					.sorted()
					.collect(Collectors.toList())
					;
		}
		
		return Collections.emptyList();
	}

	@Override
	@Transactional (readOnly = false, propagation = Propagation.REQUIRED)
	public TicketDispenser create(TicketDispenser template) throws IllegalArgumentException, IllegalStateException
	{
		Assert.notNull(template, "template required");
		Assert.hasText(template.getLabel(), "template.label required");
		
		if (template.getOwner() == null)
		{
			template.setOwner(null);
			User user = userServices.getCurrentUser(true);
			if (user != null)
			{
				/* 
				 * Per ora semplifico creando automaticamente uno ed un solo Owner per ogni User,
				 * o utilizzando un Owner a caso se già presente.
				 */
				Owner owner;
				if (user.getOwners().isEmpty())
				{
					owner = ownerRepository.save(new Owner(template.getLabel()));
					user.getOwners().add(owner);
					userRepository.save(user);
				}
				else
				{
					owner = user.getOwners().stream().findAny().orElseThrow(AssertionError::new);
				}
				template.setOwner(owner);
				return ticketDispenserRepository.save(template);
			}
		}
		
		return null;
	}

	@Override
	@Transactional (readOnly = false, propagation = Propagation.REQUIRED)
	public Session start(TicketDispenser ticketDispenser)
	{
		Assert.notNull(ticketDispenser, "ticketDispenser required");
		
		Clock clock = timeServices.getSystemClock();
		Session session = getCurrentSession(ticketDispenser, clock);
		
		if (session == null)
		{
			session = ticketDispenser.createSession();
			session.startNow(clock);
			session = sessionRepository.save(session);
		}
		
		return session;
	}

	@Override
	@Transactional (readOnly = false, propagation = Propagation.REQUIRED)
	public Session stop(TicketDispenser ticketDispenser)
	{
		Assert.notNull(ticketDispenser, "ticketDispenser required");

		Clock clock = timeServices.getSystemClock();
		Session session = getCurrentSession(ticketDispenser, clock);
		
		if (session != null)
		{
			session.endNow(clock);
			session = sessionRepository.save(session);
		}
		
		return session;
	}

	@Override
	@Transactional (readOnly = false, propagation = Propagation.REQUIRED)
	public Ticket draw(TicketDispenser ticketDispenser)
	{
		Assert.notNull(ticketDispenser, "ticketDispenser required");

		Clock clock = timeServices.getSystemClock();
		Session session = getCurrentSession(ticketDispenser, clock);
		
		if (session != null)
		{
			Ticket ticket = session.draw(clock);
			session = sessionRepository.save(session);
			return ticket;
		}
		
		return null;
	}
	
	@Override
	public Ticket current(TicketDispenser ticketDispenser)
	{
		Assert.notNull(ticketDispenser, "ticketDispenser required");

		Clock clock = timeServices.getSystemClock();
		Session session = getCurrentSession(ticketDispenser, clock);
		
		if (session != null)
		{
			return session.currentTicket(clock);
		}
		
		return null;
	}

	protected Session getCurrentSession(TicketDispenser ticketDispenser, Clock clock)
	{
		/* TODO
		 * Quando le sessioni diverranno nell'ordine delle migliaia 
		 * sarebbe opportuno ottimizzare con una query, ma ora non
		 * è il caso.
		 */
		return ticketDispenser.getCurrentSession(clock);
	}

}
