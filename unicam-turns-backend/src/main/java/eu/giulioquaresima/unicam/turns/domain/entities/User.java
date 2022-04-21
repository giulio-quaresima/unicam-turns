package eu.giulioquaresima.unicam.turns.domain.entities;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.NaturalId;

@Entity
public class User extends AbstractEntity<User>
{
	@NotNull
	@Column (nullable = false)
	@NaturalId
	private String username;
	
	@ManyToMany
	private Set<Owner> owners = new HashSet<>();
	
	@OneToMany (mappedBy = "owner")
	@OrderBy ("id")
	private Set<Ticket> tickets;
	
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
