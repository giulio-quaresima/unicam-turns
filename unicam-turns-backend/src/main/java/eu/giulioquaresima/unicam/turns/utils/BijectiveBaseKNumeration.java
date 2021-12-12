package eu.giulioquaresima.unicam.turns.utils;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.LongFunction;
import java.util.stream.LongStream;
import java.util.stream.Stream;

/**
 * Support any bijective base-k numeral system with a digit string composed by
 * any Java character: for a complete description of the bijective base-k
 * numeral system, see the Wikipedia article linked below.
 * 
 * @see https://en.wikipedia.org/wiki/Bijective_numeration
 * 
 * @author Giulio Quaresima (giulio.quaresima--at--gmail.com)
 */
public class BijectiveBaseKNumeration
{
	public static final String DIGITS_STRING_UNARY = "1";
	public static final String DIGITS_STRING_BINARY = "12";
	public static final String DIGITS_STRING_BINARY_ZERO = "01";
	public static final String DIGITS_STRING_DECIMAL = "123456789A";
	public static final String DIGITS_STRING_DECIMAL_ZERO = "0123456789";
	public static final String DIGITS_STRING_ALPHA = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	public static final String DIGITS_STRING_ALPHANUMERIC = DIGITS_STRING_DECIMAL_ZERO + DIGITS_STRING_ALPHA;

	private final char[] digits;
	private final char[] digitsIndex;
	private final int[] digitsIndexValues;

	/**
	 * @param digits The string of digits: the digits order matters.
	 * 
	 * @throws NullPointerException
	 * @throws IllegalArgumentException
	 */
	public BijectiveBaseKNumeration(char[] digits) throws NullPointerException, IllegalArgumentException
	{
		super();
		this.digits = Objects.requireNonNull(digits, "The string of digits is required");
		if (digits.length == 0)
		{
			throw new IllegalArgumentException("The string of digits cannot be empty");
		}
		
		/*
		 * Creation of an index useful to speed up the parsing of
		 * the bijective numbers.
		 */
		this.digitsIndex = Arrays.copyOf(this.digits, this.digits.length);
		Arrays.sort(digitsIndex);
		this.digitsIndexValues = new int[this.digitsIndex.length];
		for (int index = 0; index < digits.length; index++)
		{
			digitsIndexValues[Arrays.binarySearch(digitsIndex, digits[index])] = index + 1;
		}

		// Validation.
		for (int index = 1; index < digitsIndex.length; index++)
		{
			if (digitsIndex[index] == digitsIndex[index-1])
			{
				throw new IllegalArgumentException(String.format(
						"The string of digits contains the repetitive symbol %c at position %d", 
						digitsIndex[index],
						Math.max(digitsIndexValues[index], digitsIndexValues[index-1]) - 1
						));
			}
		}
	}
	
	/**
	 * This is an utility constructor which calls the {@link #BijectiveBaseKNumeration(char[])}
	 * converting the given string to an array of characters.
	 * 
	 * @param digits The string of digits.
	 */
	public BijectiveBaseKNumeration(String digits) throws NullPointerException, IllegalArgumentException
	{
		this(digits.toCharArray());
	}
	
	/**
	 * Format a natural number to its bijective representation, using
	 * the given string of digits.
	 * 
	 * @param natural A natural number, greater than or equal to 0.
	 * 
	 * @return The bijective representation: 0 is always the empty string.
	 * 
	 * @throws IllegalArgumentException If the number is not a natural number.
	 */
	public String format(long natural) throws IllegalArgumentException
	{
		if (natural < 0)
		{
			throw new IllegalArgumentException("Only natural numbers are supported");
		}
		
		StringBuilder stringBuilder = new StringBuilder();
		
		natural--;
		while (natural >= 0)
		{
			stringBuilder.insert(0, digits[(int) (natural % digits.length)]);
			natural = (natural / digits.length) - 1;
		}
		
		return stringBuilder.toString();
	}
	
	/**
	 * This is analogous to {@link #format(long)}, but use
	 * {@link BigInteger}s instead of longs.
	 * 
	 * @param natural
	 * @return
	 * 
	 * @throws NullPointerException If the number is null.
	 * @throws IllegalArgumentException If the number is not a natural number.
	 */
	public String format(BigInteger natural) throws NullPointerException, IllegalArgumentException
	{
		if (Objects.requireNonNull(natural, "").compareTo(BigInteger.ZERO) < 0)
		{
			throw new IllegalArgumentException("Only natural numbers are supported");
		}
		
		StringBuilder stringBuilder = new StringBuilder();
		
		BigInteger divisor = BigInteger.valueOf(digits.length);
		natural = natural.subtract(BigInteger.ONE);
		while (natural.compareTo(BigInteger.ZERO) >= 0)
		{
			BigInteger[] quotientAndReminder = natural.divideAndRemainder(divisor);
			stringBuilder.insert(0, digits[quotientAndReminder[1].intValueExact()]);
			natural = quotientAndReminder[0].subtract(BigInteger.ONE);
		}
		
		return stringBuilder.toString();
	}
	
	public <R> R format(long natural, BiFunction<Long, String, R> mapper)
	{
		return mapper.apply(natural, format(natural));
	}
	public <R> R formatBig(BigInteger natural, BiFunction<BigInteger, String, R> mapper)
	{
		return mapper.apply(natural, format(natural));
	}
	
	/**
	 * Try to parse a natural number in its bijective representation.
	 * 
	 * @param bijectiveRepresentation The bijective representation of a natural number.
	 * 
	 * @return The natural number, 0 for the empty string.
	 * 
	 * @throws NullPointerException If the bijective number is null.
	 * 
	 * @throws NumberFormatException If the bijective number contains symbols which are not
	 * part of the string of digits.
	 * 
	 * @throws IntegerOverflowException If the result is potentially greater than {@link Long#MAX_VALUE}.
	 */
	public long parse(char[] bijectiveRepresentation) throws NullPointerException, NumberFormatException, IntegerOverflowException
	{
		if (bijectiveRepresentation == null || bijectiveRepresentation.length == 0)
		{
			return 0L;
		}
		
		long result = 0;
		
		int base = digits.length;
		for (int n = 0; n < bijectiveRepresentation.length; n++)
		{
			int index = (bijectiveRepresentation.length - n) - 1;
			int indexOfIndex = Arrays.binarySearch(digitsIndex, bijectiveRepresentation[index]);
			if (indexOfIndex < 0)
			{
				throw new NumberFormatException(String.format("The bijective number contains the symbol '%c' which is not part of the string of digits", bijectiveRepresentation[index]));
			}
			int digitValue = digitsIndexValues[indexOfIndex];
			long positionValue = pow(base, n);
			result += digitValue * positionValue;
			if (result < 1)
			{
				throw new IntegerOverflowException();
			}
		}
		
		return result;
	}
	public long parse(String bijectiveRepresentation) throws NullPointerException, NumberFormatException, IntegerOverflowException
	{
		if (bijectiveRepresentation != null)
		{
			return parse(bijectiveRepresentation.toCharArray());
		}
		return parse((char[]) null);
	}
	
	/**
	 * Try to parse a natural number in its bijective representation: this is the
	 * {@link BigInteger} implementation of {@link #parse(String)}.
	 * 
	 * @param bijectiveRepresentation The bijective representation of a natural number.
	 * 
	 * @return The natural number, 0 for the empty string.
	 * 
	 * @throws NullPointerException If the bijective number is null.
	 * 
	 * @throws NumberFormatException If the bijective number contains symbols which are not
	 * part of the string of digits.
	 */
	public BigInteger parseBig(String bijectiveRepresentation) throws NullPointerException, NumberFormatException
	{
		bijectiveRepresentation = Objects.requireNonNull(bijectiveRepresentation, "The bijective number is required").trim();
		
		char[] bijectiveRepresentationArray = bijectiveRepresentation.toCharArray();
		
		BigInteger result = BigInteger.ZERO;
		
		int base = digits.length;
		for (int n = 0; n < bijectiveRepresentationArray.length; n++)
		{
			int index = (bijectiveRepresentationArray.length - n) - 1;
			int indexOfIndex = Arrays.binarySearch(digitsIndex, bijectiveRepresentationArray[index]);
			if (indexOfIndex < 0)
			{
				throw new NumberFormatException(String.format("The bijective number contains the symbol '%c' which is not part of the string of digits", bijectiveRepresentationArray[index]));
			}
			int digitValue = digitsIndexValues[indexOfIndex];
			BigInteger positionValue = BigInteger.valueOf(base).pow(n);
			result = result.add(BigInteger.valueOf(digitValue).multiply(positionValue));
		}
		
		return result;
	}
	
	/**
	 * Calculate the range of decimal numbers which generate the complete 
	 * base-k representations sequence of {@code length} length.
	 * 
	 * For example consider the binary bijective numeration (with [12] digits) 
	 * showed by the table below:
	 * 
	 * <table border>
	 *   <tr>
	 *     <th>decimal</th>
	 *     <th>base-2 bijective</th>
	 *     <th>length</th>
	 *   </tr>
	 *   <tr>
	 *     <td>0</td><td></td><td>0</td>
	 *   </tr>
	 *   <tr>
	 *     <td>1</td><td>1</td><td>1</td>
	 *   </tr>
	 *   <tr>
	 *     <td>2</td><td>2</td><td>1</td>
	 *   </tr>
	 *   <tr>
	 *     <td>3</td><td>11</td><td>2</td>
	 *   </tr>
	 *   <tr>
	 *     <td>4</td><td>12</td><td>2</td>
	 *   </tr>
	 *   <tr>
	 *     <td>5</td><td>21</td><td>2</td>
	 *   </tr>
	 *   <tr>
	 *     <td>6</td><td>22</td><td>2</td>
	 *   </tr>
	 *   <tr>
	 *     <td>7</td><td>111</td><td>3</td>
	 *   </tr>
	 * <table>
	 * 
	 * As you can see, the binary bijective numbers of length 2
	 * are those which ranges from decimal value 3 to 6 (inclusive),
	 * so calling <code>sequenceRange(2, 2)</code> returns
	 * <code>[3, 6]</code>.
	 * 
	 * @param base The base (k) of the base-k numeration.
	 * 
	 * @param length The required length.
	 * 
	 * @return An array of length 2, whose elements are the first
	 * and the last (inclusive) decimal value of the  base-k bijective
	 * numbers representations of {@code length} length.
	 */
	public static long[] sequenceRangeInterval(int base, int length)
	{
		if (base < 1)
		{
			throw new IllegalArgumentException("base must be >= 1");
		}
		if (length < 0)
		{
			throw new IllegalArgumentException("length must be >= 0");
		}
		if (length == 0)
		{
			return new long[] {0,0};
		}
		long[] prev = sequenceRangeInterval(base, length - 1);
		return new long[] {
				prev[1] + 1, 
				prev[1] + pow(base, length)
				};
	}
	
	/**
	 * Equivalent to <code>sequenceRangeInterval(getDigits().length, length)</code>.
	 * 
	 * @param length
	 * @return
	 */
	public long[] sequenceRangeInterval(int length)
	{
		return sequenceRangeInterval(getDigits().length, length);
	}
	
	public Stream<String> sequence(long[] range)
	{
		if (Objects.requireNonNull(range, "").length != 2)
		{
			throw new IllegalArgumentException("A range must be a 2-length array");
		}
		return sequence(range[0], range[1]);
	}
	public Stream<String> sequence(long startInclusive, final long endInclusive)
	{
		return LongStream.rangeClosed(startInclusive, endInclusive).mapToObj(this::format);
	}
	public <R> Stream<R> sequence(long[] range, BiFunction<Long, String, R> mapper)
	{
		if (Objects.requireNonNull(range, "").length != 2)
		{
			throw new IllegalArgumentException("A range must be a 2-length array");
		}
		return sequence(range[0], range[1], mapper);
	}
	public <R> Stream<R> sequence(long startInclusive, final long endInclusive, BiFunction<Long, String, R> mapper)
	{
		LongFunction<R> longMapper = natural -> format(natural, mapper);
		return LongStream.rangeClosed(startInclusive, endInclusive).mapToObj(longMapper);
	}
	
	public char[] getDigits()
	{
		return digits;
	}

	public char[] getDigitsIndex()
	{
		return digitsIndex;
	}

	public int[] getDigitsIndexValues()
	{
		return digitsIndexValues;
	}

	public static void main(String[] args)
	{
		System.out.println(Arrays.toString(BijectiveBaseKNumeration.sequenceRangeInterval(2, 0)));
		System.out.println(Arrays.toString(BijectiveBaseKNumeration.sequenceRangeInterval(2, 1)));
		System.out.println(Arrays.toString(BijectiveBaseKNumeration.sequenceRangeInterval(2, 2)));
		System.out.println(Arrays.toString(BijectiveBaseKNumeration.sequenceRangeInterval(1, 0)));
		System.out.println(Arrays.toString(BijectiveBaseKNumeration.sequenceRangeInterval(16, 4)));
	}
	
	/**
	 * The {@link StrictMath#pow(double, double)}
	 * presents two drawbacks for our needs:
	 * <ol>
	 * <li>it generates conversion error from double to long, due to the loss of lesser significant digits in its internal exponential representation;</li>
	 * <li>it doesn't prevent integer overflow errors.</li>
	 * </ol>
	 * 
	 * @param base
	 * 
	 * @param n
	 * 
	 * @return
	 * 
	 * @throws IntegerOverflowException If the result is potentially greater than {@link Long#MAX_VALUE}.
	 */
	protected static long pow(int base, int n) throws IntegerOverflowException
	{
		long result = 1;
		long overflowLimit = Long.MAX_VALUE / base;
		while (n-- > 0)
		{
			if (overflowLimit <= result) // The next multiplication will go over the limit of Long.MAX_VALUE
			{
				throw new IntegerOverflowException("Long overflow, use parseBig()");
			}
			result *= base;
		}
		return result;
	}

}
