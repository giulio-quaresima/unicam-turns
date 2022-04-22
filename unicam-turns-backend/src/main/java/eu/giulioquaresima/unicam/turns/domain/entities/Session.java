package eu.giulioquaresima.unicam.turns.domain.entities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.validation.constraints.NotNull;

import org.springframework.lang.Nullable;

@Entity
public class Session extends AbstractEntity<Session>
{
	public static final Comparator<Session> START_END_TIME_COMPARATOR =
			Comparator.nullsLast(Comparator.comparing(Session::getStartTime, Comparator.nullsLast(Comparator.naturalOrder())))
			.thenComparing(Comparator.nullsLast(Comparator.comparing(Session::getEndTime, Comparator.nullsLast(Comparator.naturalOrder()))));
	
	@NotNull
	@ManyToOne (optional = false)
	private TicketDispenser ticketDispenser;
	
	@Nullable
	@Column
	private LocalDateTime startTime;
	
	@Nullable
	@Column
	private LocalDateTime endTime;
	
	@OneToMany (mappedBy = "session", cascade = CascadeType.ALL, orphanRemoval = true)
	@OrderColumn
	private List<Ticket> tickets = new ArrayList<>();
	
	@Column (nullable = false)
	private int currentTicketIndex = -1;
	
	@Column (name = "maximum_number", nullable = false)
	private int limit = Integer.MAX_VALUE;
		
	public boolean isOpen(LocalDateTime givenTime)
	{
		return 
				startTime != null
				&&
				!givenTime.isBefore(startTime) // Start inclusive
				&&
				(endTime == null || endTime.isAfter(givenTime)) // End exclusive
				&&
				tickets.size() < limit
				;
	}
	public boolean isOpen()
	{
		return isOpen(now());
	}
	
	public void start(LocalDateTime givenTime)
	{
		setStartTime(givenTime);
	}
	public void start()
	{
		start(now());
	}
	
	public void end(LocalDateTime givenTime)
	{
		setEndTime(givenTime);
	}
	public void end()
	{
		end(now());
	}
	
	/**
	 * Withdraw a ticket now.
	 * 
	 * @param clock The clock which returns the current time.
	 * 
	 * @return A valid ticket if the session {@link #isOpenNow(Clock)},
	 * otherwise <code>null</code>. 
	 */
	public Ticket withdraw(@Nullable User owner)
	{
		if (isOpen())
		{
			Ticket ticket = new Ticket(this, tickets.size(), UUID.randomUUID(), now(), owner);
			tickets.add(ticket);
			return ticket;
		}
		
		return null;
	}
	
	/**
	 * Withdraw anonymously a ticket now.
	 * 
	 * @param clock
	 * @return
	 */
	public Ticket withdraw()
	{
		return withdraw(null);
	}
	
	public Ticket currentTicket()
	{
		if (isOpen())
		{
			if (currentTicketIndex > -1 && currentTicketIndex < tickets.size())
			{
				return tickets.get(currentTicketIndex);
			}
		}
		
		return null;		
	}
	
	/**
	 * Draw the next ticket, if there are still any not drawn.
	 * 
	 * @param clock The clock which returns the current time.
	 * 
	 * @return The next ticket or <code>null</code> if tickets are finished or
	 * the session is closed.
	 */
	public Ticket draw()
	{
		if (isOpen())
		{
			int nextIndex = currentTicketIndex + 1;
			if (nextIndex < tickets.size())
			{
				return tickets.get(currentTicketIndex = nextIndex);
			}
		}
		
		return null;
	}
	
	@Override
	protected int compareNotEqual(Session otherEntity)
	{
		int compare = START_END_TIME_COMPARATOR.compare(this, otherEntity);
		if (compare == 0)
		{
			compare = super.compareNotEqual(otherEntity);
		}
		return compare;
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

	public int getCurrentTicketIndex()
	{
		return currentTicketIndex;
	}
	public void setCurrentTicketIndex(int currentTicketIndex)
	{
		this.currentTicketIndex = currentTicketIndex;
	}

	public int getLimit()
	{
		return limit;
	}
	public void setLimit(int limit)
	{
		this.limit = limit;
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
	
	protected LocalDateTime now()
	{
		if (ticketDispenser != null)
		{
			return ticketDispenser.now();
		}
		return LocalDateTime.now();
	}
}
