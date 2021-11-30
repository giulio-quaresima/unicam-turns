package eu.giulioquaresima.unicam.turns.model.entities;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class Ticket extends AbstractEntity<Ticket>
{
	@ManyToOne
	private Session session;
}
