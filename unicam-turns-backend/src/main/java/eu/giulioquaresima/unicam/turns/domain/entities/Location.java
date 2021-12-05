package eu.giulioquaresima.unicam.turns.domain.entities;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * An abstract location where something is provided to users
 * regulating the users' access with the turn system.
 * 
 * Any {@link Tenant} may have one ore more {@link Location}s.
 * 
 * @author Giulio Quaresima (giulio.quaresima--at--gmail.com)
 */
@Entity
public class Location extends AbstractEntity<Location>
{
	@ManyToOne
	private Tenant tenant;
}
