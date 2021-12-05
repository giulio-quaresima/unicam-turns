package eu.giulioquaresima.unicam.turns.domain.entities;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * A source of tickets which regulates the access of a {@link Service}
 * provided by a {@link Location}: the ticket source is responsible
 * to generate tickets which users withdraw.
 * 
 * @author Giulio Quaresima (giulio.quaresima--at--gmail.com)
 */
@Entity
public class TicketSource extends AbstractEntity<TicketSource>
{
	@ManyToOne
	private Service service;
	
	@ManyToOne
	private TicketSourceConfiguration ticketSourceConfiguration;
	

}
