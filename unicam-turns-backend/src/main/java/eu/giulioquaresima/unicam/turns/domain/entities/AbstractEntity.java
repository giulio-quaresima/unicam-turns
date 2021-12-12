package eu.giulioquaresima.unicam.turns.domain.entities;

import java.time.Instant;
import java.util.Comparator;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;

import org.springframework.lang.Nullable;

@MappedSuperclass
public abstract class AbstractEntity<E extends AbstractEntity<E>> implements Comparable<E>
{	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Version
	private Instant version;

	public Long getId()
	{
		return id;
	}

	public void setId(Long id)
	{
		this.id = id;
	}

	public boolean entityEquals(@Nullable E entity)
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
	public boolean equals(@Nullable Object obj)
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

	/** 
	 * A {@link Comparable} implementation which assure a <em>consistent with equals</em>
	 * behavior, delegating the non-equality comparison to {@link #compareNotEqual(AbstractEntity)}.
	 * This method is declared final to force the consistency of the entities' Comparable implementation: 
	 * the developer who needs for some reason to compare entities with some <em>not consistent with equals</em>
	 * logic may implement a {@link Comparator}.
	 * 
	 * @param otherEntity
	 * 
	 * @return
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public final int compareTo(@Nullable E otherEntity)
	{
		if (entityEquals(otherEntity))
		{
			return 0;
		}
		
		int compare = compareNotEqual(otherEntity);
		if (compare == 0)
		{
			throw new IllegalStateException("The comparator implementation is not consistent with equals!");
		}
		return compare;
	}

	/**
	 * A nulls-last comparator which simply compares the ids: this comparator is not
	 * <em>consistent-with-equals</em>, so its visibility is protected.
	 */
	protected static final Comparator<AbstractEntity<?>> NULLS_LAST_COMPARATOR = Comparator.nullsLast(
			Comparator.comparing(AbstractEntity::getId, Comparator.nullsLast(Comparator.naturalOrder()))
			)
			;

	/**
	 * A comparator which addresses the not-equals case only, so it should
	 * determine if {@code this} entity is less or greater than {@code otherEntity};
	 * this implementation delegates to {@link #NULLS_LAST_COMPARATOR}. 
	 * 
	 * See {@link Ticket#compareNotEqual(Ticket)} to get an example of the correct way
	 * to override this method.
	 * 
	 * @param otherEntity The entity to compare to, which is assured
	 * to be not equal to {@code this}.
	 * 
	 * @return A nonzero value.
	 */
	protected int compareNotEqual(@Nullable E otherEntity)
	{
		return NULLS_LAST_COMPARATOR.compare(this, otherEntity);
	}

}
