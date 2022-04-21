package eu.giulioquaresima.unicam.turns.domain.entities;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.SortNatural;

/**
 * The virtual owner of one ore more ticket dispensers: one or more real people can act impersonating
 * the same owner; the owner can control the whole lifecycle of the ticket dispensers he owns, starting and
 * stopping {@link Session}s and drawing tickets.
 * 
 * @author Giulio Quaresima (giulio.quaresima--at--gmail.com)
 */
@Entity
public class Owner extends AbstractEntity<Owner>
{
	public static final Comparator<Owner> NAME_COMPARATOR =
			Comparator.nullsLast(Comparator.comparing(Owner::getName, Comparator.nullsLast(Comparator.naturalOrder())));
	
	@NotNull
	@Column (nullable = false)
	private String name;
	
	@ManyToMany (mappedBy = "owners")
	private Set<User> ownersUsers = new HashSet<>();
	
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

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public Set<User> getOwnersUsers()
	{
		return ownersUsers;
	}

	public void setOwnersUsers(Set<User> ownersUsers)
	{
		this.ownersUsers = ownersUsers;
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

}
