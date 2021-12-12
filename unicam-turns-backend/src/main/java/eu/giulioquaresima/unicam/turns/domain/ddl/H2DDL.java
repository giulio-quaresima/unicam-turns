package eu.giulioquaresima.unicam.turns.domain.ddl;

import org.hibernate.dialect.H2Dialect;

public class H2DDL
{

	public static void main(String[] args) throws ClassNotFoundException
	{
		DDL.ddl(H2Dialect.class);
	}

}
