package eu.giulioquaresima.unicam.turns.domain.entities;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.NaturalId;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class FirebaseToken extends AbstractEntity<FirebaseToken>
{
	@Column (nullable = false)
	@NaturalId
	@NotNull
	private String token;
	
	@Column (nullable = false, length = 2048)
	@NotNull
	private String origin;
	
	@ManyToOne (optional = false)
	@JoinColumn (nullable = false)
	@NotNull
	private User user;
	
	@Column (nullable = false)
	@NotNull
	private LocalDateTime creationTime = LocalDateTime.now();

	@Column (nullable = false)
	@NotNull
	private LocalDateTime toggleTime;

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
	
	@JsonIgnore // Per ora non mi serve
	public User getUser()
	{
		return user;
	}
	public void setUser(User user)
	{
		this.user = user;
	}

	/**
	 * Data di "nascita" del token.
	 * @return
	 */
	public LocalDateTime getCreationTime()
	{
		return creationTime;
	}
	public void setCreationTime(LocalDateTime creationTime)
	{
		this.creationTime = creationTime;
	}
	
	/**
	 * Ultima volta che il token e stato
	 * aggiornato, ovvero reinviato dal client:
	 * utile per calcolare quali ticket
	 * sono inattivi e vanno rimossi.
	 * 
	 * @return
	 * 
	 * @see https://github.com/giulio-quaresima/unicam-turns/issues/12
	 */
	public LocalDateTime getToggleTime()
	{
		return toggleTime;
	}
	public void setToggleTime(LocalDateTime toggleTime)
	{
		this.toggleTime = toggleTime;
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
