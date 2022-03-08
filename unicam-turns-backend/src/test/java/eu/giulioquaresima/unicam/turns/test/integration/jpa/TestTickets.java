package eu.giulioquaresima.unicam.turns.test.integration.jpa;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import eu.giulioquaresima.unicam.turns.domain.entities.Session;
import eu.giulioquaresima.unicam.turns.domain.entities.Ticket;
import eu.giulioquaresima.unicam.turns.domain.entities.TicketDispenser;

@DataJpaTest
public class TestTickets extends AbstractTest
{
	@Test
	public void testTicketWithraw()
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
		assertThat(session.getCurrentTicket()).isEqualTo(-1);
		
		Ticket ticket = session.withraw(LocalDateTime.now());
		assertThat(ticket).isNotNull();
		assertThat(ticket.getUniqueIdentifier()).isNotNull();
		assertThat(ticket.getWithrawTime()).isNotNull();
		assertThat(session.getTickets()).size().isEqualTo(1);
		assertThat(session.getCurrentTicket()).isEqualTo(0);
		
	}
	
	@Test
	public void testTicketDraw()
	{
		
	}
}
