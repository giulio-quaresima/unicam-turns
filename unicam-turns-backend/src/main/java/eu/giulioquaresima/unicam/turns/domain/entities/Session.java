package eu.giulioquaresima.unicam.turns.domain.entities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.validation.constraints.NotNull;

/**
 * A work session for a service: this entity maintain the state
 * of the session and acts as a source of tickets which regulates 
 * the access to the service: being the information expert, all the 
 * ticket management pass through this entity.
 * 
 * @author Giulio Quaresima (giulio.quaresima--at--gmail.com)
 */
@Entity
public class Session extends AbstractEntity<Session>
{
	@ManyToOne
	private Service service;
	
	@ManyToOne
	private SessionConfiguration sessionConfiguration;
	
	@NotNull
	private String label;
	
	@NotNull
	private LocalDateTime startTime;
	
	private LocalDateTime endTime;

	@OneToMany (mappedBy = "session", cascade = CascadeType.ALL)
	@OrderColumn (name = Ticket.INDEX_COLUMN)
	private List<Ticket> tickets = new ArrayList<>();
	
	@OneToMany
	@MapKey (name = "assignedReception")
	private Map<ServiceReception, Ticket> lastDrawnTickets = new HashMap<>();
	
	public Ticket withraw(User user)
	{
		TicketSourceConfiguration ticketSourceConfiguration = sessionConfiguration.getTicketSourceConfiguration();
		if (ticketSourceConfiguration.isScrambleTickets() || ticketSourceConfiguration.isPostponeEnabled())
		{
			// TODO
		}
		else
		{
			 
			Ticket ticket = new Ticket();
			ticket.setSession(this);
		}
		return null; // TODO
	}
	
	

}
