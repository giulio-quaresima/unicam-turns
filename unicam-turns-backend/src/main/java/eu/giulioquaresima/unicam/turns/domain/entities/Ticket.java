package eu.giulioquaresima.unicam.turns.domain.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * 
 * 
 * This entity compares by {@link Session} and then by {@link #getSeq()}.
 * 
 * @author Giulio Quaresima (giulio.quaresima--at--gmail.com)
 */
@Entity
public class Ticket extends AbstractEntity<Ticket>
{
	@ManyToOne (optional = false)
	private Session session;
	
	@ManyToOne
	private User user;
	
	@ManyToOne
	private ServiceReception serviceReception;
	
	private int seq;
	
	@Column (length = 8)
	private String label;
	

	@Override
	protected int compareNotEqual(Ticket otherEntity)
	{
		if (otherEntity != null)
		{
			int compare = 0;
			
			Session thisSession = getSession();
			Session otherSession = otherEntity.getSession();
			if (thisSession != null)
			{
				compare = thisSession.compareTo(otherSession);
			}
			else if (otherSession != null)
			{
				compare = - (otherSession.compareTo(thisSession));
			}
			
			if (compare == 0)
			{
				compare = getSeq() - otherEntity.getSeq();
			}
			
			if (compare != 0)
			{
				return compare;
			}
		}
		
		return super.compareNotEqual(otherEntity);
	}
	
	public Session getSession()
	{
		return session;
	}
	public void setSession(Session session)
	{
		this.session = session;
	}

	/**
	 * This is the current rank of a ticket
	 * 
	 * @return
	 */
	public int getSeq()
	{
		return seq;
	}
	public void setSeq(int seq)
	{
		this.seq = seq;
	}

	public String getLabel()
	{
		return label;
	}
	public void setLabel(String label)
	{
		this.label = label;
	}
	
}
