package eu.giulioquaresima.unicam.turns.security;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import eu.giulioquaresima.unicam.turns.domain.entities.TicketDispenser;
import eu.giulioquaresima.unicam.turns.domain.entities.User;

@Service
public class TicketDispenserPermissionEvaluator implements EntityPermissionEvaluator<TicketDispenser>
{

	@Override
	public boolean supports(TicketDispenser ticketDispenser)
	{
		return ticketDispenser instanceof TicketDispenser; // Seems strange, but the actual type is erased runtime
	}

	@Override
	public boolean hasPermission(Authentication authentication, User user, TicketDispenser ticketDispenser, Object requiredPermission)
	{
		if (user != null && ticketDispenser != null)
		{
			if (user.owns(ticketDispenser))
			{
				return CRUD.CRUD.satisfies(requiredPermission);
			}
		}
		return false;
	}

}
