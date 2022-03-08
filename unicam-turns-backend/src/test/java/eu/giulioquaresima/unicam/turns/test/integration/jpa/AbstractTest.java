package eu.giulioquaresima.unicam.turns.test.integration.jpa;

import org.springframework.beans.factory.annotation.Autowired;

import eu.giulioquaresima.unicam.turns.repository.SessionRepository;
import eu.giulioquaresima.unicam.turns.repository.TicketDispenserRepository;

public class AbstractTest
{
	@Autowired
	protected SessionRepository sessionRepository;
	@Autowired 
	protected TicketDispenserRepository ticketDispenserRepository;
}
