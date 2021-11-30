package eu.giulioquaresima.unicam.turns.model.entities;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public class AbstractEntity<E extends AbstractEntity<E>>
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	public Long getId()
	{
		return id;
	}

	public void setId(Long id)
	{
		this.id = id;
	}

	public boolean entityEquals(E entity)
	{
		if (this == entity)
		{
			return true;
		}
		
		if (entity != null)
		{
			return id != null && id.equals(entity.getId());
		}
		
		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(Object obj)
	{
		try
		{
			return entityEquals((E) obj);
		}
		catch (ClassCastException e)
		{
			return super.equals(obj);
		}
	}

	@Override
	public int hashCode()
	{
		if (id != null)
		{
			return id.hashCode();
		}
		return super.hashCode();
	}

}
