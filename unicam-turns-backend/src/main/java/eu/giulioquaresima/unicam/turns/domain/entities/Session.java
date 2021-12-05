package eu.giulioquaresima.unicam.turns.domain.entities;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class Session extends AbstractEntity<Session>
{
	@ManyToOne
	private Dispenser dispenser;
	
	private String name;
	
	private LocalDateTime startTime;
	
	private LocalDateTime endTime;
}
