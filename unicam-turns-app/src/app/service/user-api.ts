import { Injectable } from "@angular/core";
import { HttpOptions } from "@capacitor-community/http";
import { FirebaseToken } from "../domain/firebase-token";
import { Ticket } from "../domain/ticket";
import { UserTicketDispenserState } from "../domain/user-ticket-dispenser-state";
import { Response } from "./response";
import { Rest } from "./rest";

@Injectable({ providedIn: 'root' })
export class UserApi {

    constructor (private rest : Rest) {

    }

    async currentUserTickets() : Promise<Response<Ticket[]>> {
        return this.rest.get({url : "/user/tickets"}).then(response => {
            let data : Response<Ticket[]> = response.data;
            if (!data.success) {
                console.log(data.error.message);
            }
            return data;
        });
    }

    async currentUserTicketDispenserState(dispenserId : number) : Promise<Response<UserTicketDispenserState>> {
        return this.rest.get({url : "/user/ticketDispensers/" + dispenserId}).then(response => {
            let data : Response<UserTicketDispenserState> = response.data;
            if (!data.success) {
                console.log(data.error.message);
            }
            return data;
        });
    }

    async withdraw(dispenserId : number) : Promise<Response<UserTicketDispenserState>> {
        return this.rest.put({url : "/user/ticketDispensers/" + dispenserId + "/withdraw"}).then(response => {
            let data : Response<UserTicketDispenserState> = response.data;
            if (!data.success) {
                console.log(data.error.message);
            }
            return data;
        });
    }

    async saveFirebaseToken(token : string) : Promise<Response<FirebaseToken>> {
        let firebaseToken : FirebaseToken = {
            token : token,
            origin : window.location.origin
        }
        let httpOptions : HttpOptions = {
            url : "/user/firebase/tokens",
            data : firebaseToken
        };
        return this.rest.put(httpOptions).then(response => {
            let data : Response<FirebaseToken> = response.data;
            if (!data.success) {
                console.log(data.error.message);
            }
            return data;
        });
    }

}
