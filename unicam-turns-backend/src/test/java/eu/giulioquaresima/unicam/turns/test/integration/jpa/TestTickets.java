package eu.giulioquaresima.unicam.turns.test.integration.jpa;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.domain.JpaSort;

import eu.giulioquaresima.unicam.turns.domain.entities.Session;
import eu.giulioquaresima.unicam.turns.domain.entities.Ticket;
import eu.giulioquaresima.unicam.turns.domain.entities.Ticket_;
import eu.giulioquaresima.unicam.turns.utils.BijectiveBaseKNumeration;

@DataJpaTest
public class TestTickets extends AbstractTest
{
	@Test
	public void testTicketWithdrawSequentialDecimal()
	{
		Session session = createSequentialDecimalSession();
		session.withraw(createUser("Giulio", "Quaresima"));
		session.withraw(createUser("Daneel", "Olivaw"));		
		List<Ticket> tickets = ticketRepository.findAll(JpaSort.of(Ticket_.id));
		assertThat(tickets).size().isEqualTo(2);
		assertThat(tickets.get(0).getUser().getFamilyName()).isEqualTo("Quaresima");
		assertThat(tickets.get(1).getUser().getFamilyName()).isEqualTo("Olivaw");
		assertThat(tickets.get(0).getNumber()).isEqualTo("1");
		assertThat(tickets.get(1).getNumber()).isEqualTo("2");
	}
	
	@Test
	public void testTicketWithdrawSequentialBijective()
	{
		Session session = createSequentialBijectiveSession();
		BijectiveBaseKNumeration bijectiveBaseKNumeration = session.getSessionConfiguration().getTicketSourceConfiguration().getBijectiveBaseKNumeration();
		assertThat(bijectiveBaseKNumeration).isNotNull();

		int size = 1024;
		for (int count = 0; count < size; count++)
		{
			session.withraw(createUser(RandomStringUtils.randomAlphabetic(6), RandomStringUtils.randomAlphabetic(8)));
		}
		
		List<Ticket> tickets = ticketRepository.findAll(JpaSort.of(Ticket_.id));
		assertThat(tickets).size().isEqualTo(size);
		for (int count = 0; count < size; count++)
		{
			assertThat(tickets.get(count).getNumber()).isEqualTo(bijectiveBaseKNumeration.format(count + 1));
//			System.out.println(tickets.get(count));
		}
	}
	
//	@Test // FIXME
	public void testTicketWithdrawScrambledBijective()
	{
		Session session = createSrambledSession();
		BijectiveBaseKNumeration bijectiveBaseKNumeration = session.getSessionConfiguration().getTicketSourceConfiguration().getBijectiveBaseKNumeration();
		assertThat(bijectiveBaseKNumeration).isNotNull();

		int size = 1024;
		for (int count = 0; count < size; count++)
		{
			System.out.println(session.withraw(createUser(RandomStringUtils.randomAlphabetic(6), RandomStringUtils.randomAlphabetic(8))));
		}
		
		long startRange = bijectiveBaseKNumeration.parse(new char[] {bijectiveBaseKNumeration.getDigits()[0],bijectiveBaseKNumeration.getDigits()[0]});
		Set<String> expectedNumbers = new HashSet<>();
		bijectiveBaseKNumeration.sequence(startRange, startRange + size - 1).forEach(expectedNumbers::add);
		assertThat(expectedNumbers).size().isEqualTo(size);
		
		List<Ticket> tickets = ticketRepository.findAll();
		assertThat(tickets).size().isEqualTo(size);
		for (int count = 0; count < size; count++)
		{
			expectedNumbers.remove(tickets.get(count).getNumber());
//			System.out.println(tickets.get(count));
		}
//		assertThat(expectedNumbers).isEmpty();
	}
}
