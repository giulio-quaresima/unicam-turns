package eu.giulioquaresima.unicam.turns.utils;

import java.math.BigInteger;
import java.util.Random;
import java.util.stream.LongStream;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.bouncycastle.util.Arrays;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestBijectiveBaseKNumeration
{
	@Test
	public void testPow()
	{
		Assertions.assertEquals(1, BijectiveBaseKNumeration.pow(46857286, 0));
		Assertions.assertEquals(256, BijectiveBaseKNumeration.pow(2, 8));
	}
	
	/**
	 * This is to test why we need an integer local implementation
	 * of pow: for numerical analysis reasons, {@link StrictMath#pow(double, double)}
	 * generates errors for certain big numbers (loss of lesser significant digits).
	 */
	@Test
	public void testNeedForLocalPowImplementation()
	{
		Assertions.assertNotEquals(((long) Math.pow(15, 16)), BijectiveBaseKNumeration.pow(15, 16));
		Assertions.assertNotEquals(((long) StrictMath.pow(15, 16)), BijectiveBaseKNumeration.pow(15, 16));
	}
	
	@Test
	public void simpleTest()
	{
		BijectiveBaseKNumeration bijectiveNumeration = new BijectiveBaseKNumeration("ABC");
		Assertions.assertEquals("AACCBCBAACABBCC", bijectiveNumeration.format(8697135));
		Assertions.assertEquals("AACBCBCAAABBBABCACCBCCCABBBCBAC", bijectiveNumeration.format(368716872687216L));
		Assertions.assertEquals("ACACCCBBCCCBBACBCAACCABCBCBABABCCABACBBA", bijectiveNumeration.format(Long.MAX_VALUE));
	}
	
	@Test
	public void testIfFunctionsAreInverse()
	{
		testIfFunctionsAreInverse(new BijectiveBaseKNumeration(BijectiveBaseKNumeration.DIGITS_STRING_ALPHA));
	}
	private void testIfFunctionsAreInverse(BijectiveBaseKNumeration bijectiveNumeration)
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
		BijectiveBaseKNumeration bijectiveNumeration = new BijectiveBaseKNumeration(BijectiveBaseKNumeration.DIGITS_STRING_ALPHA);
		for (int count = 0; count < 256; count++)
		{
			String aVeryBigRandomBijectiveNumber = RandomStringUtils.random(RandomUtils.nextInt(100, 2000), BijectiveBaseKNumeration.DIGITS_STRING_ALPHA);
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
		BijectiveBaseKNumeration bijectiveNumeration = new BijectiveBaseKNumeration(BijectiveBaseKNumeration.DIGITS_STRING_UNARY);
		for (int natural = 0; natural < 1024; natural++)
		{
			Assertions.assertEquals(natural, bijectiveNumeration.format(natural).length());
		}
	}
	
	@Test
	public void testUnsortedStringDigit()
	{
		BijectiveBaseKNumeration bijectiveNumeration = new BijectiveBaseKNumeration("4saghe9k5j7rb8i");
		testIfFunctionsAreInverse(bijectiveNumeration);
	}
	
	@Test
	public void testParseLongOverflow()
	{
		Assertions.assertThrows(IntegerOverflowException.class, () -> {			
			BijectiveBaseKNumeration bijectiveNumeration = new BijectiveBaseKNumeration(BijectiveBaseKNumeration.DIGITS_STRING_ALPHA);
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
			BijectiveBaseKNumeration bijectiveNumeration = new BijectiveBaseKNumeration("ABC");
			bijectiveNumeration.parse("ABCDCBA"); // Note the 'D'
		});
	}

	@Test
	public void testInvalidStringOfDigits()
	{
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			new BijectiveBaseKNumeration("ABCDCBA");
		});
	}
	
	@Test
	public void testConcurrency()
	{
		int availableProcessors = Runtime.getRuntime().availableProcessors();
		if (availableProcessors > 1)
		{
			BijectiveBaseKNumeration bijectiveNumeration = new BijectiveBaseKNumeration(BijectiveBaseKNumeration.DIGITS_STRING_ALPHA);
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
	
	public void testSequenceRangeInterval()
	{
		for (int base = 1; base < 8; base++)
		{
			for (int length = 0; length < 8; length++)
			{
				char[] digits = new char[base];
				System.arraycopy(
						BijectiveBaseKNumeration.DIGITS_STRING_ALPHANUMERIC.toCharArray(), 
						0, digits, 0, base);
				BijectiveBaseKNumeration bijectiveBaseKNumeration = new BijectiveBaseKNumeration(digits);
				char[] firstExpectedNumber = new char[length];
				char[] lastExpectedNumber = new char[length];
				Arrays.fill(firstExpectedNumber, digits[0]);
				Arrays.fill(lastExpectedNumber, digits[base-1]);
				long[] expectedRange = {
						bijectiveBaseKNumeration.parse(firstExpectedNumber),
						bijectiveBaseKNumeration.parse(lastExpectedNumber)};
				Assertions.assertArrayEquals(expectedRange, bijectiveBaseKNumeration.sequenceRangeInterval(length));
			}
		}
	}

	public static void main(String[] args)
	{
		TestBijectiveBaseKNumeration _self = new TestBijectiveBaseKNumeration();
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
		_self.testSequenceRangeInterval();
	}
}
