import { Session } from "./session";
import { Ticket } from "./ticket";
import { TicketDispenser } from "./ticket-dispenser";

export class UserTicketDispenserState {
    ticketDispenser : TicketDispenser;
    currentSession : Session;
    currentUserAliveTicket : Ticket;
    exists : boolean;
    withdrawable : boolean;
}
