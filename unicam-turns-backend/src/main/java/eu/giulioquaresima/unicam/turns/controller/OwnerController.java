package eu.giulioquaresima.unicam.turns.controller;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import eu.giulioquaresima.unicam.turns.domain.entities.Session;
import eu.giulioquaresima.unicam.turns.domain.entities.TicketDispenser;
import eu.giulioquaresima.unicam.turns.domain.service.TicketDispenserServices;
import eu.giulioquaresima.unicam.turns.repository.SessionRepository;
import eu.giulioquaresima.unicam.turns.rest.Response;
import eu.giulioquaresima.unicam.turns.service.infrastructure.TimeServices;

/**
 * 
 * 
 * @author Giulio Quaresima (giulio.quaresima--at--gmail.com)
 */
@RestController
@RequestMapping ("/owner")
@PreAuthorize("isAuthenticated()")
public class OwnerController
{
	private static final Logger LOGGER = LoggerFactory.getLogger(OwnerController.class);
	
	@Autowired
	private TimeServices timeServices;
	
	@Autowired
	private SessionRepository sessionRepository;
	
	@Autowired
	private TicketDispenserServices ticketDispenserServices;
	
	@GetMapping ("/ticketDispensers")
	public ResponseEntity<Response<List<TicketDispenser>>> list()
	{
		return ResponseEntity.ok(Response.of(ticketDispenserServices.listOwnDispensers()));
	}
	
	@PostMapping ("/ticketDispensers")
	@PreAuthorize ("hasPermission('C')")
	public ResponseEntity<Response<TicketDispenser>> createDispenser(
			@Valid @RequestBody TicketDispenser ticketDispenser,
			BindingResult bindingResult)
	{
		if (bindingResult.hasErrors())
		{
			return ResponseEntity.badRequest().body(Response.of(ticketDispenser, HttpStatus.BAD_REQUEST.value(), "Missing required value"));
		}
		return ResponseEntity.ok(Response.of(ticketDispenserServices.create(ticketDispenser)));
	}
	
	@PutMapping ("/ticketDispensers/{ticketDispenser:\\d+}/start")
	public ResponseEntity<Response<Session>> startSession(@PathVariable TicketDispenser ticketDispenser)
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
