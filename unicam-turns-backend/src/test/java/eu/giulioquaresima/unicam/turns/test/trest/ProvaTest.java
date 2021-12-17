package eu.giulioquaresima.unicam.turns.test.trest;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import eu.giulioquaresima.unicam.turns.domain.entities.Session;

@DataJpaTest
public class ProvaTest extends AbstractTest
{
	@Test
	public void testTicketWithdraw()
	{
		Session session = createSequentialDecimalSession();
		session.withraw(null);
		session.withraw(null);
		session.withraw(null);
		session = sessionRepository.save(session);
		assertThat(ticketRepository.findAll()).size().isEqualTo(3);
	}
}
