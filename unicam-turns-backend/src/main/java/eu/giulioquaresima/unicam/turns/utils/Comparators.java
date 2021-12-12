package eu.giulioquaresima.unicam.turns.utils;

import java.util.Comparator;

public class Comparators
{
	public static final Comparator<Integer> INTEGER_NULLS_LAST_COMPARATOR = Comparator.nullsLast(Comparator.naturalOrder());
	
	public static Comparator<Integer> integerNullsLastComparator()
	{
		return INTEGER_NULLS_LAST_COMPARATOR;
	}
}
