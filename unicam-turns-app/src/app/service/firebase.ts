import { environment } from 'src/environments/environment';
import { initializeApp } from "firebase/app";
import { getMessaging, onMessage, getToken } from "firebase/messaging";
import { Injectable, OnInit } from '@angular/core';

const firebaseApp = initializeApp(environment.firebase);

const messaging = getMessaging(firebaseApp);

@Injectable({ providedIn: 'root' })
export class Firebase {

    sendTokenToBackend() { 
        getToken(messaging, {vapidKey : environment.firebase.vapidPublicKey})
            .then(currentToken => {
                if (currentToken) {
                    console.log("Ecco il token!!!", currentToken);
                    console.log("Ora inizializzo il listener dei messaggi");
                    onMessage(messaging, (payload) => {
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
