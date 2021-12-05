package eu.giulioquaresima.unicam.turns.domain.entities;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * 
 * 
 * @author Giulio Quaresima (giulio.quaresima--at--gmail.com)
 */
@Entity
public class Session extends AbstractEntity<Session>
{
	@ManyToOne
	private TicketSource ticketSource;
	
	private String name;
	
	private LocalDateTime startTime;
	
	private LocalDateTime endTime;
}
