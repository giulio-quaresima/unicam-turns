package eu.giulioquaresima.unicam.turns.domain.entities;

import java.time.LocalDateTime;

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
	private User user;
	
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
		return "Ticket [number=" + number + ", user=" + user + "]";
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

	public User getUser()
	{
		return user;
	}
	public void setUser(User user)
	{
		this.user = user;
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

	public ServiceReception getServiceReception()
	{
		return serviceReception;
	}
	public void setServiceReception(ServiceReception serviceReception)
	{
		this.serviceReception = serviceReception;
	}

	public boolean isDrawnable()
	{
		return ! isCancelled(); // TODO Any other rules?
	}

}
