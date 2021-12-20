package eu.giulioquaresima.unicam.turns.test.integration.jpa;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import eu.giulioquaresima.unicam.turns.domain.entities.ServiceReception;
import eu.giulioquaresima.unicam.turns.domain.entities.Session;

@DataJpaTest
public class TestTicketsDraw extends AbstractTest
{
	@Test
	public void testDrawInvariants()
	{
		for (Session session : List.of(createSequentialDecimalSession(), createSequentialBijectiveSession(), createSrambledSession()))
		{
			ServiceReception serviceReception = createServiceReception(session.getService(), "Sportello 01");
			
			// Create some random users
			int usersCount = 1024 * 16;
			// TODO
		}
		
	}
}
