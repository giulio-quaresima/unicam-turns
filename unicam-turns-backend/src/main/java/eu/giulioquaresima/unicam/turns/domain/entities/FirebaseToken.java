package eu.giulioquaresima.unicam.turns.domain.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.NaturalId;

@Entity
public class FirebaseToken extends AbstractEntity<FirebaseToken>
{
	@Column (nullable = false)
	@NaturalId
	@NotNull
	private String token;
	
	@Column (nullable = false, length = 2048)
	@NaturalId
	@NotNull
	private String origin;
	
	@ManyToOne (optional = false)
	@JoinColumn (nullable = false)
	@NotNull
	private User user;

	public String getToken()
	{
		return token;
	}
	public void setToken(String token)
	{
		this.token = token;
	}

	public String getOrigin()
	{
		return origin;
	}
	public void setOrigin(String origin)
	{
		this.origin = origin;
	}
	
	public User getUser()
	{
		return user;
	}
	public void setUser(User user)
	{
		this.user = user;
	}

	@Override
	protected FirebaseToken castOrNull(Object obj)
	{
		if (obj instanceof FirebaseToken)
		{
			return (FirebaseToken) obj;
		}
		return null;
	}

}
