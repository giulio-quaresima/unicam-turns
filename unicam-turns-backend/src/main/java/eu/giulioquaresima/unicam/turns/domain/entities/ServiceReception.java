package eu.giulioquaresima.unicam.turns.domain.entities;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * A place where the user is targeted when it's its turn.
 * 
 * @author Giulio Quaresima (giulio.quaresima--at--gmail.com)
 */
@Entity
public class ServiceReception extends AbstractEntity<ServiceReception>
{
	@ManyToOne
	private Service service;
	
	private String label;
}
