package eu.giulioquaresima.unicam.turns.domain.entities;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * A source of tickets which regulates the access of a service
 * provided by a Location: the ticket source is responsible
 * to generate tickets
 * 
 * @author Giulio Quaresima (giulio.quaresima--at--gmail.com)
 */
@Entity
public class TicketSource extends AbstractEntity<TicketSource>
{
	@ManyToOne
	private Location location;
	

}
