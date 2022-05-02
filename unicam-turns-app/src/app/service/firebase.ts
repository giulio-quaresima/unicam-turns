import { environment } from 'src/environments/environment';
import { FirebaseApp, initializeApp } from "firebase/app";
import { getMessaging, onMessage, getToken, Messaging } from "firebase/messaging";
import { Injectable, OnInit } from '@angular/core';
import { UserApi } from './user-api';

@Injectable({ providedIn: 'root' })
export class Firebase {

    private _supported : boolean = false;
    private _firebaseApp : FirebaseApp = null;
    private _messaging : Messaging = null;

    constructor(userApi : UserApi) {
        try {
            this._firebaseApp = initializeApp(environment.firebase, environment.firebase.projectId);
            this._messaging = getMessaging(this._firebaseApp);
            this._supported = true;
        } catch (error) {
            console.log("Cannot initialize Firebase", error);
        }
    }

    /**
     * @returns true if this service is supported an can be used
     */
    get supported() {
        return this._supported;
    }

    sendTokenToBackend() { 
        getToken(this._messaging, {vapidKey : environment.firebase.vapidPublicKey})
            .then(currentToken => {
                if (currentToken) {
                    console.log("Ecco il token!!!", currentToken);
                    console.log("Ora inizializzo il listener dei messaggi");
                    onMessage(this._messaging, (payload) => {
                        console.log('Message received. ', payload);
                    }); 
                } else {
                    console.log("No registration token available. Request permission to generate one.");
                }
            })
            .catch((error) => {
                console.log("Errore nel tentativo di ricevere il token da inviare al backend", error);
            })
            ;
    }

}
