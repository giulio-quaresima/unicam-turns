import { environment } from 'src/environments/environment';
import { FirebaseApp, initializeApp } from "firebase/app";
import { getMessaging, onMessage, getToken, Messaging, Observer, MessagePayload, NextFn } from "firebase/messaging";
import { Injectable, OnInit } from '@angular/core';
import { UserApi } from './user-api';
import { ErrorFn, CompleteFn } from '@firebase/util';

@Injectable({ providedIn: 'root' })
export class Firebase implements Observer<MessagePayload> {

    private _supported : boolean = false;
    private _initialized : boolean = false;
    private _firebaseApp : FirebaseApp = null;
    private _messaging : Messaging = null;

    constructor(private userApi : UserApi) {
        try {
            this._firebaseApp = initializeApp(environment.firebase, environment.firebase.projectId);
            this._messaging = getMessaging(this._firebaseApp);
            this._supported = true;
        } catch (error) {
            console.log("Cannot initialize Firebase", error);
        }
    }

    /**
     * @returns true if this service is supported an can be used.
     */
    get supported() {
        return this._supported;
    }

    /**
     * @returns true if this messaging service is initialized.
     */
    get initialized() {
        return this._initialized;
    }

    initialize() {
        getToken(this._messaging, {vapidKey : environment.firebase.vapidPublicKey})
            .then(currentToken => {
                if (currentToken) {
                    console.log("Ecco il token!!!", currentToken);
                    this.userApi.saveFirebaseToken(currentToken);
                    console.log("Ora inizializzo il listener dei messaggi");
                    onMessage(this._messaging, this); 
                    this._initialized = true;
                } else {
                    console.log("No registration token available. Request permission to generate one.");
                }
            })
            .catch((error) => {
                console.log("Errore nel tentativo di ricevere il token da inviare al backend", error);
            })
        ;
    }


    // Observer signatures ////////////////

    next : NextFn<MessagePayload> = function(payload : MessagePayload) : void {
        console.log("Ho appena ricevuto questo messaggio: ", payload);
    };

    error : ErrorFn = function(error : Error) : void {
        console.log("Ho appena ricevuto questo errore: ", error);
    };

    complete : CompleteFn = function() : void {

    };
}
