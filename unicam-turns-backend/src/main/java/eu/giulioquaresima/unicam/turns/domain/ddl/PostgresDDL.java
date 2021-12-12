package eu.giulioquaresima.unicam.turns.domain.ddl;

import org.hibernate.dialect.PostgreSQL10Dialect;

public class PostgresDDL
{

	public static void main(String[] args) throws ClassNotFoundException
	{
		DDL.ddl(PostgreSQL10Dialect.class);
	}

}
