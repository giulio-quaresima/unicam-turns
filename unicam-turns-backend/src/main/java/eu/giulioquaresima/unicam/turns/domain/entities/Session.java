package eu.giulioquaresima.unicam.turns.domain.entities;

import java.util.List;

public class Session
{
	private TicketDispenser ticketDispenser;
	private List<Ticket> tickets;
	private int currentTicket = -1;
}
