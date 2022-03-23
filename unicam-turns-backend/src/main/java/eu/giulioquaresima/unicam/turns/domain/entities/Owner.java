package eu.giulioquaresima.unicam.turns.domain.entities;

import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.SortNatural;

/**
 * The virtual owner of one ore more ticket dispensers: more real people can act impersonating
 * the owner; the owner can control the whole lifecycle of the ticket dispensers he owns, starting and
 * stopping {@link Session}s and drawing tickets.
 * 
 * @author Giulio Quaresima (giulio.quaresima--at--gmail.com)
 */
@Entity
@Table (schema = AbstractEntity.SCHEMA_NAME)
public class Owner extends AbstractEntity<Owner>
{
	public static final Comparator<Owner> NAME_COMPARATOR =
			Comparator.nullsLast(Comparator.comparing(Owner::getName, Comparator.nullsLast(Comparator.naturalOrder())));
	
	@NotNull
	@Column (nullable = false)
	private String name;
	
	@SortNatural
	@OneToMany (mappedBy = "owner")
	private SortedSet<TicketDispenser> ticketDispensers = new TreeSet<>();

	public Owner()
	{
		super();
	}

	public Owner(@NotNull String name)
	{
		this();
		this.name = name;
	}

	@Override
	protected int compareNotEqual(Owner otherEntity)
	{
		int compare = NAME_COMPARATOR.compare(this, otherEntity);
		if (compare == 0)
		{
			compare = super.compareNotEqual(otherEntity);
		}
		return compare;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public SortedSet<TicketDispenser> getTicketDispensers()
	{
		return ticketDispensers;
	}

	public void setTicketDispensers(SortedSet<TicketDispenser> ticketDispensers)
	{
		this.ticketDispensers = ticketDispensers;
	}

	@Override
	protected Owner castOrNull(Object obj)
	{
		if (obj instanceof Owner)
		{
			return (Owner) obj;
		}
		return null;
	}

}
