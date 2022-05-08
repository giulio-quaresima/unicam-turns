package eu.giulioquaresima.unicam.turns.domain.service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import eu.giulioquaresima.unicam.turns.repository.TicketRepository;
import eu.giulioquaresima.unicam.turns.repository.UserRepository;
import eu.giulioquaresima.unicam.turns.service.infrastructure.FirebaseServices;

@Service
@Transactional (readOnly = true, propagation = Propagation.SUPPORTS)
public class TicketDispenserServicesImpl implements TicketDispenserServices
{
	private final static Logger LOGGER = LoggerFactory.getLogger(TicketDispenserServicesImpl.class);
	
	@Autowired
	private TicketDispenserRepository ticketDispenserRepository;
	
	@Autowired
	private OwnerRepository ownerRepository;
	
	@Autowired
	private SessionRepository sessionRepository;
	
	@Autowired
	private TicketRepository ticketRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private UserServices userServices;
	
	@Autowired
	private FirebaseServices firebaseServices;
	
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
		Assert.notNull(template.getZoneId(), "template.zoneId required");
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
		
		Session session = getCurrentSession(ticketDispenser);
		
		if (session == null)
		{
			session = ticketDispenser.createSession();
			session.start();
			session = sessionRepository.save(session);
			
			firebaseToggle(ticketDispenser);
		}
		
		return session;
	}

	protected void firebaseToggle(TicketDispenser ticketDispenser)
	{
		try
		{
			firebaseServices.toggle(ticketDispenser);
		}
		catch (Exception e) 
		{
			LOGGER.error("Errore nel tentativo di push notification", e);
		}
	}

	@Override
	@Transactional (readOnly = false, propagation = Propagation.REQUIRED)
	public Session stop(TicketDispenser ticketDispenser)
	{
		Assert.notNull(ticketDispenser, "ticketDispenser required");

		Session session = getCurrentSession(ticketDispenser);
		
		if (session != null)
		{
			session.end();
			session = sessionRepository.save(session);
			
			firebaseToggle(ticketDispenser);
		}
		
		return session;
	}

	@Override
	@Transactional (readOnly = false, propagation = Propagation.REQUIRED)
	public Ticket draw(TicketDispenser ticketDispenser)
	{
		Assert.notNull(ticketDispenser, "ticketDispenser required");

		Session session = getCurrentSession(ticketDispenser);
		
		if (session != null)
		{
			Ticket ticket = session.draw();
			session = sessionRepository.save(session);
			
			try
			{
				firebaseToggle(ticketDispenser);
				firebaseServices.yourTicketCalled(ticket);
			}
			catch (Exception e) 
			{
				LOGGER.error("Errore nel tentativo di push notification", e);
			}
			
			return ticket;
		}
		
		return null;
	}
	
	@Override
	@Transactional (readOnly = false, propagation = Propagation.REQUIRED)
	public Ticket withdraw(TicketDispenser ticketDispenser)
	{
		Assert.notNull(ticketDispenser, "ticketDispenser required");

		User user = userServices.getCurrentUser(true);
		Session session = getCurrentSession(ticketDispenser);
		
		if (session != null && session.isOpen())
		{
			Ticket ticket = ticketRepository.save(session.withdraw(user));
			firebaseToggle(ticketDispenser);
			return ticket;
		}
		
		return null;
	}

	@Override
	public Ticket currentUserTicket(TicketDispenser ticketDispenser)
	{
		Assert.notNull(ticketDispenser, "ticketDispenser required");

		User user = userServices.getCurrentUser(false);
		if (user != null)
		{
			Session session = getCurrentSession(ticketDispenser);
			if (session != null)
			{
				return session.findLastTicket(user, false);
			}
		}
		
		return null;
	}

	protected Session getCurrentSession(TicketDispenser ticketDispenser)
	{
		/* TODO
		 * Quando le sessioni diverranno nell'ordine delle migliaia 
		 * sarebbe opportuno ottimizzare con una query, ma ora non
		 * è il caso.
		 */
		return ticketDispenser.getCurrentSession();
	}

}
