package eu.giulioquaresima.unicam.turns.rest.json;

public interface JsonViews
{
	public interface Default extends JsonViews {}
	public interface SessionToTicket extends Default {}
	public interface TicketToSession extends Default {}
}
