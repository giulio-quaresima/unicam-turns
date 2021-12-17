package eu.giulioquaresima.unicam.turns.domain.entities;

import java.util.Comparator;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.persistence.Entity;

/**
 * 
 * 
 * @author Giulio Quaresima (giulio.quaresima--at--gmail.com)
 */
@Entity
public class User extends AbstractEntity<User>
{
	public static final Comparator<User> ALPHABETIC_COMPARATOR = Comparator.nullsLast(Comparator
			.comparing(User::getFamilyName, Comparator.nullsLast(String::compareToIgnoreCase))
			.thenComparing(User::getGivenName, Comparator.nullsLast(String::compareToIgnoreCase)));
	
	private String title;
	private String prefix;
	private String givenName;
	private String familyName;
	private String suffix;
	
	public User()
	{
		super();
	}
	public User(String title, String prefix, String givenName, String familyName, String suffix)
	{
		this();
		this.title = title;
		this.prefix = prefix;
		this.givenName = givenName;
		this.familyName = familyName;
		this.suffix = suffix;
	}
	public User(String givenName, String familyName)
	{
		this();
		this.givenName = givenName;
		this.familyName = familyName;
	}
	
	public String getTitle()
	{
		return title;
	}
	public void setTitle(String title)
	{
		this.title = title;
	}
	public String getPrefix()
	{
		return prefix;
	}
	public void setPrefix(String prefix)
	{
		this.prefix = prefix;
	}
	public String getGivenName()
	{
		return givenName;
	}
	public void setGivenName(String givenName)
	{
		this.givenName = givenName;
	}
	public String getFamilyName()
	{
		return familyName;
	}
	public void setFamilyName(String familyName)
	{
		this.familyName = familyName;
	}
	public String getSuffix()
	{
		return suffix;
	}
	public void setSuffix(String suffix)
	{
		this.suffix = suffix;
	}
	
	@Override
	public String toString()
	{
		return Stream.of(title, prefix, givenName, familyName, suffix)
				.filter(Objects::nonNull)
				.collect(Collectors.joining(" "))
				;
	}
	
	@Override
	protected int compareNotEqual(User otherEntity)
	{
		int compare = ALPHABETIC_COMPARATOR.compare(this, otherEntity);
		if (compare == 0)
		{
			compare = super.compareNotEqual(otherEntity);
		}
		return compare;
	}
	
}
