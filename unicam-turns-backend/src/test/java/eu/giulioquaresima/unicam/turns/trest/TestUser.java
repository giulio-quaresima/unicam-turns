package eu.giulioquaresima.unicam.turns.trest;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.UnaryOperator;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import eu.giulioquaresima.unicam.turns.domain.entities.User;

@DataJpaTest
public class TestUser extends AbstractTest
{
	@Test
	public void testComparator()
	{
		final String[] familyNames = {"Rossi","Bianchi","Neri","Garibaldi"};
		final String[] givenNames = {"Ciro","Mario","Paolo","Emanuele"};
		final Random random = new Random(System.currentTimeMillis());
		final int length = 256;
		
		Set<User> users = new TreeSet<>();
		for (int index = 0; index < length; index++)
		{
			final UnaryOperator<String> caser = s -> {
				// Treno! 33, 33 e 33!
				if (random.nextInt() % 3 == 0)
					return s.toLowerCase();
				if (random.nextInt() % 3 == 0)
					return s.toUpperCase();
				return s;
			};
			users.add(userRepository.save(new User(
					caser.apply(givenNames[random.nextInt(givenNames.length)]), 
					caser.apply(familyNames[random.nextInt(familyNames.length)])))
					);
		}
		assertThat(users).size().isEqualTo(length); // Assert no comparable collisions (consistent with equals)
		
		User previous = null;
		for (User user : users)
		{
//			System.out.printf("%d: %s\n", user.getId(), user);
			if (previous != null)
			{
				int compare = previous.getFamilyName().compareToIgnoreCase(user.getFamilyName());
				if (compare == 0)
				{
					compare = previous.getGivenName().compareToIgnoreCase(user.getGivenName());
					if (compare == 0)
					{
						compare = previous.getId().compareTo(user.getId());
					}
				}
				assertThat(compare).isNegative();
			}
			previous = user;
		}
	}
}
