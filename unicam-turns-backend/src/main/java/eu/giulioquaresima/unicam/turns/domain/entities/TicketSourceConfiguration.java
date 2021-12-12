package eu.giulioquaresima.unicam.turns.domain.entities;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;

/**
 * 
 * 
 * @author Giulio Quaresima (giulio.quaresima--at--gmail.com)
 */
@Embeddable
public class TicketSourceConfiguration
{
	@NotNull
	@Column (nullable = false)
	private boolean useBijectiveNumeration = true;
	private char[] numberDigits = "123456789ABCDEFGHX".toCharArray();
	private boolean postponeEnabled = true;
	private boolean scrambleTickets = true;
	
	/**
	 * If <code>true</code> the {@link Ticket#getNumber()}s will be generated
	 * using a <em>bijective</em> numeral system, otherwise, a standard positional
	 * system is used.
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
	}
	/**
	 * The digits used for the generation of the {@link Ticket#getNumber()}s.
	 * 
	 * @return
	 */
	public char[] getNumberDigits()
	{
		return numberDigits;
	}
	public void setNumberDigits(char[] numberDigits)
	{
		this.numberDigits = numberDigits;
	}
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
	}
}
