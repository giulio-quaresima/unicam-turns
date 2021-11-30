package eu.giulioquaresima.unicam.turns.model.entities;

import java.util.Collections;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class Session extends AbstractEntity<Session>
{
	@ManyToOne
	private Location location;
	public static void main(String[] args)
	{
		Collections.shuffle(null);
	}
}
