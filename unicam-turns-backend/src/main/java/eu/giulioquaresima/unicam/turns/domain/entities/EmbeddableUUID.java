package eu.giulioquaresima.unicam.turns.domain.entities;

import java.util.Objects;
import java.util.UUID;

import javax.persistence.Embeddable;

@Embeddable
public class EmbeddableUUID
{
	private long uuidMostSigBits;	
	private long uuidLeastSigBits;
	
	public EmbeddableUUID()
	{
		super();
	}
	public EmbeddableUUID(long uuidMostSigBits, long uuidLeastSigBits)
	{
		this();
		this.uuidMostSigBits = uuidMostSigBits;
		this.uuidLeastSigBits = uuidLeastSigBits;
	}
	public EmbeddableUUID(UUID uuid)
	{
		this(uuid.getMostSignificantBits(), uuid.getLeastSignificantBits());
	}
	
	public UUID toUUID()
	{
		return new UUID(uuidMostSigBits, uuidLeastSigBits);
	}

	
	@Override
	public int hashCode()
	{
		return Objects.hash(uuidLeastSigBits, uuidMostSigBits);
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
		EmbeddableUUID other = (EmbeddableUUID) obj;
		return uuidLeastSigBits == other.uuidLeastSigBits && uuidMostSigBits == other.uuidMostSigBits;
	}
	
	public long getUuidMostSigBits()
	{
		return uuidMostSigBits;
	}
	public void setUuidMostSigBits(long uuidMostSigBits)
	{
		this.uuidMostSigBits = uuidMostSigBits;
	}
	public long getUuidLeastSigBits()
	{
		return uuidLeastSigBits;
	}
	public void setUuidLeastSigBits(long uuidLeastSigBits)
	{
		this.uuidLeastSigBits = uuidLeastSigBits;
	}
}
