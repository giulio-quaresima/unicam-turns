package eu.giulioquaresima.unicam.turns.domain.ddl;

import java.time.Instant;
import java.util.EnumSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.MappedSuperclass;

import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Environment;
import org.hibernate.dialect.Dialect;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.hibernate.tool.schema.TargetType;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.orm.jpa.hibernate.SpringImplicitNamingStrategy;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;

public class DDL
{
	public static void ddl(Class<? extends Dialect> dialect) throws ClassNotFoundException
	{
		ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()	
//				.applySetting(Environment.DIALECT, PostgreSQL10Dialect.class)
				.applySetting(Environment.DIALECT, dialect)
				.applySetting(Environment.IMPLICIT_NAMING_STRATEGY, SpringImplicitNamingStrategy.class)
				.applySetting(Environment.PHYSICAL_NAMING_STRATEGY, CamelCaseToUnderscoresNamingStrategy.class)
				.build();
		
		ClassPathScanningCandidateComponentProvider classPathScanningCandidateComponentProvider = new ClassPathScanningCandidateComponentProvider(false);
		classPathScanningCandidateComponentProvider.addIncludeFilter(new AnnotationTypeFilter(Entity.class));
		classPathScanningCandidateComponentProvider.addIncludeFilter(new AnnotationTypeFilter(MappedSuperclass.class));
		
		MetadataSources metadataSources = new MetadataSources(serviceRegistry);
		String[] packageNames = {"eu.giulioquaresima.unicam.turns"};
		for (String packageName : packageNames)
		{
			metadataSources.addPackage(packageName);
			Set<BeanDefinition> beanDefinitions = classPathScanningCandidateComponentProvider.findCandidateComponents(packageName);
			for (BeanDefinition beanDefinition : beanDefinitions)
			{
//				System.out.println(beanDefinition.getBeanClassName());
				metadataSources.addAnnotatedClass(Class.forName(beanDefinition.getBeanClassName()));
			}
		}
		Metadata metadata = metadataSources.buildMetadata();

		SchemaExport schemaExport = new SchemaExport();
		schemaExport.setFormat(true);
		schemaExport.setDelimiter(";");
//		schemaExport.setOutputFile("create.sql");
//		schemaExport.createOnly(EnumSet.of(TargetType.SCRIPT), metadata);
		
		System.out.println("----------------------------------------------------------------");
		System.out.println("-- Generated by eu.giulioquaresima.unicam.turns.domain.ddl.DDL --");
		System.out.printf("-- at %s                          --\n", Instant.now());
		System.out.println("----------------------------------------------------------------");
		System.out.println();

		schemaExport.createOnly(EnumSet.of(TargetType.STDOUT), metadata);
	}

}
