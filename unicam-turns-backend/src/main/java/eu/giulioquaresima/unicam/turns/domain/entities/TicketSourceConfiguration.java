package eu.giulioquaresima.unicam.turns.domain.entities;

import javax.persistence.Embeddable;
import javax.persistence.PostLoad;

import eu.giulioquaresima.unicam.turns.utils.BijectiveBaseKNumeration;

/**
 * 
 * 
 * @author Giulio Quaresima (giulio.quaresima--at--gmail.com)
 */
@Embeddable
public class TicketSourceConfiguration
{
	/**
	 * A default set of digits for bijective numeration system which improves readability.
	 */
	public static final char[] BIJECTIVE_DEFAULT_DIGITS = "123456789ABCDEFGHX".toCharArray();
	
	private boolean postponeEnabled = false;
	private boolean scrambleTickets = false;
	private boolean useBijectiveNumeration = false;
	private char[] bijectiveNumerationDigits = null;
	
	private BijectiveBaseKNumeration bijectiveBaseKNumeration;
	
	/**
	 * If <code>true</code>, the postpone function (i.e. the option for the user
	 * to postpone its ticket rank if she is momentarily away from
	 * the service location) is enabled.
	 * 
	 * @return
	 */
	public boolean isPostponeEnabled()
	{
		return postponeEnabled;
	}
	public void setPostponeEnabled(boolean postponeEnabled)
	{
		this.postponeEnabled = postponeEnabled;
		if (postponeEnabled)
		{
			setScrambleTickets(true);
		}
	}
	/**
	 * If <code>true</code>, the {@link Ticket#getNumber()}s will be
	 * randomly scrambled, so as to make impossible for the user to
	 * infer the ticket's rank from its number: if {@link #isPostponeEnabled()}
	 * is <code>true</code>, this option must be considered true as well. 
	 * 
	 * @return
	 */
	public boolean isScrambleTickets()
	{
		return scrambleTickets;
	}
	public void setScrambleTickets(boolean scrambleTickets)
	{
		this.scrambleTickets = scrambleTickets;
		if (scrambleTickets)
		{
			setUseBijectiveNumeration(true);
		}
		else
		{
			setPostponeEnabled(false);
		}
	}
	/**
	 * If <code>true</code> the {@link Ticket#getNumber()}s will be generated
	 * using a <em>bijective</em> numeral system, otherwise, a standard positional
	 * decimal system is used.
	 * 
	 * If {@link #isScrambleTickets()} is <code>true</code>, this option must be 
	 * considered true as well.
	 * 
	 * @return
	 * 
	 * @see https://en.wikipedia.org/wiki/Bijective_numeration
	 */
	public boolean isUseBijectiveNumeration()
	{
		return useBijectiveNumeration;
	}
	public void setUseBijectiveNumeration(boolean useBijectiveNumeration)
	{
		this.useBijectiveNumeration = useBijectiveNumeration;
		if (useBijectiveNumeration)
		{
			if (getBijectiveNumerationDigits() == null)
			{
				setBijectiveNumerationDigits(BIJECTIVE_DEFAULT_DIGITS);
			}
		}
		else
		{
			setScrambleTickets(false);
			setBijectiveNumerationDigits(null);
		}
	}
	/**
	 * The digits used for the generation of the {@link Ticket#getNumber()}s.
	 * 
	 * @return
	 */
	public char[] getBijectiveNumerationDigits()
	{
		return bijectiveNumerationDigits;
	}
	public void setBijectiveNumerationDigits(char[] bijectiveNumerationDigits)
	{
		if (isUseBijectiveNumeration() && bijectiveNumerationDigits == null)
		{
			throw new IllegalArgumentException("bijectiveNumerationDigits required when useBijectiveNumeration == true");
		}
		this.bijectiveNumerationDigits = bijectiveNumerationDigits;
		postLoad();
	}
	
	public BijectiveBaseKNumeration getBijectiveBaseKNumeration()
	{
		return bijectiveBaseKNumeration;
	}
	
	@PostLoad
	public void postLoad()
	{
		if (bijectiveNumerationDigits != null)
		{
			bijectiveBaseKNumeration = new BijectiveBaseKNumeration(bijectiveNumerationDigits);
		}
		else
		{
			bijectiveBaseKNumeration = null;
		}
	}
}
