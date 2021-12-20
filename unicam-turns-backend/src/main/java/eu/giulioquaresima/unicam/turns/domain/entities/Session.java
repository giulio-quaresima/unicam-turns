package eu.giulioquaresima.unicam.turns.domain.entities;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

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
	private static final Logger LOGGER = LoggerFactory.getLogger(Session.class);
	
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
	
	private int lastDrawnTicketPosition = -1;
	
	private Duration estimatedAverageWaitingDuration;
	
	@OneToMany (mappedBy = "session", cascade = CascadeType.ALL)
	@OrderColumn (name = "position")
	private List<Ticket> tickets = new ArrayList<>();
	
	/**
	 * Get the set of positioned tickets which pass the {@code filter} test
	 * and fall inside the [from,to) interval.
	 * 
	 * @param filter The filter or criterion.
	 * 
	 * @param from The start position (inclusive).
	 * 
	 * @param to The end position (exclusive).
	 * 
	 * @return Never <code>null</code>.
	 */
	public List<PositionedTicket> positionedTickets(Predicate<Ticket> filter, int from, int to)
	{
		List<PositionedTicket> positionedTickets = new ArrayList<>();
		from = Math.max(from, 0);
		to = Math.min(to, tickets.size());
		for (int position = from; position < to; position++)
		{
			Ticket ticket = tickets.get(position);
			if (filter.test(ticket))
			{
				positionedTickets.add(new PositionedTicket(position, ticket));
			}
		}
		return positionedTickets;
	}
	
	/**
	 * Withdraw a ticket (the same act
	 * as pulling the ticket from a dispenser).
	 * 
	 * @param user The user who withdrew the ticket.
	 * 
	 * @param cancelPrevious If <code>true</code>, the previous
	 * waiting ticket, if any, will be cancelled.
	 * 
	 * @return The new ticket which has {@code user} 
	 * as owner, a number, a withdraw time, and so is
	 * in its "waiting" state: if the user already owns a waiting
	 * ticket, if {@code cancelPrevious} the previous ticket is cancelled,
	 * otherwise that previous ticket is returned.
	 */
	public PositionedTicket withraw(User user, boolean cancelPrevious)
	{
		PositionedTicket positionedTicket = waitingTicket(user);
		if (positionedTicket != null)
		{
			if (cancelPrevious)
			{
				positionedTicket.getTicket().cancel();
			}
			else
			{
				if (LOGGER.isDebugEnabled())
				{
					LOGGER.debug("The user {} is triying to withraw a ticket while already owns a waiting ticket: I return the currently waiting ticket.");
				}
				return positionedTicket;
			}
		}
		
		Ticket ticket = new Ticket();
		
		ticket.setSession(this);
		ticket.setOwner(user);
		ticket.setWithdrawTime(LocalDateTime.now());		
		ticket.setNumber(pollNewNumber());
		
		positionedTicket = new PositionedTicket(tickets.size(), ticket);
		
		tickets.add(ticket);
		
		return positionedTicket;
	}
	
	/**
	 * The act of drawing the next waiting ticket assigning
	 * it to a service reception
	 * 
	 * @param serviceReception The service reception to assign
	 * the ticket to.
	 * 
	 * @return A no longer waiting ticket targeted to a service reception,
	 * or <code>null</code> if there are no longer waiting tickets.
	 */
	public PositionedTicket draw(ServiceReception serviceReception)
	{
		for (int position = (lastDrawnTicketPosition + 1); position < tickets.size(); position++)
		{
			Ticket ticket = tickets.get(position);
			if (ticket.isWaiting())
			{
				lastDrawnTicketPosition = position;
				ticket.setServiceReception(serviceReception);
				ticket.setDrawTime(LocalDateTime.now());
				
				Duration duration = Duration.between(ticket.getWithdrawTime(), ticket.getDrawTime());
				if (estimatedAverageWaitingDuration == null)
				{
					estimatedAverageWaitingDuration = duration;
				}
				else
				{
					estimatedAverageWaitingDuration = duration
							.plus(estimatedAverageWaitingDuration.multipliedBy(lastDrawnTicketPosition-1))
							.dividedBy(lastDrawnTicketPosition);
				}
				
				return new PositionedTicket(lastDrawnTicketPosition, ticket);
			}
		}
		
		return null;
	}

	/**
	 * Get the set of indices of the tickets which are owned
	 * by the passed user.
	 * 
	 * @param user The sought owner.
	 * 
	 * @return The owned tickets, empty if no ticket is owned.
	 * 
	 * @see Ticket#isOwner(User)
	 */
	public List<PositionedTicket> ownedTickets(User user)
	{
		if (user != null)
		{
			return positionedTickets(ticket -> ticket.isOwner(user), 0, tickets.size());
		}
		return Collections.emptyList();
	}
	
	/**
	 * Get if exists the the ticket which is owned by the passed user 
	 * AND is waiting.
	 * 
	 * @param user The sought owner.
	 * 
	 * @return The only possible waiting ticket owned by {@code user}.
	 * 
	 * @see Ticket#isWaiting()
	 */
	public PositionedTicket waitingTicket(User user)
	{
		if (user != null)
		{
			Predicate<Ticket> filter = ticket -> ticket.isOwner(user);
			filter = filter.and(ticket -> ticket.isWaiting());
			List<PositionedTicket> positionedTickets = positionedTickets(
					filter, 
					lastDrawnTicketPosition + 1, // The drawn tickets are not alive 
					tickets.size());
			Assert.state(positionedTickets.size() < 2, "At most one ticket per user can be in the \"waiting\" state: there must be an error in state management of tickets");
			if (!positionedTickets.isEmpty())
			{
				return positionedTickets.get(0);
			}
		}
		return null;		
	}

	/**
	 * Cancel the owned waiting ticket if any.
	 * 
	 * @param user
	 * 
	 * @return The cancelled ticket, or <code>null</code> if
	 * the user owns no waiting ticket.
	 */
	public PositionedTicket cancel(User user)
	{
		PositionedTicket positionedTicket = waitingTicket(user);
		if (positionedTicket != null)
		{
			positionedTicket.getTicket().cancel();
		}
		return positionedTicket;
	}
	
	/**
	 * Postpone the waiting ticket of the user (if any) of
	 * at most {@code slots} slots.
	 * 
	 * @param user
	 * 
	 * @param slots
	 * 
	 * @return The ticket with the new position.
	 */
	public PositionedTicket postpone(User user, int slots)
	{
		if (slots < 0)
		{
			throw new IllegalArgumentException("Cannot skip the queue!!!");
		}
		
		PositionedTicket positionedTicket = waitingTicket(user);
		
		if (positionedTicket != null)
		{
			if (!sessionConfiguration.getTicketSourceConfiguration().isPostponeEnabled())
			{
				throw new UnsupportedOperationException("This function is not enabled for this session");
			}
			
			int actualPosition = positionedTicket.getPosition();
			int newPosition = Math.min(actualPosition + slots, (tickets.size() - 1));
			if (newPosition > actualPosition)
			{
				tickets.add(newPosition, tickets.remove(actualPosition));
				positionedTicket = new PositionedTicket(newPosition, tickets.get(newPosition));
			}
		}
		
		return positionedTicket;
	}
	
	public PositionedTicket postpone(User user, Duration duration)
	{
		long slots;
		
		if (estimatedAverageWaitingDuration.isZero())
		{
			slots = 0;
		}
		else
		{
			slots = duration.dividedBy(estimatedAverageWaitingDuration);
		}
		slots = Math.min(slots, Integer.MAX_VALUE);
		
		return postpone(user, (int) slots);
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

	public Integer getLastDrawnTicketPosition()
	{
		return lastDrawnTicketPosition;
	}
	public void setLastDrawnTicketPosition(Integer lastDrawnTicketPosition)
	{
		this.lastDrawnTicketPosition = lastDrawnTicketPosition;
	}
	public PositionedTicket getLastDrawnTicket()
	{
		if (lastDrawnTicketPosition > -1)
		{
			return new PositionedTicket(lastDrawnTicketPosition, tickets.get(lastDrawnTicketPosition));
		}
		return null;
	}

	public List<Ticket> getTickets()
	{
		return tickets;
	}
	public void setTickets(List<Ticket> tickets)
	{
		this.tickets = tickets;
	}
	
	public List<PositionedTicket> getPositionedTickets()
	{
		return positionedTickets(t -> true, 0, tickets.size());
	}
	
	/**
	 * A wrapper for {@link Ticket} which exposes its position
	 * in the {@link Session#getTickets()} list.
	 * 
	 * @author Giulio Quaresima (giulio.quaresima--at--gmail.com)
	 */
	public static class PositionedTicket
	{
		private final int position;
		private final Ticket ticket;
		public PositionedTicket(int position, Ticket ticket)
		{
			super();
			this.position = position;
			this.ticket = ticket;
		}
		public int getPosition()
		{
			return position;
		}
		public Ticket getTicket()
		{
			return ticket;
		}
		@Override
		public int hashCode()
		{
			return Objects.hash(position, ticket);
		}
		@Override
		public boolean equals(Object obj)
		{
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			PositionedTicket other = (PositionedTicket) obj;
			return position == other.position && Objects.equals(ticket, other.ticket);
		}
	}

}
