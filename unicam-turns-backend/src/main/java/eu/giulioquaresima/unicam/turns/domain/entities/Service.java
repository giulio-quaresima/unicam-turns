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
	@ManyToOne
	private Location location;
}
