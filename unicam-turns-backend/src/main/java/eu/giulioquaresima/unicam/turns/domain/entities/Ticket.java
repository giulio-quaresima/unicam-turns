package eu.giulioquaresima.unicam.turns.domain.entities;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.NaturalId;

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
	@NaturalId
	private EmbeddableUUID uuid;
	
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
	
	/**
	 * An UUID field which represents an unique, unpredictable (random generated) value
	 * assigned to each ticket in addition to its number, so as to provide an anonymous 
	 * authentication mechanism (i.e without the necessity to sign up and login) 
	 * for each user who owns a ticket because only the user who withdrew the ticket 
	 * may knows its UUID.
	 * 
	 * The UUID is internally mapped as an {@link Embeddable} type because it apperas
	 * that Hibernate by default maps the java native UUID as a binary column in the DB.
	 * 
	 * @return
	 */
	public UUID getUuid()
	{
		if (uuid != null)
		{
			return uuid.toUUID();
		}
		return null;
	}
	public void setUuid(UUID uuid)
	{
		if (uuid != null)
		{
			this.uuid = new EmbeddableUUID(uuid);
		}
		else
		{
			this.uuid = null;
		}
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
