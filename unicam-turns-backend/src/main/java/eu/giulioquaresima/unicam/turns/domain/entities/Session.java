package eu.giulioquaresima.unicam.turns.domain.entities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OrderColumn;
import javax.validation.constraints.NotNull;

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
	
	@ManyToOne
	private Ticket lastWithdrawnTicket;

	@ManyToOne
	private Ticket lastDrawnTicket;

	public Ticket withraw(User user)
	{
		Ticket ticket = new Ticket();
		
		ticket.setSession(this);
		ticket.setUser(user);
		ticket.setWithdrawTime(LocalDateTime.now());
		
		ticket.setNumber(pollNewNumber());
		
		if (lastWithdrawnTicket != null)
		{
			ticket.setPrevious(lastWithdrawnTicket);
		}
		lastWithdrawnTicket = ticket;
		
		return lastWithdrawnTicket;
	}
	
	public Ticket draw(ServiceReception serviceReception)
	{
		if (lastDrawnTicket == null)
		{
			if (lastWithdrawnTicket != null)
			{
				Ticket first;
				Iterator<Ticket> iterator = lastWithdrawnTicket.backIterator();
				Assert.state(iterator.hasNext(), label);
				do
				{
					first = iterator.next();
				}
				while (iterator.hasNext());
				lastDrawnTicket = first;
			}
		}
		else
		{
			lastDrawnTicket = lastDrawnTicket.getNext();
		}
		
		if (lastDrawnTicket != null)
		{
			lastDrawnTicket.setServiceReception(serviceReception);
			lastDrawnTicket.setDrawTime(LocalDateTime.now());
		}
		
		return lastDrawnTicket;
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
				List<String> numbers = bijectiveBaseKNumeration
						.sequence(bijectiveBaseKNumeration.sequenceRangeInterval(number == null ? 2 : number.length()))
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
			long lastValue;
			
			if (lastWithdrawnTicket == null)
			{
				lastValue = 0;
			}
			else
			{
				if (ticketSourceConfiguration.isUseBijectiveNumeration())
				{
					Objects.requireNonNull(bijectiveBaseKNumeration, "Illegal state in TicketSourceConfiguration");
					lastValue = bijectiveBaseKNumeration.parse(lastWithdrawnTicket.getNumber());
				}
				else
				{
					lastValue = Integer.parseInt(lastWithdrawnTicket.getNumber());
				}
			}
			
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

}
