package eu.giulioquaresima.unicam.turns.test.integration.jpa;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Clock;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.random.RandomGenerator;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

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
		TicketDispenser ticketDispenser = new TicketDispenser("Farmacia Comunale AFAS n. 1");
		ticketDispenser = ticketDispenserRepository.save(ticketDispenser);
		assertThat(ticketDispenser).isNotNull();
		assertThat(ticketDispenser.getId()).isNotNull();
		
		Clock clock = Clock.systemUTC();
		
		Session session = ticketDispenser.createSession();
		session = sessionRepository.save(session);
		
		assertThat(session).isNotNull();
		assertThat(session.getId()).isNotNull();
		assertThat(session.getStartTime()).isNull();
		assertThat(!session.isOpenNow(clock));
		assertThat(session.withraw(clock)).isNull();
		assertThat(session.draw(clock)).isNull();
		
		session.startNow(clock);
		
		assertThat(session.isOpenNow(clock));
		assertThat(session.getStartTime()).isNotNull();
		assertThat(session.getEndTime()).isNull();
		assertThat(session.getTickets()).isEmpty();
		assertThat(session.getCurrentTicketIndex()).isEqualTo(-1);
		assertThat(session.draw(clock)).isNull();
		
		Ticket ticket = session.withraw(clock);
		assertThat(ticket).isNotNull();
		assertThat(ticket.getUniqueIdentifier()).isNotNull();
		assertThat(ticket.getWithrawTime()).isNotNull();
		assertThat(session.getTickets()).size().isEqualTo(1);
		assertThat(ticket.getHumanFriendlyNumber()).isEqualTo((long) session.getTickets().size());
		assertThat(session.getCurrentTicketIndex()).isEqualTo(-1);
		
		Ticket drawnTicket = session.draw(clock);
		assertThat(drawnTicket).isEqualTo(ticket);
		assertThat(session.draw(clock)).isNull();
		
		assertThat(session.withraw(clock)).isNotNull();
		assertThat(session.draw(clock)).isNotNull();
		assertThat(session.withraw(clock)).isNotNull();
		
		session.endNow(clock);
		
		assertThat(session.withraw(clock)).isNull();
		assertThat(session.draw(clock)).isNull();
		assertThat(session.getCurrentTicketIndex()).isEqualTo(1);
		assertThat(session.getTickets()).size().isEqualTo(3);
	}
	
	@Test
	public void testUuidPersistenceConsistency()
	{
		TicketDispenser ticketDispenser = new TicketDispenser("Farmacia Comunale AFAS n. 1");
		ticketDispenser = ticketDispenserRepository.save(ticketDispenser);
		
		Clock clock = Clock.systemUTC();
		
		Session session = ticketDispenser.createSession();
		session.startNow(clock);
		session = sessionRepository.save(session);
		long sessionId = session.getId();
		
		Ticket ticket = session.withraw(clock);
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
		TicketDispenser ticketDispenser = new TicketDispenser("Farmacia Comunale AFAS n. 1");
		ticketDispenser = ticketDispenserRepository.save(ticketDispenser);
		
		Clock clock = Clock.systemUTC();
		RandomGenerator randomGenerator = new Random(-1873867143071539904L); // Same seed, repeatable test
		
		Session session = ticketDispenser.createSession();
		session.startNow(clock);
		session = sessionRepository.save(session);
		
		Set<UUID> withrawnUuids = new HashSet<>();
		
		int countWithrawals = 0;
		int countDraws = 0;
		
		// Withraw some tickets, the office is closed yet.
		for (int count = 0; count < 1024 * 1024; count++)
		{
			Ticket ticket = session.withraw(clock);
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
				ticket = session.withraw(clock);
				withrawnUuids.add(ticket.getUniqueIdentifier());
				countWithrawals++;
			}
			else // 0.75 likelihood
			{
				ticket = session.draw(clock);
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
			Ticket ticket = session.draw(clock);
			assertThat(ticket).isNotNull();
			assertThat(withrawnUuids.remove(ticket.getUniqueIdentifier()));
			countDraws++;
		}
		
		// There are no other numbers to call (i.e. tickets to draw),
		// the office can close.
		assertThat(session.draw(clock)).isNull();
		assertThat(withrawnUuids.isEmpty());
	}
	
}
