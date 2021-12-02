package eu.giulioquaresima.unicam.turns.model.entities;

import javax.persistence.Entity;

@Entity
public class Tenant extends AbstractEntity<Tenant>
{
	@SuppressWarnings("unused")
	private String name;
}
