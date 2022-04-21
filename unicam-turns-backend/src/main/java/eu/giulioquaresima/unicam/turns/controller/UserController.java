package eu.giulioquaresima.unicam.turns.controller;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import eu.giulioquaresima.unicam.turns.domain.entities.Ticket;
import eu.giulioquaresima.unicam.turns.domain.entities.TicketDispenser;
import eu.giulioquaresima.unicam.turns.domain.service.TicketServices;

/**
 * 
 * 
 * @author Giulio Quaresima (giulio.quaresima--at--gmail.com)
 */
@RestController
@RequestMapping ("/user")
public class UserController
{
	private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);
	
	@Autowired
	private TicketServices ticketServices;
	
	@GetMapping
	public ResponseEntity<String> whoami()
	{
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		return ResponseEntity.ok("Hey, " + authentication.getName());
	}
	
	@GetMapping ("/withdraw/dispenser/{ticketDispenser:\\d+}")
	public ResponseEntity<Ticket> withdraw(@PathVariable TicketDispenser ticketDispenser)
	{
//		System.out.println(userDetails);
//		if (userDetails != null)
//		{
//			System.out.println(userDetails.getUsername());
//			System.out.println(userDetails.getPassword());
//		}
		Optional<Ticket> optionalTicket = Optional.empty();
		if (ticketDispenser != null)
		{
			optionalTicket = Optional.ofNullable(ticketServices.withdraw(ticketDispenser));
		}
		else
		{
			LOGGER.info("No TicketDispenser found with the given id");
		}
		return ResponseEntity.of(optionalTicket);
	}
}
