package eu.giulioquaresima.unicam.turns.rest;

import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import com.fasterxml.jackson.annotation.JsonView;

import eu.giulioquaresima.unicam.turns.domain.entities.Session;
import eu.giulioquaresima.unicam.turns.domain.entities.Ticket;
import eu.giulioquaresima.unicam.turns.domain.entities.TicketDispenser;
import eu.giulioquaresima.unicam.turns.domain.entities.User;
import eu.giulioquaresima.unicam.turns.rest.json.JsonViews;

/**
 * The state of an {@link User} relative to a {@link TicketDispenser}.
 * 
 * @author Giulio Quaresima (giulio.quaresima--at--gmail.com)
 */
@JsonView (JsonViews.Default.class)
public class UserTicketDispenserState
{
	private final TicketDispenser ticketDispenser;
	private Ticket currentUserAliveTicket;
	
	public UserTicketDispenserState(TicketDispenser ticketDispenser)
	{
		super();
		Assert.notNull(ticketDispenser, "ticketDispenser");
		this.ticketDispenser = ticketDispenser;
	}

	public UserTicketDispenserState(Ticket currentUserAliveTicket)
	{
		this(currentUserAliveTicket.getSession().getTicketDispenser());
		this.currentUserAliveTicket = currentUserAliveTicket;
	}

	public TicketDispenser getTicketDispenser()
	{
		return ticketDispenser;
	}
	
	@Nullable
	public Session getCurrentSession()
	{
		return ticketDispenser.getCurrentSession();
	}
	
	@Nullable
	public Ticket getCurrentUserAliveTicket()
	{
		return currentUserAliveTicket;
	}
	public void setCurrentUserAliveTicket(Ticket currentUserAliveTicket)
	{
		this.currentUserAliveTicket = currentUserAliveTicket;
	}
	
	@Nullable
	public Ticket getLastDrewTicket()
	{
		Session session = getCurrentSession();
		if (session != null)
		{
			return session.getLastDrewTicket();
		}
		return null;
	}
	
	@Nullable
	public Ticket getLastWithdrewTicket()
	{
		Session session = getCurrentSession();
		if (session != null)
		{
			return session.getLastWithdrewTicket();
		}
		return null;
	}
	
	@Nullable
	public long getNextWithdrawableTicketPublicNumber()
	{
		Ticket lastWithdrewTicket = getLastWithdrewTicket();
		if (lastWithdrewTicket != null)
		{
			return lastWithdrewTicket.getPublicNumber() + 1;
		}
		return 1;
	}
	
	public boolean isWithdrawable()
	{
		Session currentSession = getCurrentSession();
		return currentSession != null && currentSession.isOpen();
	}
	
}
