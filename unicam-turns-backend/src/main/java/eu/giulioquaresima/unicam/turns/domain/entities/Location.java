package eu.giulioquaresima.unicam.turns.domain.entities;

import java.time.ZoneId;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

/**
 * An abstract location where some services are provided to users
 * regulating the users' access with the turn system.
 * 
 * Any {@link Tenant} may have one ore more {@link Location}s.
 * 
 * @author Giulio Quaresima (giulio.quaresima--at--gmail.com)
 */
@Entity
public class Location extends AbstractEntity<Location>
{
	private String name;
	
	@ManyToOne
	private Tenant tenant;
	
	@NotNull
	private String zoneId;
	
	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public ZoneId getTimeZone()
	{
		if (zoneId != null)
		{
			return ZoneId.of(zoneId);
		}
		return null;
	}
	
	public void setTimeZone(ZoneId timeZone)
	{
		if (timeZone != null)
		{
			zoneId = timeZone.getId();
		}
		else
		{
			zoneId = null;
		}
	}
	
	public Tenant getTenant()
	{
		return tenant;
	}

	public void setTenant(Tenant tenant)
	{
		this.tenant = tenant;
	}

	public String getZoneId()
	{
		return zoneId;
	}

	public void setZoneId(String zoneId)
	{
		this.zoneId = zoneId;
	}

}
