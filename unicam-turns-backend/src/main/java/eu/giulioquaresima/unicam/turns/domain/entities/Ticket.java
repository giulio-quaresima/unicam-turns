package eu.giulioquaresima.unicam.turns.domain.entities;

import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.function.UnaryOperator;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;

/**
 * The double-linked-list of tickets of a {@link Session}.
 * 
 * @author Giulio Quaresima (giulio.quaresima--at--gmail.com)
 */
@Entity
public class Ticket extends AbstractEntity<Ticket> implements Iterable<Ticket>
{
	@OneToOne (cascade = CascadeType.ALL)
	private Ticket previous;
	
	@OneToOne (cascade = CascadeType.ALL, mappedBy = "previous")
	private Ticket next;
	
	@ManyToOne (optional = false)
	@NotNull
	private Session session;
	
	@ManyToOne
	private User user;
	
	@ManyToOne
	private ServiceReception assignedReception;
	
	@Column (length = 8, nullable = false)
	private String number;
	
	private LocalDateTime withdrawTime;

	private LocalDateTime drawTime;

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
	
	/**
	 * Swap the position of {@code this} ticket
	 * and the {@code other} in the tickets' linked
	 * list.
	 * 
	 * @param other
	 */
	public void swap(Ticket other)
	{
		if (other != null && !equals(other))
		{
			Ticket thisPrevious = getPrevious();
			Ticket thisNext = getNext();
			Ticket otherPrevious = other.getPrevious();
			Ticket otherNext = other.getNext();
			
			// Consider the side effect of setPrevious (see)
			
			setPrevious(otherPrevious);
			if (otherNext != null)
			{
				otherNext.setPrevious(this);
			}
			else
			{
				setNext(null);
			}
			
			other.setPrevious(thisPrevious);
			if (thisNext != null)
			{
				thisNext.setPrevious(other);
			}
			else
			{
				other.setNext(null);
			}
		}
	}

	public Ticket getPrevious()
	{
		return previous;
	}
	public void setPrevious(Ticket previous)
	{
		if (equals(previous))
		{
			throw new IllegalArgumentException("Circular reference!");
		}
		this.previous = previous;
	}

	public Ticket getNext()
	{
		return next;
	}
	public void setNext(Ticket next)
	{
		if (equals(next))
		{
			throw new IllegalArgumentException("Circular reference!");
		}
		this.next = next;
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
	
	public ServiceReception getServiceReception()
	{
		return serviceReception;
	}
	public void setServiceReception(ServiceReception serviceReception)
	{
		this.serviceReception = serviceReception;
	}

	@Override
	public Iterator<Ticket> iterator()
	{
		return forwardIterator();
	}
	public Iterator<Ticket> forwardIterator()
	{
		return new DirectionableIterator(this, Ticket::getNext);
	}
	public Iterator<Ticket> backIterator()
	{
		return new DirectionableIterator(this, Ticket::getPrevious);
	}
	
	public static class DirectionableIterator implements Iterator<Ticket>
	{
		private Ticket current;
		private final UnaryOperator<Ticket> nextPointer;
		
		public DirectionableIterator(Ticket current, UnaryOperator<Ticket> nextPointer)
		{
			super();
			this.current = current;
			this.nextPointer = nextPointer;
		}

		@Override
		public boolean hasNext()
		{
			return current != null;
		}

		@Override
		public Ticket next()
		{
			Ticket returnee = current;
			current = nextPointer.apply(returnee);
			return returnee;
		}
	}
	
}
