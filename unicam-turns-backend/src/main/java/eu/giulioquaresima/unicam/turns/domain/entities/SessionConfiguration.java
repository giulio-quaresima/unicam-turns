package eu.giulioquaresima.unicam.turns.domain.entities;

import javax.persistence.Embedded;
import javax.persistence.Entity;

/**
 * 
 * 
 * @author Giulio Quaresima (giulio.quaresima--at--gmail.com)
 */
@Entity
public class SessionConfiguration extends AbstractEntity<SessionConfiguration>
{
	@Embedded
	private TicketSourceConfiguration ticketSourceConfiguration;

	public TicketSourceConfiguration getTicketSourceConfiguration()
	{
		return ticketSourceConfiguration;
	}

	public void setTicketSourceConfiguration(TicketSourceConfiguration ticketSourceConfiguration)
	{
		this.ticketSourceConfiguration = ticketSourceConfiguration;
	}
}
