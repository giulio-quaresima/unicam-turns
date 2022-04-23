package eu.giulioquaresima.unicam.turns.domain.entities;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.SortNatural;
import org.springframework.lang.Nullable;

import com.fasterxml.jackson.annotation.JsonIgnore;

import eu.giulioquaresima.unicam.turns.domain.entities.converters.ZoneIdConverter;

/**
 * This entity identifies a virtual dispenser of digital tickets.
 * 
 * @author Giulio Quaresima (giulio.quaresima--at--gmail.com)
 */
@Entity
public class TicketDispenser extends AbstractEntity<TicketDispenser>
{
	@NotNull
	@Column (nullable = false)
	private String label;
	
	@NotNull
	@Column (nullable = false)
	@Convert (converter = ZoneIdConverter.class)
	private ZoneId zoneId;
	
	@ManyToOne (optional = false)
	private Owner owner;
	
	@OneToMany (mappedBy = "ticketDispenser")
	@SortNatural
	private SortedSet<Session> sessions = new TreeSet<>();

	public Session createSession()
	{
		Session session = new Session();
		session.setTicketDispenser(this);
		return session;
	}
	
	public TicketDispenser()
	{
		super();
	}
	public TicketDispenser(String label, ZoneId zoneId, Owner owner)
	{
		this();
		this.label = label;
		this.zoneId = zoneId;
		this.owner = owner;
	}

	@Nullable
	@JsonIgnore
	public Session getLastSession()
	{
		if (sessions != null && !sessions.isEmpty())
		{
			return sessions.last();
		}
		return null;
	}
	@Nullable
	public Session getCurrentSession(LocalDateTime localDateTime)
	{
		Session session = getLastSession();
		if (session != null && session.isOpen(localDateTime))
		{
			return session;
		}
		return null;
	}
	@Nullable
	@JsonIgnore
	public Session getCurrentSession()
	{
		return getCurrentSession(now());
	}
	
	public String getLabel()
	{
		return label;
	}
	public void setLabel(String label)
	{
		this.label = label;
	}

	public ZoneId getZoneId()
	{
		return zoneId;
	}
	public void setZoneId(ZoneId zoneId)
	{
		this.zoneId = zoneId;
	}
	
	@JsonIgnore
	public Clock getClock()
	{
		if (zoneId != null)
		{
			return Clock.system(zoneId);
		}
		return Clock.systemDefaultZone();
	}
	
	protected LocalDateTime now()
	{
		return LocalDateTime.now(getClock());
	}

	public Owner getOwner()
	{
		return owner;
	}
	public void setOwner(Owner owner)
	{
		this.owner = owner;
	}

	@Override
	protected TicketDispenser castOrNull(Object obj)
	{
		if (obj instanceof TicketDispenser)
		{
			return (TicketDispenser) obj;
		}
		return null;
	}
	
	protected static final Comparator<TicketDispenser> LABEL_COMPARATOR = Comparator.nullsLast(
			Comparator.comparing(TicketDispenser::getLabel, Comparator.nullsLast(String::compareToIgnoreCase))
			)
			;

	@Override
	protected int compareNotEqual(TicketDispenser otherEntity)
	{
		int compare = LABEL_COMPARATOR.compare(this, otherEntity);
		if (compare == 0)
		{
			compare = super.compareNotEqual(otherEntity);
		}
		return compare;
	}

}
