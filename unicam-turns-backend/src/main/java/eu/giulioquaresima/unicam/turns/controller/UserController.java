package eu.giulioquaresima.unicam.turns.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonView;

import eu.giulioquaresima.unicam.turns.domain.service.TicketDispenserServices;
import eu.giulioquaresima.unicam.turns.rest.json.JsonViews;

/**
 * 
 * 
 * @author Giulio Quaresima (giulio.quaresima--at--gmail.com)
 */
@RestController
@RequestMapping ("/user")
public class UserController
{
//	private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);
	
	@Autowired
	@JsonView (JsonViews.TicketToSession.class)
	private TicketDispenserServices ticketDispenserServices;
	
	
}
