package eu.giulioquaresima.unicam.turns.domain.entities;

import java.util.SortedSet;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.SortNatural;

/**
 * This entity identifies a virtual dispenser of digital tickets.
 * 
 * @author Giulio Quaresima (giulio.quaresima--at--gmail.com)
 */
@Entity
public class TicketDispenser extends AbstractEntity<TicketDispenser>
{
	@NotNull
	@Column (nullable = false)
	private String label;
	
	@ManyToOne (optional = false)
	private Owner owner;
	
	@OneToMany (mappedBy = "ticketDispenser")
	@SortNatural
	private SortedSet<Session> sessions;

	public Session createSession()
	{
		Session session = new Session();
		session.setTicketDispenser(this);
		return session;
	}
	
	public TicketDispenser()
	{
		super();
	}
	public TicketDispenser(String label, Owner owner)
	{
		this();
		this.label = label;
		this.owner = owner;
	}
	
	public String getLabel()
	{
		return label;
	}
	public void setLabel(String label)
	{
		this.label = label;
	}

	@Override
	protected TicketDispenser castOrNull(Object obj)
	{
		if (obj instanceof TicketDispenser)
		{
			return (TicketDispenser) obj;
		}
		return null;
	}

}
