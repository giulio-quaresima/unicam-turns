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
	private UUID uniqueIdentifier;
	
	@NotNull
	@Column (nullable = false)
	private LocalDateTime withrawTime;
	
	public Ticket()
	{
		super();
	}
	public Ticket(@NotNull UUID uniqueIdentifier)
	{
		this();
		this.uniqueIdentifier = uniqueIdentifier;
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
