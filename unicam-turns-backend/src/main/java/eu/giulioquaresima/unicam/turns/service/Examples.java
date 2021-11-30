package eu.giulioquaresima.unicam.turns.service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import org.bouncycastle.util.Arrays;

import eu.giulioquaresima.unicam.turns.utils.BijectiveNumeration;

public class Examples
{
	public static List<String> sequence(char[] digits, int size)
	{
		if (size < 1 || size > 6)
		{
			throw new IllegalArgumentException("Invalid size");
		}
		BijectiveNumeration bijectiveNumeration = new BijectiveNumeration(digits);
		char[] first = new char[size];
		char[] last = new char[size];
		Arrays.fill(first, digits[0]);
		Arrays.fill(last, digits[digits.length - 1]);
		return LongStream
				.rangeClosed(
						bijectiveNumeration.parse(String.valueOf(first)), 
						bijectiveNumeration.parse(String.valueOf(last))
						)
				.mapToObj(Long::valueOf)
				.map(bijectiveNumeration::format)
				.collect(Collectors.toList())
				;
	}
	public static void main(String[] args)
	{
		List<String> tickets = sequence("0123456789ABCDEFGH".toCharArray(), 2);
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
