package eu.giulioquaresima.unicam.turns.test.integration.jpa;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.assertj.core.api.Condition;
import org.assertj.core.condition.Not;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.domain.JpaSort;

import eu.giulioquaresima.unicam.turns.domain.entities.Session;
import eu.giulioquaresima.unicam.turns.domain.entities.Session.PositionedTicket;
import eu.giulioquaresima.unicam.turns.domain.entities.Ticket;
import eu.giulioquaresima.unicam.turns.domain.entities.Ticket_;
import eu.giulioquaresima.unicam.turns.domain.entities.User;
import eu.giulioquaresima.unicam.turns.utils.BijectiveBaseKNumeration;

@DataJpaTest
public class TestTicketsWithdrawal extends AbstractTest
{
	@Test
	public void testTicketWithdrawSequentialDecimal()
	{
		Session session = createSequentialDecimalSession();
		session.withraw(createUser("Giulio", "Quaresima"), true);
		session.withraw(createUser("Daneel", "Olivaw"), true);		
		List<Ticket> tickets = ticketRepository.findAll(JpaSort.of(Ticket_.id));
		assertThat(tickets).size().isEqualTo(2);
		assertThat(tickets.get(0).getOwner().getFamilyName()).isEqualTo("Quaresima");
		assertThat(tickets.get(1).getOwner().getFamilyName()).isEqualTo("Olivaw");
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
			session.withraw(createRandomUser(), true);
		}
		
		List<Ticket> tickets = ticketRepository.findAll(JpaSort.of(Ticket_.id));
		assertThat(tickets).size().isEqualTo(size);
		for (int count = 0; count < size; count++)
		{
			assertThat(tickets.get(count).getNumber()).isEqualTo(bijectiveBaseKNumeration.format(count + 1));
//			System.out.println(tickets.get(count));
		}
	}
	
	@Test
	public void testTicketWithdrawScrambledBijective()
	{
		Session session = createSrambledSession();
		BijectiveBaseKNumeration bijectiveBaseKNumeration = session.getSessionConfiguration().getTicketSourceConfiguration().getBijectiveBaseKNumeration();
		assertThat(bijectiveBaseKNumeration).isNotNull();

		// Complete range of all bijective numbers with 2 and 3 digits
		char lessDigit = bijectiveBaseKNumeration.getDigits()[0];
		char greaterDigit = bijectiveBaseKNumeration.getDigits()[bijectiveBaseKNumeration.getDigits().length - 1];
		long startRange = bijectiveBaseKNumeration.parse(new char[] {lessDigit, lessDigit});
		long endRange = bijectiveBaseKNumeration.parse(new char[] {greaterDigit, greaterDigit, greaterDigit});
		
		long size = (endRange - startRange) + 1;
		for (long count = 0; count < size; count++)
		{
			session.withraw(createRandomUser(), true);
		}
		
		Set<String> expectedNumbers = new HashSet<>();
		bijectiveBaseKNumeration.sequence(startRange, startRange + size - 1).forEach(expectedNumbers::add);
		assertThat(expectedNumbers).size().isEqualTo(size);
		
		List<Ticket> tickets = ticketRepository.findAll();
		assertThat(tickets).size().isEqualTo(size);
		for (int count = 0; count < size; count++)
		{
			expectedNumbers.remove(tickets.get(count).getNumber());
		}
		assertThat(expectedNumbers).isEmpty();
	}
	
	@Test
	public void testWithrawTwiceNotAllowed()
	{
		for (Session session : List.of(createSequentialDecimalSession(), createSequentialBijectiveSession(), createSrambledSession()))
		{
			User alice = createUser("Alice", null);
			User bob = createUser("Bob", null);
			User carol = createUser("Carol", null);
			
			PositionedTicket aliceTicket = session.withraw(alice, false);
			PositionedTicket bobTicket = session.withraw(bob, false);
			PositionedTicket carolTicket = session.withraw(carol, false);
			
			assertThat(aliceTicket).isEqualTo(aliceTicket);
			
			assertThat(aliceTicket).isNotEqualTo(bobTicket);
			assertThat(aliceTicket).isNotEqualTo(carolTicket);
			assertThat(bobTicket).isNotEqualTo(carolTicket);
		
			PositionedTicket newAliceTicket = session.withraw(alice, false);
			assertThat(aliceTicket).isEqualTo(newAliceTicket); // The second withraw returns the same ticket...
			session.cancel(alice);
			newAliceTicket = session.withraw(alice, false);
			assertThat(aliceTicket).isNotEqualTo(newAliceTicket); // ... unless you cancel the previous one
			
			// Or, with only one call
			aliceTicket = newAliceTicket;
			newAliceTicket = null;
			newAliceTicket = session.withraw(alice, true); // Notice the boolean argument
			assertThat(aliceTicket).isNotEqualTo(newAliceTicket);
			
			Condition<PositionedTicket> isWaiting = new Condition<PositionedTicket>(t -> t.getTicket().isWaiting(), "isWaiting");
			assertThat(session.ownedTickets(alice))
				.areExactly(2, Not.not(isWaiting))
				.areExactly(1, isWaiting)
				.size().isEqualTo(3);
			assertThat(session.ownedTickets(bob))
				.areExactly(1, isWaiting)
				.size().isEqualTo(1);
			assertThat(session.ownedTickets(carol))
				.areExactly(1, isWaiting)
				.size().isEqualTo(1);
		}
	}
	
}
