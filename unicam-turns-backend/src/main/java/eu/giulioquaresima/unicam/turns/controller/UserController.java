package eu.giulioquaresima.unicam.turns.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonView;

import eu.giulioquaresima.unicam.turns.domain.entities.FirebaseToken;
import eu.giulioquaresima.unicam.turns.domain.entities.Ticket;
import eu.giulioquaresima.unicam.turns.domain.entities.TicketDispenser;
import eu.giulioquaresima.unicam.turns.domain.service.TicketDispenserServices;
import eu.giulioquaresima.unicam.turns.domain.service.TicketServices;
import eu.giulioquaresima.unicam.turns.domain.service.UserServices;
import eu.giulioquaresima.unicam.turns.rest.Response;
import eu.giulioquaresima.unicam.turns.rest.UserTicketDispenserState;
import eu.giulioquaresima.unicam.turns.rest.json.JsonViews;

/**
 * 
 * 
 * @author Giulio Quaresima (giulio.quaresima--at--gmail.com)
 */
@RestController
@RequestMapping ("/user")
@PreAuthorize("isAuthenticated()")
public class UserController
{
//	private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);
	
	@Autowired
	private TicketServices ticketServices;
	
	@Autowired
	private TicketDispenserServices ticketDispenserServices;
	
	@Autowired
	private UserServices userServices;
	
	@GetMapping ("/tickets")
	@JsonView (JsonViews.TicketToSession.class)
	public ResponseEntity<Response<List<Ticket>>> currentUserTickets()
	{
		return Response.ok(ticketServices.currentUserTickets());
	}
	
	@GetMapping ("/ticketDispensers/{ticketDispenser:\\d+}")
	@JsonView (JsonViews.TicketToSession.class)
	public ResponseEntity<Response<UserTicketDispenserState>> currentUserTicketDispenserState(
			@PathVariable (required = false) TicketDispenser ticketDispenser)
	{
		Ticket ticket = ticketDispenserServices.currentUserTicket(ticketDispenser);
		return theFormerOrTheLatter(ticket, ticketDispenser);
	}
	
	@PutMapping ("/ticketDispensers/{ticketDispenser:\\d+}/withdraw")
	@JsonView (JsonViews.TicketToSession.class)
	public ResponseEntity<Response<UserTicketDispenserState>> withdraw(@PathVariable TicketDispenser ticketDispenser)
	{
		Ticket ticket = ticketDispenserServices.withdraw(ticketDispenser);
		return theFormerOrTheLatter(ticket, ticketDispenser);
	}
	
	@PutMapping ("/firebase/tokens/{token}")
	public ResponseEntity<Response<FirebaseToken>> saveFirebaseToken(@PathVariable ("token") String token)
	{
		return Response.ok(userServices.assignTokenToCurrentUser(token));
	}
	
	private ResponseEntity<Response<UserTicketDispenserState>> theFormerOrTheLatter(Ticket ticket, TicketDispenser ticketDispenser)
	{
		if (ticketDispenser != null)
		{
			if (ticket != null)
			{
				return Response.ok(new UserTicketDispenserState(ticket)); 
			}
			return Response.ok(new UserTicketDispenserState(ticketDispenser));		
		}
		return Response.ko(null, HttpStatus.NOT_FOUND, "Non esiste un dispenser con questo id");
	}
	
}
