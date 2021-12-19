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
	
	private int lastDrawnTicketIndex = -1;
	
	private Duration estimatedAverageWaitingDuration;
	
	@OneToMany (mappedBy = "session", cascade = CascadeType.ALL)
	@OrderColumn (name = "position")
	private List<Ticket> tickets = new ArrayList<>();
	
	/**
	 * Get the set of indexed tickets which pass the {@code filter} test
	 * and fall inside the [from,to) interval.
	 * 
	 * @param filter The filter or criterion.
	 * 
	 * @param from The start index (inclusive).
	 * 
	 * @param to The end index (exclusive).
	 * 
	 * @return Never <code>null</code>.
	 */
	public List<IndexedTicket> indexedTickets(Predicate<Ticket> filter, int from, int to)
	{
		List<IndexedTicket> indexedTickets = new ArrayList<>();
		from = Math.max(from, 0);
		to = Math.min(to, tickets.size());
		for (int index = from; index < to; index++)
		{
			Ticket ticket = tickets.get(index);
			if (filter.test(ticket))
			{
				indexedTickets.add(new IndexedTicket(index, ticket));
			}
		}
		return indexedTickets;
	}
	
	/**
	 * Get the set of indices of the tickets which are owned
	 * by the passed user.
	 * 
	 * @param user The sought owner.
	 * 
	 * @return The indices of the owned tickets, empty if no ticket
	 * is owned.
	 * 
	 * @see Ticket#isOwner(User)
	 */
	public List<IndexedTicket> ownedTickets(User user)
	{
		if (user != null)
		{
			return indexedTickets(ticket -> ticket.isOwner(user), 0, tickets.size());
		}
		return Collections.emptyList();
	}
	
	/**
	 * Get the set of indices of the tickets which are owned
	 * by the passed user AND are waiting.
	 * 
	 * @param user The sought owner.
	 * 
	 * @return The only possible waiting ticket owned by {@code user}.
	 * 
	 * @see Ticket#isWaiting()
	 */
	public IndexedTicket waitingTicket(User user)
	{
		if (user != null)
		{
			Predicate<Ticket> filter = ticket -> ticket.isOwner(user);
			filter = filter.and(ticket -> ticket.isWaiting());
			List<IndexedTicket> indexedTickets = indexedTickets(
					filter, 
					lastDrawnTicketIndex + 1, // The drawn tickets are not alive 
					tickets.size());
			Assert.state(indexedTickets.size() < 2, "At most one ticket per user can be in the \"waiting\" state: there must be an error in state management of tickets");
			if (!indexedTickets.isEmpty())
			{
				return indexedTickets.get(0);
			}
		}
		return null;		
	}

	/**
	 * Withdraw a ticket (the same act
	 * as pulling the ticket from a dispenser).
	 * 
	 * @param user The user who withdrew the ticket.
	 * 
	 * @return The new ticket which has {@code user} set
	 * has the owner, a number, a withdraw time, and so is
	 * in its "waiting" state.
	 */
	public IndexedTicket withraw(User user)
	{
		IndexedTicket indexedTicket = waitingTicket(user);
		if (indexedTicket != null)
		{
			if (LOGGER.isDebugEnabled())
			{
				LOGGER.debug("The user {} is triying to withraw a ticket while is already waiting for with another ticket: I return the currently waiting ticket.");
			}
			return indexedTicket;
		}
		
		Ticket ticket = new Ticket();
		
		ticket.setSession(this);
		ticket.setOwner(user);
		ticket.setWithdrawTime(LocalDateTime.now());		
		ticket.setNumber(pollNewNumber());
		
		indexedTicket = new IndexedTicket(tickets.size(), ticket);
		
		tickets.add(ticket);
		
		return indexedTicket;
	}
	
	/**
	 * The act of drawing the next waiting ticket assigning
	 * it to a service reception
	 * 
	 * @param serviceReception The service reception to assign
	 * the ticket to.
	 * 
	 * @return A no longer waiting ticket with a service reception
	 * and a draw
	 */
	public IndexedTicket draw(ServiceReception serviceReception)
	{
		for (int position = (lastDrawnTicketIndex + 1); position < tickets.size(); position++)
		{
			Ticket ticket = tickets.get(position);
			if (ticket.isWaiting())
			{
				lastDrawnTicketIndex = position;
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
							.plus(estimatedAverageWaitingDuration.multipliedBy(lastDrawnTicketIndex-1))
							.dividedBy(lastDrawnTicketIndex);
				}
				
				return new IndexedTicket(lastDrawnTicketIndex, ticket);
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

	public Integer getLastDrawnTicketIndex()
	{
		return lastDrawnTicketIndex;
	}
	public void setLastDrawnTicketIndex(Integer lastDrawnTicketIndex)
	{
		this.lastDrawnTicketIndex = lastDrawnTicketIndex;
	}
	public IndexedTicket getLastDrawnTicket()
	{
		if (lastDrawnTicketIndex > -1)
		{
			return new IndexedTicket(lastDrawnTicketIndex, tickets.get(lastDrawnTicketIndex));
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
	
	public List<IndexedTicket> getIndexedTickets()
	{
		return indexedTickets(t -> true, 0, tickets.size());
	}
	
	/**
	 * A wrapper for {@link Ticket} which exposes its index
	 * in the {@link Session#getTickets()} list.
	 * 
	 * @author Giulio Quaresima (giulio.quaresima--at--gmail.com)
	 */
	public static class IndexedTicket
	{
		private final int index;
		private final Ticket ticket;
		public IndexedTicket(int index, Ticket ticket)
		{
			super();
			this.index = index;
			this.ticket = ticket;
		}
		public int getIndex()
		{
			return index;
		}
		public Ticket getTicket()
		{
			return ticket;
		}
	}

}
