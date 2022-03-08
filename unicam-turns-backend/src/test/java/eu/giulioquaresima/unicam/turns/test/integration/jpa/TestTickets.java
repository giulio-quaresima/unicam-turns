package eu.giulioquaresima.unicam.turns.test.integration.jpa;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.UUID;

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
		TicketDispenser ticketDispenser = new TicketDispenser();
		ticketDispenser = ticketDispenserRepository.save(ticketDispenser);
		assertThat(ticketDispenser).isNotNull();
		assertThat(ticketDispenser.getId()).isNotNull();
		
		Session session = new Session();
		session.setTicketDispenser(ticketDispenser);
		session.setStartTime(LocalDateTime.now());
		session = sessionRepository.save(session);
		assertThat(session).isNotNull();
		assertThat(session.getId()).isNotNull();
		assertThat(session.getTickets()).isEmpty();
		assertThat(session.getCurrentTicketIndex()).isEqualTo(-1);
		
		Ticket ticket = session.withraw(LocalDateTime.now());
		assertThat(ticket).isNotNull();
		assertThat(ticket.getUniqueIdentifier()).isNotNull();
		assertThat(ticket.getWithrawTime()).isNotNull();
		assertThat(session.getTickets()).size().isEqualTo(1);
		assertThat(session.getCurrentTicketIndex()).isEqualTo(-1);
	}
	
	@Test
	public void testUuidPersistenceConsistency()
	{
		TicketDispenser ticketDispenser = new TicketDispenser();
		ticketDispenser = ticketDispenserRepository.save(ticketDispenser);
		Session session = new Session();
		session.setTicketDispenser(ticketDispenser);
		session.setStartTime(LocalDateTime.now());
		session = sessionRepository.save(session);
		Ticket ticket = session.withraw(LocalDateTime.now());
		UUID uuid = ticket.getUniqueIdentifier();
		long sessionId = sessionRepository.save(session).getId();
		
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
	public void testTicketDraw()
	{
		
	}
}
