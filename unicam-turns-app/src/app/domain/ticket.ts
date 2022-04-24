import { Session } from "./session";

export class Ticket {

    public withdrawTime : Date;
    public publicNumber : number;
    public uniqueIdentifier : String;
    public session : Session;

    public alive : boolean;
    public waiting : boolean;
    public current : boolean;
    public expired : boolean;

}
