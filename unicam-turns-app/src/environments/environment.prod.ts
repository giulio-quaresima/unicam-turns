// This file can be replaced during build by using the `fileReplacements` array.
// `ng build --prod` replaces `environment.ts` with `environment.prod.ts`.
// The list of file replacements can be found in `angular.json`.

export const environment = {
  production: false,
  apiInfo: {
    protocol: "https",
    host: "unicam-turns-backend.giulioquaresima.eu",
    port: 443,
    url: "https://unicam-turns-backend.giulioquaresima.eu"
  },
  oauthConfig: {
    issuer: "unicam-turns-authorization-server.giulioquaresima.eu"
  },
  firebase : {
    apiKey: "AIzaSyAVZKYOx5VLbeWrGj0I6q2TxTE0sNTaXfQ",
    authDomain: "unicam-turns-app.firebaseapp.com",
    projectId: "unicam-turns-app",
    storageBucket: "unicam-turns-app.appspot.com",
    messagingSenderId: "95967144560",
    appId: "1:95967144560:web:659f83a346b7c285afa2b5",
    measurementId: "G-086RJ9HYC2",
    /**
     * Voluntary Application Server Identification for Web Push
     * VAPID
     * https://datatracker.ietf.org/doc/html/draft-thomson-webpush-vapid
     */
    vapidPublicKey: "BEJ2Pbkm1r6U3WJnzClQvkRbBZ3vjAgjc7EoCQ1isZjBubU3B55IqFEr0Hq0FhIFf5invYkmRhHfqvME_T560k8"
  }
};

/*
 * For easier debugging in development mode, you can import the following file
 * to ignore zone related error stack frames such as `zone.run`, `zoneDelegate.invokeTask`.
 *
 * This import should be commented out in production mode because it will have a negative impact
 * on performance if an error is thrown.
 */
// import 'zone.js/dist/zone-error';  // Included with Angular CLI.
