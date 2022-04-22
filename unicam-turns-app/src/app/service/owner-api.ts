import { Injectable } from "@angular/core";
import { HttpOptions } from "@capacitor-community/http";
import { TicketDispenser } from "../domain/ticket-dispenser";
import { Response } from "./response";
import { Rest } from "./rest";

@Injectable({ providedIn: 'root' })
export class OwnerApi {

    constructor (private rest : Rest) {

    }

    async ticketDispensersList() : Promise<Response<TicketDispenser[]>> {
        return this.rest.get({url : "/owner/ticketDispensers"}).then(response => {
            let data : Response<TicketDispenser[]> = response.data;
            if (!data.success) {
                console.log(data.error.message);
            }
            return data;
        });
    }

    async createTicketDispenser(label : string) : Promise<Response<TicketDispenser>> {
        let ticketDispenser : TicketDispenser = {
            label : label
        }
        let httpOptions : HttpOptions = {
            url : "/owner/ticketDispensers",
            data : ticketDispenser
        };
        return this.rest.post(httpOptions).then(response => {
            let data : Response<TicketDispenser> = response.data;
            if (!data.success) {
                console.log(data.error.message);
            }
            return data;
        });
    }

}
