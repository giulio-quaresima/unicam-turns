package eu.giulioquaresima.unicam.turns.domain.entities;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;

@Embeddable
public class Ticket
{
	@NotNull
	@Column (nullable = false)
	private int number;
	
	@NotNull
	@Column (nullable = false, unique = true)
	private UUID uniqueIdentifier;
	
	@NotNull
	@Column (nullable = false)
	private LocalDateTime withrawTime;
	
	public Ticket()
	{
		super();
	}
	public Ticket(@NotNull int number, @NotNull UUID uniqueIdentifier, @NotNull LocalDateTime withrawTime)
	{
		super();
		this.number = number;
		this.uniqueIdentifier = uniqueIdentifier;
		this.withrawTime = withrawTime;
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
	public LocalDateTime getWithrawTime()
	{
		return withrawTime;
	}
	public void setWithrawTime(LocalDateTime withrawTime)
	{
		this.withrawTime = withrawTime;
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
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Ticket other = (Ticket) obj;
		return Objects.equals(uniqueIdentifier, other.uniqueIdentifier);
	}
	
}
