package eu.giulioquaresima.unicam.turns.service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import org.bouncycastle.util.Arrays;

import eu.giulioquaresima.unicam.turns.utils.BijectiveBaseKNumeration;

public class Examples
{
	public static int[] sequenceRange(int base, int size)
	{
		if (size < 2)
		{
			return new int[] {0,base};
		}
		int[] prev = sequenceRange(base, size-1);
		return new int[] {prev[1]+1, prev[1]+((int) Math.pow(base, size))};
	}
	public static List<String> sequence(char[] digits, int size)
	{
		if (size < 1 || size > 6)
		{
			throw new IllegalArgumentException("Invalid size");
		}
		BijectiveBaseKNumeration bijectiveNumeration = new BijectiveBaseKNumeration(digits);
		int[] range = sequenceRange(digits.length, size);
		return LongStream
				.rangeClosed(range[0],range[1])
				.mapToObj(Long::valueOf)
				.map(bijectiveNumeration::format)
				.collect(Collectors.toList())
				;
	}
	public static void main(String[] args)
	{
		List<String> tickets = sequence("0123456789ABCDEF".toCharArray(), 2);
		for (int i = 0; i < tickets.size(); i++)
		{
			System.out.printf("%5d : %s\n", i, tickets.get(i));
		}
		System.out.println("Shuffle!");
		Collections.shuffle(tickets);
		for (int i = 0; i < tickets.size(); i++)
		{
			System.out.printf("%5d : %s\n", i, tickets.get(i));
		}
	}
}
