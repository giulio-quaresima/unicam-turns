package eu.giulioquaresima.unicam.turns.domain.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import eu.giulioquaresima.unicam.turns.utils.Comparators;

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
	public static final String INDEX_COLUMN = "ticket_index";
	public static final String SERVICE_RECEPTION_FK_COLUMN = "service_reception_id";
	
	@Column (name = INDEX_COLUMN, insertable = false, updatable = false)
	private Integer index;
	
	@ManyToOne
	@JoinColumn (name = SERVICE_RECEPTION_FK_COLUMN)
	private ServiceReception assignedReception;
	
	@ManyToOne (optional = false)
	@NotNull
	private Session session;
	
	@ManyToOne
	private User user;
	
	@Column (length = 8, nullable = false)
	private String number;

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
				compare = Comparators.integerNullsLastComparator().compare(getIndex(), otherEntity.getIndex());
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

	public Integer getIndex()
	{
		return index;
	}
	public void setIndex(Integer index)
	{
		this.index = index;
	}

	public String getNumber()
	{
		return number;
	}
	public void setNumber(String number)
	{
		this.number = number;
	}
	
}
