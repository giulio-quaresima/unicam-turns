package eu.giulioquaresima.unicam.turns.domain.entities;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.NaturalId;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;

import eu.giulioquaresima.unicam.turns.rest.json.JsonViews;

/**
 * 
 * 
 * @author Giulio Quaresima (giulio.quaresima--at--gmail.com)
 */
@Entity
@JsonView (JsonViews.Default.class)
public class User extends AbstractEntity<User>
{
	@NotNull
	@Column (nullable = false)
	@NaturalId
	private String username;
	
	// Column "OWNERS0_.OWNERS_USERS_ID" not found
	@ManyToMany
	@JoinTable (
			joinColumns = @JoinColumn (name = "user_id"),
			inverseJoinColumns = @JoinColumn (name = "owners_id")
			)
	@JsonIgnore
	private Set<Owner> owners = new HashSet<>();
	
	@OneToMany (mappedBy = "owner")
	@OrderBy ("id")
	private Set<Ticket> tickets;
	
	@Transient
	public boolean owns(TicketDispenser ticketDispenser)
	{
		for (Owner owner : owners)
		{
			for (TicketDispenser ownTicketDispenser : owner.getTicketDispensers())
			{
				if (ownTicketDispenser.equals(ticketDispenser))
				{
					return true;
				}
			}
		}
		return false;
	}
	
	public String getUsername()
	{
		return username;
	}
	public void setUsername(String username)
	{
		this.username = username;
	}

	public Set<Owner> getOwners()
	{
		return owners;
	}
	public void setOwners(Set<Owner> owners)
	{
		this.owners = owners;
	}

	@Override
	protected User castOrNull(Object obj)
	{
		if (obj instanceof User)
		{
			return (User) obj;
		}
		return null;
	}

}
