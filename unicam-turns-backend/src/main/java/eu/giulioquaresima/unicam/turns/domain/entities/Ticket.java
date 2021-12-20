package eu.giulioquaresima.unicam.turns.domain.entities;

import java.time.LocalDateTime;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

/**
 * The double-linked-list of tickets of a {@link Session}.
 * 
 * @author Giulio Quaresima (giulio.quaresima--at--gmail.com)
 */
@Entity
public class Ticket extends AbstractEntity<Ticket>
{
	@NotNull
	@ManyToOne (optional = false)
	private Session session;
	
	@NotNull
	@Column (length = 8, nullable = false)
	private String number;
	
	@NotNull
	@ManyToOne (optional = false)
	private User owner;
	
	@ManyToOne
	private ServiceReception assignedReception;
	
	private LocalDateTime withdrawTime;

	private LocalDateTime drawTime;
	
	private LocalDateTime cancellationTime;

	@ManyToOne
	private ServiceReception serviceReception;
	
	public Ticket()
	{
		super();
	}

	public Ticket(@NotNull Session session, String number)
	{
		this();
		this.session = session;
		this.number = number;
	}
	
	@Override
	public String toString()
	{
		return "Ticket [number=" + number + ", owner=" + owner + "]";
	}

	public Session getSession()
	{
		return session;
	}
	public void setSession(Session session)
	{
		this.session = session;
	}

	public String getNumber()
	{
		return number;
	}
	public void setNumber(String number)
	{
		this.number = number;
	}

	public ServiceReception getAssignedReception()
	{
		return assignedReception;
	}
	public void setAssignedReception(ServiceReception assignedReception)
	{
		this.assignedReception = assignedReception;
	}

	/**
	 * The owner of the ticket, i.e. the user who withdrew the ticket:
	 * a ticket must have an owner, and can have only an owner at a time.  
	 * 
	 * @return
	 */
	public User getOwner()
	{
		return owner;
	}
	public void setOwner(User owner)
	{
		this.owner = owner;
	}
	/**
	 * @param user
	 * 
	 * @return <code>true</code> if the user is the actual owner, of if
	 * both are <code>null</code>.
	 */
	public boolean isOwner(User user)
	{
		return Objects.equals(user, getOwner());
	}

	public LocalDateTime getWithdrawTime()
	{
		return withdrawTime;
	}
	public void setWithdrawTime(LocalDateTime withdrawTime)
	{
		this.withdrawTime = withdrawTime;
	}

	public LocalDateTime getDrawTime()
	{
		return drawTime;
	}
	public void setDrawTime(LocalDateTime drawTime)
	{
		this.drawTime = drawTime;
	}
	public boolean isDrawn()
	{
		return getDrawTime() != null;
	}
	
	public LocalDateTime getCancellationTime()
	{
		return cancellationTime;
	}
	public void setCancellationTime(LocalDateTime cancellationTime)
	{
		this.cancellationTime = cancellationTime;
	}
	public boolean isCancelled()
	{
		return getCancellationTime() != null;
	}
	public void cancel()
	{
		setCancellationTime(LocalDateTime.now());
	}
	
	/**
	 * A ticket is waiting if it has not been cancelled
	 * and it has not been drawn yet.
	 * 
	 * @return
	 */
	public boolean isWaiting()
	{
		return !isCancelled() && !isDrawn();
	}

	public ServiceReception getServiceReception()
	{
		return serviceReception;
	}
	public void setServiceReception(ServiceReception serviceReception)
	{
		this.serviceReception = serviceReception;
	}

}
