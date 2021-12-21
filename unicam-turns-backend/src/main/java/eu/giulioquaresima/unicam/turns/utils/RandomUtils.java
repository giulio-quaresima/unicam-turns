package eu.giulioquaresima.unicam.turns.utils;

import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.random.RandomGenerator;

public class RandomUtils
{
	private final RandomGenerator randomGenerator;

	public RandomUtils(RandomGenerator randomGenerator)
	{
		super();
		this.randomGenerator = Objects.requireNonNull(randomGenerator);
	}
	
	public static RandomUtils with(RandomGenerator randomGenerator)
	{
		return new RandomUtils(randomGenerator);
	}
	
	public static RandomUtils withDefaultRandomGenerator()
	{
		return with(new Random(System.currentTimeMillis()));
	}
	
	public <E> E randomElement(E[] elements, double... likelihoods)
	{
		if (elements != null && likelihoods != null)
		{
			double randomDouble = randomGenerator.nextDouble(1);
			double lowerBound = 0;
			double upperBound = 0;
			int length = Math.min(elements.length, likelihoods.length);
			for (int i = 0; i < length; i++)
			{
				upperBound += likelihoods[i];
				if (randomDouble >= lowerBound && randomDouble < upperBound)
				{
					return elements[i];
				}
				lowerBound = upperBound;
			}
		}
		return null;
	}
	
	public <E> E randomElement(List<E> elements, List<Double> likelihoods)
	{
		if (elements != null && likelihoods != null)
		{
			double randomDouble = randomGenerator.nextDouble(1);
			double lowerBound = 0;
			int length = Math.min(elements.size(), likelihoods.size());
			for (int i = 1; i < length; i++)
			{
				if (randomDouble >= lowerBound && randomDouble < likelihoods.get(i))
				{
					return elements.get(i);
				}
				lowerBound += likelihoods.get(i);
			}
		}
		return null;
	}

}
