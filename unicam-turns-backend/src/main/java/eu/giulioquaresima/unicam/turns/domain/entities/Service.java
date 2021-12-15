package eu.giulioquaresima.unicam.turns.domain.entities;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * The kind of a service provided by the {@link Tenant}
 * in a {@link Location}.
 * 
 * @author Giulio Quaresima (giulio.quaresima--at--gmail.com)
 */
@Entity
public class Service extends AbstractEntity<Service>
{
	private String name;
	
	@ManyToOne
	private Location location;
	
	@ManyToOne
	private SessionScheduling sessionScheduler;

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public Location getLocation()
	{
		return location;
	}

	public void setLocation(Location location)
	{
		this.location = location;
	}

	public SessionScheduling getSessionScheduler()
	{
		return sessionScheduler;
	}

	public void setSessionScheduler(SessionScheduling sessionScheduler)
	{
		this.sessionScheduler = sessionScheduler;
	}
}
