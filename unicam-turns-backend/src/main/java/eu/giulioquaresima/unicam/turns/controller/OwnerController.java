package eu.giulioquaresima.unicam.turns.controller;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import eu.giulioquaresima.unicam.turns.domain.entities.Session;
import eu.giulioquaresima.unicam.turns.domain.entities.TicketDispenser;
import eu.giulioquaresima.unicam.turns.repository.SessionRepository;
import eu.giulioquaresima.unicam.turns.service.infrastructure.TimeServices;

@RestController
@RequestMapping ("/owner/dispenser/{ticketDispenser:\\d+}")
public class OwnerController
{
	private static final Logger LOGGER = LoggerFactory.getLogger(OwnerController.class);
	
	@Autowired
	private TimeServices timeServices;
	
	@Autowired
	private SessionRepository sessionRepository;
	
	@PostMapping ("/sessions/start")
	public ResponseEntity<Session> startSession(@PathVariable TicketDispenser ticketDispenser)
	{
		Optional<Session> optionalSession = Optional.empty();
		if (ticketDispenser != null)
		{
			Session session = ticketDispenser.getCurrentSession(timeServices.getSystemClock());
			if (session == null)
			{
				session = ticketDispenser.createSession();
				session.startNow(timeServices.getSystemClock());
				optionalSession = Optional.of(sessionRepository.save(session));
			}
		}
		else
		{
			LOGGER.info("No TicketDispenser found with the given id");
		}
		return ResponseEntity.of(optionalSession);
	}
	
}
