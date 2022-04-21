package eu.giulioquaresima.unicam.turns.domain.entities;

import java.time.LocalDateTime;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

@Entity
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

	public int getNumber()
	{
		return number;
	}
	public void setNumber(int number)
	{
		this.number = number;
	}
	/**
	 * Tickets generally starts from 1.
	 * 
	 * @return
	 */
	public long getHumanFriendlyNumber()
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
	
	@NotNull
	public LocalDateTime getWithdrawTime()
	{
		return withdrawTime;
	}
	public void setWithdrawTime(LocalDateTime withdrawTime)
	{
		this.withdrawTime = withdrawTime;
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
