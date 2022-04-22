package eu.giulioquaresima.unicam.turns.test.unit.security;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import eu.giulioquaresima.unicam.turns.security.CRUD;

public class TestCRUD
{
	@Test
	public void testSatisfy()
	{
		assertThat(CRUD.CRUD.satisfies("CRUD")).isTrue();
		assertThat(CRUD.CRUD.satisfies("RDCU")).isTrue();
		assertThat(CRUD.CRUD.satisfies("RDCC")).isTrue();
		assertThat(CRUD.CRUD.satisfies("RDCA")).isFalse();
		
		assertThat(CRUD.CRU.satisfies("CRUD")).isFalse();
		assertThat(CRUD.CRU.satisfies("CRU")).isTrue();
		assertThat(CRUD.CRU.satisfies("CRD")).isFalse();
		assertThat(CRUD.CRU.satisfies("RUD")).isFalse();
		assertThat(CRUD.CRU.satisfies("CR")).isTrue();
		assertThat(CRUD.CRU.satisfies("CU")).isTrue();
		assertThat(CRUD.CRU.satisfies("RU")).isTrue();
		assertThat(CRUD.CRU.satisfies("CD")).isFalse();
		assertThat(CRUD.CRU.satisfies("RD")).isFalse();
		assertThat(CRUD.CRU.satisfies("UD")).isFalse();
		assertThat(CRUD.CRU.satisfies("C")).isTrue();
		assertThat(CRUD.CRU.satisfies("R")).isTrue();
		assertThat(CRUD.CRU.satisfies("U")).isTrue();
		assertThat(CRUD.CRU.satisfies("D")).isFalse();
		assertThat(CRUD.CRU.satisfies("Cantiano")).isFalse();		
		assertThat(CRUD.CRU.satisfies(Integer.MAX_VALUE)).isFalse();		
	}
	
	public static void main(String[] args)
	{
		TestCRUD _self = new TestCRUD();
		_self.testSatisfy();
	}
}
