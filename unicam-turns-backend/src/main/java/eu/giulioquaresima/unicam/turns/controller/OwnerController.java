package eu.giulioquaresima.unicam.turns.controller;

import java.time.ZoneId;
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

import com.fasterxml.jackson.annotation.JsonView;

import eu.giulioquaresima.unicam.turns.domain.entities.Session;
import eu.giulioquaresima.unicam.turns.domain.entities.Ticket;
import eu.giulioquaresima.unicam.turns.domain.entities.TicketDispenser;
import eu.giulioquaresima.unicam.turns.domain.service.TicketDispenserServices;
import eu.giulioquaresima.unicam.turns.rest.Response;
import eu.giulioquaresima.unicam.turns.rest.json.JsonViews;

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
	public ResponseEntity<Response<TicketDispenser>> createDispenser(
			ZoneId zoneId,
			@Valid @RequestBody TicketDispenser ticketDispenser,
			BindingResult bindingResult)
	{
		if (bindingResult.hasErrors())
		{
			if (bindingResult.getFieldErrorCount() == 1 && bindingResult.hasFieldErrors("zoneId"))
			{
				ticketDispenser.setZoneId(zoneId);
			}
			else
			{
				return Response.ko(ticketDispenser, HttpStatus.BAD_REQUEST, "Missing required value");
			}
		}
		return Response.ok(ticketDispenserServices.create(ticketDispenser));
	}
	
	@GetMapping ("/ticketDispensers/{ticketDispenser:\\d+}")
	public ResponseEntity<Response<TicketDispenser>> dispenser(@PathVariable TicketDispenser ticketDispenser)
	{
		Assert.notNull(ticketDispenser, "ticketDispenser");
		return Response.ok(ticketDispenser);
	}
	
	@GetMapping ("/ticketDispensers/{ticketDispenser:\\d+}/sessions/last")
	@JsonView (JsonViews.SessionToTicket.class)
	public ResponseEntity<Response<Session>> currentSession(@PathVariable TicketDispenser ticketDispenser)
	{
		Assert.notNull(ticketDispenser, "ticketDispenser");
		return Response.ok(ticketDispenser.getLastSession());
	}
	
	@PutMapping ("/ticketDispensers/{ticketDispenser:\\d+}/sessions/start")
	@JsonView (JsonViews.SessionToTicket.class)
	public ResponseEntity<Response<Session>> startSession(@PathVariable TicketDispenser ticketDispenser, ZoneId zoneId)
	{
		Assert.notNull(ticketDispenser, "ticketDispenser");
		return Response.ok(ticketDispenserServices.start(ticketDispenser));
	}
	
	@PutMapping ("/ticketDispensers/{ticketDispenser:\\d+}/sessions/draw")
	@JsonView (JsonViews.SessionToTicket.class)
	public ResponseEntity<Response<Session>> drawTicket(@PathVariable TicketDispenser ticketDispenser, ZoneId zoneId)
	{
		Assert.notNull(ticketDispenser, "ticketDispenser");
		Ticket ticket = ticketDispenserServices.draw(ticketDispenser);
		if (ticket != null)
		{
			return Response.ok(ticket.getSession());
		}
		Session session = ticketDispenser.getCurrentSession();
		if (session == null)
		{
			return Response.ko(session, HttpStatus.NOT_FOUND, "No active session");
		}
		return Response.ok(session);
	}
	
	@PutMapping ("/ticketDispensers/{ticketDispenser:\\d+}/sessions/end")
	@JsonView (JsonViews.SessionToTicket.class)
	public ResponseEntity<Response<Session>> endSession(@PathVariable TicketDispenser ticketDispenser)
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
