package eu.giulioquaresima.unicam.turns.utils;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;

public class TestCollectionUtils
{
	@Test
	public void testDivide()
	{
		Collection<Integer> integers = IntStream.range(0, 11111).mapToObj(Integer::valueOf).collect(Collectors.toUnmodifiableList());
		Collection<Collection<Integer>> integerss = CollectionUtils.divide(integers, 1000);
		assertThat(integerss).size().isEqualTo(12);
		int count = 0;
		for (Collection<Integer> sublist : integerss)
		{
			count++;
			if (count < 12)
			{
				assertThat(sublist).size().isEqualTo(1000);
			}
			else
			{
				assertThat(sublist).size().isEqualTo(111);
			}
		}
	}
	
	public static void main(String[] args)
	{
		TestCollectionUtils _self = new TestCollectionUtils();
		_self.testDivide();
	}
}
