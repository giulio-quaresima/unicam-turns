import { Ticket } from "./ticket";
import { TicketDispenser } from "./ticket-dispenser";

export class Session {
    startTime : Date;
    endTime : Date;
    open : boolean;
    ticketDispenser : TicketDispenser;
    lastWithdrewTicket : Ticket;
    lastDrewTicket : Ticket;
}
