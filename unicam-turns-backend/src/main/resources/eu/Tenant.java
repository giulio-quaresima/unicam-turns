package eu.giulioquaresima.unicam.turns.domain.entities;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
public class Tenant extends AbstractEntity<Tenant>
{
	@Column
	private String name;

	@Override
	protected Tenant castOrNull(Object obj)
	{
		if (obj instanceof Tenant)
		{
			return (Tenant) obj;
		}
		return null;
	}

}
