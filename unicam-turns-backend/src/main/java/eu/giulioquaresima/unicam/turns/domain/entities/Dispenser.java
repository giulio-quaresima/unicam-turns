package eu.giulioquaresima.unicam.turns.domain.entities;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * A ticket dispenser installed in a {@link Location}.
 * 
 * @author Giulio Quaresima (giulio.quaresima--at--gmail.com)
 */
@Entity
public class Dispenser extends AbstractEntity<Dispenser>
{
	@ManyToOne
	private Location location;
	
	@ManyToOne // TODO Forse non mi serve
	private Ticket next;
}
