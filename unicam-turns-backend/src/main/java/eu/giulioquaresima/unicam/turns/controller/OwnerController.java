package eu.giulioquaresima.unicam.turns.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.Assert;
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
import eu.giulioquaresima.unicam.turns.rest.Response;

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
//	private static final Logger LOGGER = LoggerFactory.getLogger(OwnerController.class);
	
	@Autowired
	private TicketDispenserServices ticketDispenserServices;
	
	@GetMapping ("/ticketDispensers")
	public ResponseEntity<Response<List<TicketDispenser>>> list()
	{
		return Response.ok(ticketDispenserServices.listOwnDispensers());
	}
	
	@PostMapping ("/ticketDispensers")
	@PreAuthorize ("hasPermission('C')")
	public ResponseEntity<Response<TicketDispenser>> createDispenser(
			@Valid @RequestBody TicketDispenser ticketDispenser,
			BindingResult bindingResult)
	{
		if (bindingResult.hasErrors())
		{
			return Response.ko(ticketDispenser, HttpStatus.BAD_REQUEST, "Missing required value");
		}
		return Response.ok(ticketDispenserServices.create(ticketDispenser));
	}
	
	@PutMapping ("/ticketDispensers/{ticketDispenser:\\d+}/start")
	@PreAuthorize ("hasPermission('U')")
	public ResponseEntity<Response<Session>> startSession(@PathVariable TicketDispenser ticketDispenser)
	{
		Assert.notNull(ticketDispenser, "ticketDispenser");
		return Response.ok(ticketDispenserServices.start(ticketDispenser));
	}
	
	@PutMapping ("/ticketDispensers/{ticketDispenser:\\d+}/stop")
	@PreAuthorize ("hasPermission('U')")
	public ResponseEntity<Response<Session>> stopSession(@PathVariable TicketDispenser ticketDispenser)
	{
		Assert.notNull(ticketDispenser, "ticketDispenser");
		Session session = ticketDispenserServices.stop(ticketDispenser);
		if (session == null)
		{
			return Response.ko(session, HttpStatus.NOT_FOUND, "No active session");
		}
		return Response.ok(session);
	}
	
}
