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
import eu.giulioquaresima.unicam.turns.domain.service.ClockService;
import eu.giulioquaresima.unicam.turns.repository.SessionRepository;

@RestController
@RequestMapping ("/owner/dispenser/{ticketDispenser:\\d+}")
public class OwnerController
{
	private static final Logger LOGGER = LoggerFactory.getLogger(OwnerController.class);
	
	@Autowired
	private ClockService clockService;
	
	@Autowired
	private SessionRepository sessionRepository;
	
	@PostMapping ("/sessions/start")
	public ResponseEntity<Session> startSession(@PathVariable TicketDispenser ticketDispenser)
	{
		Optional<Session> optionalSession = Optional.empty();
		if (ticketDispenser != null)
		{
			Session session = ticketDispenser.getCurrentSession(clockService.getClock());
			if (session == null)
			{
				session = ticketDispenser.createSession();
				session.startNow(clockService.getClock());
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
