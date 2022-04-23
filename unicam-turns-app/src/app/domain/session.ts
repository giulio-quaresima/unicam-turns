import { TicketDispenser } from "./ticket-dispenser";

export class Session {
    startTime : Date;
    endTime : Date;
    open : boolean;
    tichetDispenser : TicketDispenser;
}
