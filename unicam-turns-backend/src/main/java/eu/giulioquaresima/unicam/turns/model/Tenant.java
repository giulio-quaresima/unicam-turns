package eu.giulioquaresima.unicam.turns.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Tenant
{
	@Id
	@GeneratedValue (strategy = GenerationType.IDENTITY)
	private Integer id; 
}
