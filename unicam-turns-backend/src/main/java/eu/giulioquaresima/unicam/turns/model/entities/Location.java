package eu.giulioquaresima.unicam.turns.model.entities;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class Location extends AbstractEntity<Location>
{
	@ManyToOne
	private Tenant tenant;
}
