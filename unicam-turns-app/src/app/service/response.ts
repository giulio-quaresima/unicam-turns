export class Response<T> {
    public payload : T;
    public success : boolean;
    public error : {
        code : number;
        message : string;
    };
}
