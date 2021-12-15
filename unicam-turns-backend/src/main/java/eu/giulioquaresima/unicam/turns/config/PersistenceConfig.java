package eu.giulioquaresima.unicam.turns.config;

import org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy;
import org.hibernate.boot.model.naming.ImplicitNamingStrategy;
import org.hibernate.boot.model.naming.PhysicalNamingStrategy;
import org.springframework.boot.orm.jpa.hibernate.SpringImplicitNamingStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PersistenceConfig
{	
	
	@Bean
	public ImplicitNamingStrategy implicitNamingStrategy() 
	{
	    return new SpringImplicitNamingStrategy();
	}
	
	@Bean
	public PhysicalNamingStrategy physicalNamingStrategy() 
	{
	    return new CamelCaseToUnderscoresNamingStrategy();
	}
	
}
