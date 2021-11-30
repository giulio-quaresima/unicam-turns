package eu.giulioquaresima.unicam.turns.utils;

import java.math.BigInteger;
import java.util.Random;
import java.util.stream.LongStream;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestBijectiveNumeration
{
	@Test
	public void testPow()
	{
		Assertions.assertEquals(1, BijectiveNumeration.pow(46857286, 0));
		Assertions.assertEquals(256, BijectiveNumeration.pow(2, 8));
	}
	
	/**
	 * This is to test why we need an integer local implementation
	 * of pow: for numerical analysis reasons, {@link StrictMath#pow(double, double)}
	 * generates errors for certain big numbers (loss of lesser significant digits).
	 */
	@Test
	public void testNeedForLocalPowImplementation()
	{
		Assertions.assertNotEquals(((long) Math.pow(15, 16)), BijectiveNumeration.pow(15, 16));
		Assertions.assertNotEquals(((long) StrictMath.pow(15, 16)), BijectiveNumeration.pow(15, 16));
	}
	
	@Test
	public void simpleTest()
	{
		BijectiveNumeration bijectiveNumeration = new BijectiveNumeration("ABC");
		Assertions.assertEquals("AACCBCBAACABBCC", bijectiveNumeration.format(8697135));
		Assertions.assertEquals("AACBCBCAAABBBABCACCBCCCABBBCBAC", bijectiveNumeration.format(368716872687216L));
		Assertions.assertEquals("ACACCCBBCCCBBACBCAACCABCBCBABABCCABACBBA", bijectiveNumeration.format(Long.MAX_VALUE));
	}
	
	@Test
	public void testIfFunctionsAreInverse()
	{
		testIfFunctionsAreInverse(new BijectiveNumeration(BijectiveNumeration.DIGITS_STRING_ALPHA));
	}
	private void testIfFunctionsAreInverse(BijectiveNumeration bijectiveNumeration)
	{
		Random random = new Random(System.currentTimeMillis());
		
		for (int count = 0; count < 256 * 256; count++)
		{
			long natural = random.nextLong();
			if (natural < 0L)
			{
				natural = -natural;
			}
			String bijectiveRepresentation = bijectiveNumeration.format(natural);
			Assertions.assertEquals(natural, bijectiveNumeration.parse(bijectiveRepresentation));
			// System.out.printf("%d : %s\n", natural, bijectiveRepresentation);
		}
	}
	
	@Test
	public void testBig()
	{
		BijectiveNumeration bijectiveNumeration = new BijectiveNumeration(BijectiveNumeration.DIGITS_STRING_ALPHA);
		for (int count = 0; count < 256; count++)
		{
			String aVeryBigRandomBijectiveNumber = RandomStringUtils.random(RandomUtils.nextInt(100, 2000), BijectiveNumeration.DIGITS_STRING_ALPHA);
			BigInteger natural = bijectiveNumeration.parseBig(aVeryBigRandomBijectiveNumber);
			Assertions.assertEquals(bijectiveNumeration.format(natural), aVeryBigRandomBijectiveNumber);
			/*
			System.out.println(natural);
			System.out.println(aVeryBigRandomBijectiveNumber);
			System.out.println();
			*/
		}
	}
	
	@Test
	public void testUnary()
	{
		BijectiveNumeration bijectiveNumeration = new BijectiveNumeration(BijectiveNumeration.DIGITS_STRING_UNARY);
		for (int natural = 0; natural < 1024; natural++)
		{
			Assertions.assertEquals(natural, bijectiveNumeration.format(natural).length());
		}
	}
	
	@Test
	public void testUnsortedStringDigit()
	{
		BijectiveNumeration bijectiveNumeration = new BijectiveNumeration("4saghe9k5j7rb8i");
		testIfFunctionsAreInverse(bijectiveNumeration);
	}
	
	@Test
	public void testParseLongOverflow()
	{
		Assertions.assertThrows(IntegerOverflowException.class, () -> {			
			BijectiveNumeration bijectiveNumeration = new BijectiveNumeration(BijectiveNumeration.DIGITS_STRING_ALPHA);
			Assertions.assertEquals("CRPXNLSKVLJFHG", bijectiveNumeration.format(Long.MAX_VALUE));
			Assertions.assertEquals(Long.MAX_VALUE, bijectiveNumeration.parse("CRPXNLSKVLJFHG"));
			
			// So the next number, CRPXNLSKVLJFHG + A = CRPXNLSKVLJFHH, should generate an overflow...
			bijectiveNumeration.parse("CRPXNLSKVLJFHH");
		});
	}

	@Test
	public void testNumberFormatException()
	{
		Assertions.assertThrows(NumberFormatException.class, () -> {
			BijectiveNumeration bijectiveNumeration = new BijectiveNumeration("ABC");
			bijectiveNumeration.parse("ABCDCBA"); // Note the 'D'
		});
	}

	@Test
	public void testInvalidStringOfDigits()
	{
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			new BijectiveNumeration("ABCDCBA");
		});
	}
	
	@Test
	public void testConcurrency()
	{
		int availableProcessors = Runtime.getRuntime().availableProcessors();
		if (availableProcessors > 1)
		{
			BijectiveNumeration bijectiveNumeration = new BijectiveNumeration(BijectiveNumeration.DIGITS_STRING_ALPHA);
			long testSize = availableProcessors * 1024 * 1024; // 1048576 for each processor
			long actual = LongStream.range(0, testSize)
					.parallel()
					.map(l -> {
						return bijectiveNumeration.parse(bijectiveNumeration.format(l));
					})
					.distinct()
					.count()
					;
			Assertions.assertEquals(testSize, actual);
		}
		else
		{
			Assertions.assertTrue(true);
		}
	}

	public static void main(String[] args)
	{
		TestBijectiveNumeration _self = new TestBijectiveNumeration();
		_self.testPow();
		_self.testNeedForLocalPowImplementation();
		_self.simpleTest();
		_self.testIfFunctionsAreInverse();
		_self.testBig();
		_self.testUnary();
		_self.testUnsortedStringDigit();
		_self.testParseLongOverflow();
		_self.testNumberFormatException();
		_self.testInvalidStringOfDigits();
		_self.testConcurrency();
	}
}
