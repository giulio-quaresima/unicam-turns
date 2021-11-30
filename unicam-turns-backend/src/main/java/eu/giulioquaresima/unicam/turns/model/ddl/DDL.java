package eu.giulioquaresima.unicam.turns.model.ddl;

import java.util.EnumSet;

import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Environment;
import org.hibernate.dialect.PostgreSQL10Dialect;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.hibernate.tool.schema.TargetType;
import org.springframework.boot.orm.jpa.hibernate.SpringImplicitNamingStrategy;

import eu.giulioquaresima.unicam.turns.model.entities.Location;
import eu.giulioquaresima.unicam.turns.model.entities.Session;
import eu.giulioquaresima.unicam.turns.model.entities.Tenant;
import eu.giulioquaresima.unicam.turns.model.entities.Ticket;

public class DDL
{
	public static void ddl()
	{
		ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()	
				.applySetting(Environment.DIALECT, PostgreSQL10Dialect.class)
//				.applySetting(Environment.DIALECT, H2Dialect.class)
				.applySetting(Environment.IMPLICIT_NAMING_STRATEGY, SpringImplicitNamingStrategy.class)
				.applySetting(Environment.PHYSICAL_NAMING_STRATEGY, CamelCaseToUnderscoresNamingStrategy.class)
				.build();
		
		MetadataSources metadataSources = new MetadataSources(serviceRegistry);
		metadataSources.addAnnotatedClass(Location.class);
		metadataSources.addAnnotatedClass(Session.class);
		metadataSources.addAnnotatedClass(Tenant.class);
		metadataSources.addAnnotatedClass(Ticket.class);
		Metadata metadata = metadataSources.buildMetadata();

		SchemaExport schemaExport = new SchemaExport();
		schemaExport.setFormat(true);
//		schemaExport.setOutputFile("create.sql");
//		schemaExport.createOnly(EnumSet.of(TargetType.SCRIPT), metadata);
		schemaExport.createOnly(EnumSet.of(TargetType.STDOUT), metadata);
	}

	public static void main(String[] args)
	{
		ddl();
	}

}
