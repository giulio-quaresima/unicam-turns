package eu.giulioquaresima.unicam.turns.config;

import javax.sql.DataSource;

import org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy;
import org.hibernate.boot.model.naming.ImplicitNamingStrategy;
import org.hibernate.boot.model.naming.PhysicalNamingStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.orm.jpa.hibernate.SpringImplicitNamingStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import liquibase.integration.spring.SpringLiquibase;

@Configuration
public class PersistenceConfig
{	
	@Bean
	public SpringLiquibase springLiquibase(@Autowired DataSource dataSource)
	{
		SpringLiquibase springLiquibase = new SpringLiquibase();
		
		springLiquibase.setDataSource(dataSource);
		springLiquibase.setChangeLog("classpath:eu/giulioquaresima/unicam/turns/liquibase/dbchangelog.xml");
		springLiquibase.setDropFirst(true);
		
		return springLiquibase;
	}
	
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
