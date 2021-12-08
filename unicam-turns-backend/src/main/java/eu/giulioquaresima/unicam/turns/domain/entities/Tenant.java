package eu.giulioquaresima.unicam.turns.domain.entities;

import javax.persistence.Entity;

/**
 * The tenant, i.e. the owner of the {@link Location}s where
 * the turns service will be used.
 * 
 * @author Giulio Quaresima (giulio.quaresima--at--gmail.com)
 */
@Entity
public class Tenant extends AbstractEntity<Tenant>
{
	private String name;

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}
}
