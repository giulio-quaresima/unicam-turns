package eu.giulioquaresima.unicam.turns.test.integration.jpa;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.ZoneId;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.random.RandomGenerator;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import eu.giulioquaresima.unicam.turns.domain.entities.Owner;
import eu.giulioquaresima.unicam.turns.domain.entities.Session;
import eu.giulioquaresima.unicam.turns.domain.entities.Ticket;
import eu.giulioquaresima.unicam.turns.domain.entities.TicketDispenser;

@DataJpaTest
public class TestTickets extends AbstractTest
{
	@PersistenceContext
	private EntityManager entityManager;
	
	@Test
	public void testSimpleTicketWithrawal()
	{
		TicketDispenser ticketDispenser = ticketDispenserRepository.save(
				new TicketDispenser(
						"Farmacia Comunale AFAS n. 1",
						ZoneId.systemDefault(), 
						ownerRepository.save(new Owner("Farmacia Comunale AFAS n. 1"))));
		assertThat(ticketDispenser).isNotNull();
		assertThat(ticketDispenser.getId()).isNotNull();
		
		Session session = ticketDispenser.createSession();
		session = sessionRepository.save(session);
		
		assertThat(session).isNotNull();
		assertThat(session.getId()).isNotNull();
		assertThat(session.getStartTime()).isNull();
		assertThat(!session.isOpen());
		assertThat(session.withdraw()).isNull();
		assertThat(session.draw()).isNull();
		
		session.start();
		
		assertThat(session.isOpen());
		assertThat(session.getStartTime()).isNotNull();
		assertThat(session.getEndTime()).isNull();
		assertThat(session.getTickets()).isEmpty();
		assertThat(session.getCurrentTicketIndex()).isEqualTo(-1);
		assertThat(session.draw()).isNull();
		
		Ticket ticket = session.withdraw();
		assertThat(ticket).isNotNull();
		assertThat(ticket.getUniqueIdentifier()).isNotNull();
		assertThat(ticket.getWithdrawTime()).isNotNull();
		assertThat(session.getTickets()).size().isEqualTo(1);
		assertThat(ticket.getPublicNumber()).isEqualTo((long) session.getTickets().size());
		assertThat(session.getCurrentTicketIndex()).isEqualTo(-1);
		assertThat(session.getLastDrewTicket()).isNull();
		
		Ticket drawnTicket = session.draw();
		assertThat(drawnTicket).isEqualTo(ticket);
		assertThat(drawnTicket).isEqualTo(session.getLastDrewTicket());
		assertThat(session.draw()).isNull();
		assertThat(session.getLastDrewTicket()).isNotNull();
		assertThat(drawnTicket).isEqualTo(session.getLastDrewTicket());
		
		assertThat(session.withdraw()).isNotNull();
		assertThat(session.draw()).isNotNull();
		assertThat(session.withdraw()).isNotNull();
		
		session.end();
		
		assertThat(session.withdraw()).isNull();
		assertThat(session.draw()).isNull();
		assertThat(session.getCurrentTicketIndex()).isEqualTo(1);
		assertThat(session.getTickets()).size().isEqualTo(3);
	}
	
	@Test
	public void testUuidPersistenceConsistency()
	{
		TicketDispenser ticketDispenser = ticketDispenserRepository.save(
				new TicketDispenser(
						"Farmacia Comunale AFAS n. 1",
						ZoneId.systemDefault(), 
						ownerRepository.save(new Owner("Farmacia Comunale AFAS n. 1"))));
		
		Session session = ticketDispenser.createSession();
		session.start();
		session = sessionRepository.save(session);
		long sessionId = session.getId();
		
		Ticket ticket = session.withdraw();
		UUID uuid = ticket.getUniqueIdentifier();
		
		entityManager.flush();
		entityManager.detach(session);
		
		Session sessionNewRef = sessionRepository.getById(sessionId);
		assertThat(sessionNewRef).isNotNull();
		assertThat(sessionNewRef != session).isTrue();
		assertThat(sessionNewRef.getId()).isEqualTo(sessionId);
		assertThat(sessionNewRef.getTickets()).size().isEqualTo(1);
		assertThat(sessionNewRef.getTickets().get(0).getUniqueIdentifier()).isEqualTo(uuid);
	}
	
	@Test
	public void testTicketDrawConsistency()
	{
		TicketDispenser ticketDispenser = ticketDispenserRepository.save(
				new TicketDispenser(
						"Farmacia Comunale AFAS n. 1",
						ZoneId.systemDefault(), 
						ownerRepository.save(new Owner("Farmacia Comunale AFAS n. 1"))));
		
		RandomGenerator randomGenerator = new Random(-1873867143071539904L); // Same seed, repeatable test
		
		Session session = ticketDispenser.createSession();
		session.start();
		session = sessionRepository.save(session);
		
		Set<UUID> withrawnUuids = new HashSet<>();
		
		int countWithrawals = 0;
		int countDraws = 0;
		
		// Withraw some tickets, the office is closed yet.
		for (int count = 0; count < 1024 * 1024; count++)
		{
			Ticket ticket = session.withdraw();
			withrawnUuids.add(ticket.getUniqueIdentifier());
			countWithrawals++;
			assertThat(countWithrawals).isEqualTo(withrawnUuids.size());
		}
		
		// The office opens and start to call numbers (i.e. to draw tickets),
		// while people continue to arrive and withraw new tickets
		for (int count = 0; count < 2 * 1024 * 1024; count++)
		{
			Ticket ticket;
			if (randomGenerator.nextBoolean() && randomGenerator.nextBoolean()) // 0.25 likelihood
			{
				ticket = session.withdraw();
				withrawnUuids.add(ticket.getUniqueIdentifier());
				countWithrawals++;
			}
			else // 0.75 likelihood
			{
				ticket = session.draw();
				if (countDraws < countWithrawals)
				{
					assertThat(ticket).isNotNull();
					assertThat(withrawnUuids.remove(ticket.getUniqueIdentifier()));
					countDraws++;
				}
				else
				{
					assertThat(ticket).isNull();
				}
			}
		}
		
		// The office continue to call numbers (i.e. to draw tickets),
		// and no new people withraw new tickets
		while (countDraws < countWithrawals)
		{
			Ticket ticket = session.draw();
			assertThat(ticket).isNotNull();
			assertThat(withrawnUuids.remove(ticket.getUniqueIdentifier()));
			countDraws++;
		}
		
		// There are no other numbers to call (i.e. tickets to draw),
		// the office can close.
		assertThat(session.draw()).isNull();
		assertThat(withrawnUuids.isEmpty());
	}
	
}
