package eu.giulioquaresima.unicam.turns.test.integration.jpa;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
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
	@Timeout (value = 60, unit = TimeUnit.SECONDS)
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
			
			// Create many random users
			List<User> users = createRandomUsers(1024 * 16);
			
			while (!users.isEmpty() || successfulWithrawalsCount > (successfulDrawCount + successfulCancellationsCount))
			{
				assertThat(withrawalsCount)
					.isGreaterThanOrEqualTo(successfulWithrawalsCount);
				assertThat(drawCount)
					.isGreaterThanOrEqualTo(successfulDrawCount);
				assertThat(cancellationsCount)
					.isGreaterThanOrEqualTo(successfulCancellationsCount);
				
				/*
				 * The likelihoods 0.6 and 0.3 emulate a situation where the frequency
				 * of the users arrival is twice the frequency of the service, plus
				 * a 10% (0.1) of users' cancellations.
				 */
				switch (randomUtils.randomElement(Action.values(), 0.6, 0.3, 0.1))
				{
				case WITHRAW:
				{
					withrawalsCount++;
					User user = users.get(randomGenerator.nextInt(users.size()));
					if (user != null)
					{
						boolean cancel = randomGenerator.nextBoolean() && randomGenerator.nextBoolean();
						PositionedTicket waitingTicket = session.waitingTicket(user);
						PositionedTicket positionedTicket = session.withraw(user, cancel);
						if (cancel)
						{
							cancellationsCount++;
							successfulWithrawalsCount++;
							if (waitingTicket != null)
							{
								successfulCancellationsCount++;
								assertThat(positionedTicket).isNotNull().isNotEqualTo(waitingTicket);
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
				}
			}		
			
			assertThat(successfulWithrawalsCount).isEqualTo(successfulDrawCount + successfulCancellationsCount);
		}
		
	}
}
