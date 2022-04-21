package eu.giulioquaresima.unicam.turns.domain.entities;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Optional;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.Predicate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.SortNatural;

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
	public TicketDispenser(String label, Owner owner)
	{
		this();
		this.label = label;
		this.owner = owner;
	}
	
	public Optional<Session> findFirstMatchingSession(Predicate<Session> predicate)
	{
		return sessions.stream().sorted().filter(predicate).findFirst();
	}
	public Session getCurrentSession(LocalDateTime localDateTime)
	{
		return findFirstMatchingSession(s -> s.isOpen(localDateTime)).orElse(null);
	}
	public Session getCurrentSession(Clock clock)
	{
		return findFirstMatchingSession(s -> s.isOpenNow(clock)).orElse(null);
	}
	
	public String getLabel()
	{
		return label;
	}
	public void setLabel(String label)
	{
		this.label = label;
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
