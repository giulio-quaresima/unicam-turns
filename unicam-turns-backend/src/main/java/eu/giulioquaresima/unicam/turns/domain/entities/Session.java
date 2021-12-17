package eu.giulioquaresima.unicam.turns.domain.entities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.validation.constraints.NotNull;

import eu.giulioquaresima.unicam.turns.utils.BijectiveBaseKNumeration;

/**
 * A work session for a service: this entity maintain the state
 * of the session and acts as a source of the tickets used to regulate 
 * the access to the service: being the information expert, all the 
 * ticket management is done by this entity.
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
	
	@ElementCollection
	@Column (length = 8)
	@OrderColumn
	private List<String> ticketNumbers = new ArrayList<>();
	
	private Integer lastWithdrawnTicket;

	private Integer lastDrawnTicket = -1;
	
	@OneToMany (mappedBy = "session", cascade = CascadeType.ALL)
	@OrderColumn (name = "position")
	private List<Ticket> tickets = new ArrayList<>();

	public Ticket withraw(User user)
	{
		Ticket ticket = new Ticket();
		
		ticket.setSession(this);
		ticket.setUser(user);
		ticket.setWithdrawTime(LocalDateTime.now());		
		ticket.setNumber(pollNewNumber());
		
		tickets.add(ticket);
		lastWithdrawnTicket = tickets.size() - 1;
		
		return ticket;
	}
	
	public Ticket draw(ServiceReception serviceReception)
	{
		for (int position = (lastDrawnTicket + 1); position < tickets.size(); position++)
		{
			Ticket ticket = tickets.get(position);
			if (ticket.isDrawnable())
			{
				lastDrawnTicket = position;
				return ticket;
			}
		}
		return null;
	}

	protected String pollNewNumber()
	{
		String number = null;
		
		TicketSourceConfiguration ticketSourceConfiguration = sessionConfiguration.getTicketSourceConfiguration();
		BijectiveBaseKNumeration bijectiveBaseKNumeration = ticketSourceConfiguration.getBijectiveBaseKNumeration();
		
		if (ticketSourceConfiguration.isScrambleTickets())
		{
			Objects.requireNonNull(bijectiveBaseKNumeration, "Illegal state in TicketSourceConfiguration");
			if (!ticketNumbers.isEmpty())
			{
				// number = ticketNumbers.remove(0); // Worse performance!
				number = ticketNumbers.remove(ticketNumbers.size() - 1);
			}
			if (ticketNumbers.isEmpty())
			{
				int newTicketNumberLength = number == null ? 2 : number.length() + 1;
				List<String> numbers = bijectiveBaseKNumeration
						.sequence(bijectiveBaseKNumeration.sequenceRangeInterval(newTicketNumberLength))
						.collect(Collectors.toList());
				Collections.shuffle(numbers);
				numbers.forEach(ticketNumbers::add);
			}
			if (number == null)
			{
				number = ticketNumbers.remove(ticketNumbers.size() - 1);
			}
		}
		else
		{
			long lastValue = tickets.size();
			
			if (ticketSourceConfiguration.isUseBijectiveNumeration())
			{
				number = bijectiveBaseKNumeration.format(lastValue + 1);
			}
			else
			{
				number = Long.valueOf(lastValue + 1).toString();
			}
		}
		
		return number;
	}

	public Service getService()
	{
		return service;
	}
	public void setService(Service service)
	{
		this.service = service;
	}

	public SessionConfiguration getSessionConfiguration()
	{
		return sessionConfiguration;
	}
	public void setSessionConfiguration(SessionConfiguration sessionConfiguration)
	{
		this.sessionConfiguration = sessionConfiguration;
	}

	public String getLabel()
	{
		return label;
	}
	public void setLabel(String label)
	{
		this.label = label;
	}

	public LocalDateTime getStartTime()
	{
		return startTime;
	}
	public void setStartTime(LocalDateTime startTime)
	{
		this.startTime = startTime;
	}

	public LocalDateTime getEndTime()
	{
		return endTime;
	}
	public void setEndTime(LocalDateTime endTime)
	{
		this.endTime = endTime;
	}

	public List<String> getTicketNumbers()
	{
		return ticketNumbers;
	}
	public void setTicketNumbers(List<String> ticketNumbers)
	{
		this.ticketNumbers = ticketNumbers;
	}

	public Integer getLastWithdrawnTicket()
	{
		return lastWithdrawnTicket;
	}
	public void setLastWithdrawnTicket(Integer lastWithdrawnTicket)
	{
		this.lastWithdrawnTicket = lastWithdrawnTicket;
	}

	public Integer getLastDrawnTicket()
	{
		return lastDrawnTicket;
	}
	public void setLastDrawnTicket(Integer lastDrawnTicket)
	{
		this.lastDrawnTicket = lastDrawnTicket;
	}

	public List<Ticket> getTickets()
	{
		return tickets;
	}
	public void setTickets(List<Ticket> tickets)
	{
		this.tickets = tickets;
	}

}
