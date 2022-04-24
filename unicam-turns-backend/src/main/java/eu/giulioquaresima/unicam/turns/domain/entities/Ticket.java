package eu.giulioquaresima.unicam.turns.domain.entities;

import java.time.LocalDateTime;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonView;

import eu.giulioquaresima.unicam.turns.rest.json.JsonViews;

@Entity
@JsonView (JsonViews.Default.class)
public class Ticket extends AbstractEntity<Ticket>
{
	@ManyToOne (optional = false)
	@JoinColumn (nullable = false)
	private Session session;
	
	@NotNull
	@Column (nullable = false)
	private int number;
	
	@NotNull
	@Column (nullable = false, unique = true)
	private UUID uniqueIdentifier;
	
	@NotNull
	@Column (nullable = false)
	private LocalDateTime withdrawTime;
	
	@NotNull
	@Column (nullable = false)
	private boolean cancelled = false;
	
	@ManyToOne (optional = true)
	private User owner;
	
	public Ticket()
	{
		super();
	}
	public Ticket(
			@NotNull Session session,
			@NotNull int number, 
			@NotNull UUID uniqueIdentifier, 
			@NotNull LocalDateTime withrawTime, User owner)
	{
		super();
		this.session = session;
		this.number = number;
		this.uniqueIdentifier = uniqueIdentifier;
		this.withdrawTime = withrawTime;
		this.owner = owner;
	}
	
	/**
	 * @return <code>true</code> iff !{@link #isCancelled()} and 
	 * {@link Session#isOpen()}. 
	 */
	public boolean isAlive()
	{
		return !isCancelled() && session != null && session.isOpen();
	}
	
	/**
	 * @return <code>true</code> iff {@link #isAlive()}
	 * and this ticket has not yet been drew.
	 */
	public boolean isWaiting()
	{
		if (isAlive())
		{
			Ticket lastDrewTicket = session.getLastDrewTicket();
			return lastDrewTicket == null || lastDrewTicket.getNumber() < getNumber();
		}
		return false;
	}

	/**
	 * @return <code>true</code> if this number has been just called: hurry up!
	 */
	public boolean isCurrent()
	{
		if (isAlive())
		{
			return equals(session.getLastDrewTicket());
		}
		return false;
	}

	/**
	 * @return <code>true</code> iff {@link #isAlive()}
	 * and this ticket has expired, i.e. the current drew number
	 * is after this number.
	 */
	public boolean isExpired()
	{
		if (isAlive())
		{
			Ticket lastDrewTicket = session.getLastDrewTicket();
			return lastDrewTicket != null && lastDrewTicket.getNumber() > getNumber();
		}
		return false;
	}
	
	/**
	 * This is the ticket number that can be showed to users,
	 * and depends from {@link #getNumber()}.
	 * 
	 * @return
	 */
	public long getPublicNumber()
	{
		return ((long) getNumber()) + 1L;
	}
	
	public UUID getUniqueIdentifier()
	{
		return uniqueIdentifier;
	}
	public void setUniqueIdentifier(UUID uniqueIdentifier)
	{
		this.uniqueIdentifier = uniqueIdentifier;
	}
	
	public int getNumber()
	{
		return number;
	}
	public void setNumber(int number)
	{
		this.number = number;
	}
	
	@NotNull
	public LocalDateTime getWithdrawTime()
	{
		return withdrawTime;
	}
	public void setWithdrawTime(LocalDateTime withdrawTime)
	{
		this.withdrawTime = withdrawTime;
	}
	
	public boolean isCancelled()
	{
		return cancelled;
	}
	public void setCancelled(boolean cancelled)
	{
		this.cancelled = cancelled;
	}
	public void cancel()
	{
		setCancelled(true);
	}
	
	@JsonView (JsonViews.TicketToSession.class)
	public Session getSession()
	{
		return session;
	}
	public void setSession(Session session)
	{
		this.session = session;
	}
	
	public User getOwner()
	{
		return owner;
	}
	public void setOwner(User owner)
	{
		this.owner = owner;
	}
	
	@Override
	public int hashCode()
	{
		if (uniqueIdentifier != null)
		{
			return uniqueIdentifier.hashCode();
		}
		return 0;
	}
	
	@Override
	protected Ticket castOrNull(Object obj)
	{
		if (obj instanceof Ticket)
		{
			return (Ticket) obj;
		}
		return null;
	}
	
}
