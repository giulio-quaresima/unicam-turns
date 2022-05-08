package eu.giulioquaresima.unicam.turns.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CollectionUtils
{

	@SuppressWarnings("unchecked")
	public static <T> Collection<Collection<T>> divide(Collection<T> collection, int maxSublistsSize)
	{
		if (maxSublistsSize < 1)
		{
			return Collections.singletonList(new ArrayList<>(collection));
		}
		AtomicInteger atomicInteger = new AtomicInteger(0);
		Function<T, Integer> classifier = t -> atomicInteger.getAndIncrement() / maxSublistsSize;
		return (Collection<Collection<T>>) (Collection<? extends Collection<T>>) collection.stream()
				.collect(Collectors.groupingBy(classifier))
				.values()
				;
	}

	public static void main(String[] args)
	{
		System.out.println(500/500);
	}

}
