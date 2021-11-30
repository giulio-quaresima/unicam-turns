package eu.giulioquaresima.unicam.turns.utils;

/**
 * An unchecked exception to be thrown when a calculation is expected
 * to generate a value that will be greater to (or less then) the
 * maximum (or minimum) possible value for a given integer type.
 * 
 * @author Giulio Quaresima (giulio.quaresima--at--unipg.it, giulio.quaresima--at--gmail.com)
 * 
 * @see https://en.wikipedia.org/wiki/Integer_overflow
 */
public class IntegerOverflowException extends RuntimeException
{
	private static final long serialVersionUID = 3284763178718804140L;

	public IntegerOverflowException()
	{
		super();
	}

	public IntegerOverflowException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
	{
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public IntegerOverflowException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public IntegerOverflowException(String message)
	{
		super(message);
	}

	public IntegerOverflowException(Throwable cause)
	{
		super(cause);
	}

}
