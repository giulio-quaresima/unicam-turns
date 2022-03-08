package eu.giulioquaresima.unicam.turns.domain.entities;

import javax.persistence.Entity;

/**
 * The ticket dispenser.
 * 
 * @author Giulio Quaresima (giulio.quaresima--at--gmail.com)
 */
@Entity
public class TicketDispenser extends AbstractEntity<TicketDispenser>
{

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
