import { Entity } from "./entity";
import { Ticket } from "./ticket";
import { TicketDispenser } from "./ticket-dispenser";

export class Session extends Entity {
    startTime : Date;
    endTime : Date;
    open : boolean;
    ticketDispenser : TicketDispenser;
    lastWithdrewTicket : Ticket;
    lastDrewTicket : Ticket;
    remainingTicketsCount : number;
}
