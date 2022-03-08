package eu.giulioquaresima.unicam.turns.domain.entities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OrderColumn;
import javax.validation.constraints.NotNull;

import io.github.resilience4j.core.lang.Nullable;

@Entity
public class Session extends AbstractEntity<Session>
{
	@NotNull
	@ManyToOne (optional = false)
	private TicketDispenser ticketDispenser;
	
	@NotNull
	@Column
	private LocalDateTime startTime;
	
	@Nullable
	@Column
	private LocalDateTime endTime;
	
	@ElementCollection
	@OrderColumn
	private List<Ticket> tickets = new ArrayList<>();
	
	@Column
	private int currentTicket = -1;
	
	public Ticket withraw(LocalDateTime withrawTime)
	{
		if (startTime == null)
		{
			throw new IllegalStateException("The session has not been started");
		}
		if (endTime != null && withrawTime.isAfter(endTime))
		{
			throw new IllegalStateException("The session has been closed");
		}
		
		Ticket ticket = new Ticket();
		ticket.setUniqueIdentifier(UUID.randomUUID());
		ticket.setWithrawTime(withrawTime);
		tickets.add(ticket);
		currentTicket++;
		
		return ticket;
	}

	@NotNull
	public TicketDispenser getTicketDispenser()
	{
		return ticketDispenser;
	}
	public void setTicketDispenser(TicketDispenser ticketDispenser)
	{
		this.ticketDispenser = ticketDispenser;
	}

	@NotNull
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

	public List<Ticket> getTickets()
	{
		return tickets;
	}
	public void setTickets(List<Ticket> tickets)
	{
		this.tickets = tickets;
	}

	public int getCurrentTicket()
	{
		return currentTicket;
	}
	public void setCurrentTicket(int currentTicket)
	{
		this.currentTicket = currentTicket;
	}

	@Override
	protected Session castOrNull(Object obj)
	{
		if (obj instanceof Session)
		{
			return (Session) obj;
		}
		return null;
	}
}
