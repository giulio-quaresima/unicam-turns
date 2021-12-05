package eu.giulioquaresima.unicam.turns.domain.entities;

import javax.persistence.Entity;

/**
 * 
 * 
 * @author Giulio Quaresima (giulio.quaresima--at--gmail.com)
 */
@Entity
public class TicketSourceConfiguration extends AbstractEntity<TicketSourceConfiguration>
{
	private char[] digits = "0123456789ABCDEFGH".toCharArray();
	private boolean postponeEnabled = true;
	int minimumTicketLabelLength = 2;
}
