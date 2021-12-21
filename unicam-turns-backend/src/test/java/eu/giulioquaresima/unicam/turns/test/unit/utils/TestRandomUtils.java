package eu.giulioquaresima.unicam.turns.test.unit.utils;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.EnumMap;

import org.junit.jupiter.api.Test;

import eu.giulioquaresima.unicam.turns.utils.RandomUtils;

public class TestRandomUtils
{
	public static enum ABC
	{
		A,B,C;
	}
	
	@Test
	public void testRandomElement()
	{
		int testSize = 1024 * 1024 * 16; // Big number to reduce probability of test failure
		EnumMap<ABC, Integer> counter = new EnumMap<>(ABC.class);
		RandomUtils randomUtils = RandomUtils.withDefaultRandomGenerator();
		for (int i = 0; i < testSize; i++)
		{
			ABC abc = randomUtils.randomElement(ABC.values(), 0.6, 0.3, 0.1);
			assertThat(abc).isNotNull();
			counter.put(abc, counter.getOrDefault(abc, 0) + 1);
		}
		assertThat(counter.get(ABC.A)).isGreaterThan(counter.get(ABC.B));
		assertThat(counter.get(ABC.B)).isGreaterThan(counter.get(ABC.C));
		assertThat(counter.get(ABC.C)).isGreaterThan(0);
	}
	
	public static void main(String[] args)
	{
		TestRandomUtils _self = new TestRandomUtils();
		_self.testRandomElement();
	}
	
}
