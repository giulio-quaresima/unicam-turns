package eu.giulioquaresima.unicam.turns.test.integration.jpa;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import eu.giulioquaresima.unicam.turns.domain.entities.ServiceReception;
import eu.giulioquaresima.unicam.turns.domain.entities.Session;
import eu.giulioquaresima.unicam.turns.domain.entities.Session.PositionedTicket;
import eu.giulioquaresima.unicam.turns.domain.entities.User;
import eu.giulioquaresima.unicam.turns.utils.RandomUtils;

@DataJpaTest
public class TestTicketsDraw extends AbstractTest
{
	public enum Action
	{
		WITHRAW,
		DRAW,
		CANCEL;
	}
	
	@Test
	public void testDrawInvariants()
	{
		RandomUtils randomUtils = RandomUtils.with(randomGenerator);
		int withrawalsCount = 0;
		int drawCount = 0;
		int cancellationsCount = 0;
		int successfulWithrawalsCount = 0;
		int successfulDrawCount = 0;
		int successfulCancellationsCount = 0;
		for (Session session : List.of(createSequentialDecimalSession(), createSequentialBijectiveSession(), createSrambledSession()))
		{
			ServiceReception serviceReception = createServiceReception(session.getService(), "Sportello 01");
			
			// Create some random users
			List<User> users = createRandomUsers(1024);
			
			while (!users.isEmpty())
			{
				switch (randomUtils.randomElement(Action.values(), 0.6, 0.3, 0.1))
				{
				case WITHRAW:
				{
					withrawalsCount++;
					User user = users.get(randomGenerator.nextInt(users.size()));
					boolean cancel = randomGenerator.nextBoolean() && randomGenerator.nextBoolean();
					PositionedTicket waitingTicket = session.waitingTicket(user);
					PositionedTicket positionedTicket = session.withraw(user, cancel);
					if (cancel)
					{
						if (waitingTicket != null)
						{
							assertThat(positionedTicket).isNotEqualTo(waitingTicket);
						}
					}
					else
					{
						if (waitingTicket != null)
						{
							assertThat(positionedTicket).isEqualTo(waitingTicket);
						}
						else
						{
							successfulWithrawalsCount++;
						}
					}
				}
					break;
				case DRAW:
				{
					drawCount++;
					PositionedTicket drawnTicket = session.draw(serviceReception);
					if (drawnTicket != null)
					{
						User owner = users.remove(Collections.binarySearch(users, drawnTicket.getTicket().getOwner()));
						assertThat(owner).isNotNull().isEqualTo(drawnTicket.getTicket().getOwner());
						successfulDrawCount++;
					}
				}
					break;
				case CANCEL:
				{
					cancellationsCount++;
					User user = users.remove(randomGenerator.nextInt(users.size()));
					PositionedTicket waitingTicket = session.waitingTicket(user);
					PositionedTicket cancelledTicket = session.cancel(user);
					if (cancelledTicket != null)
					{
						assertThat(waitingTicket).isEqualTo(cancelledTicket);
						assertThat(session.waitingTicket(user)).isNull();
						assertThat(session.ownedTickets(user))
							.isNotEmpty()
							.allMatch(t -> t.getTicket().isCancelled());
						successfulCancellationsCount++;
					}
				}
					break;
				default:
					throw new IllegalArgumentException("Unexpected value: " + randomUtils.randomElement(Action.values(), 0.6, 0.3, 0.1));
				}
			}
			
			assertThat(withrawalsCount)
				.isGreaterThanOrEqualTo(successfulWithrawalsCount)
				.isGreaterThanOrEqualTo(drawCount)
				.isGreaterThanOrEqualTo(cancellationsCount);
			// TODO
		}
		
	}
}
