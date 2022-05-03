import { Entity } from "./entity";
import { Session } from "./session";

export class Ticket extends Entity {

    public withdrawTime : Date;
    public publicNumber : number;
    public uniqueIdentifier : string;
    public session : Session;

    public alive : boolean;
    public waiting : boolean;
    public current : boolean;
    public expired : boolean;

}
