importScripts('https://www.gstatic.com/firebasejs/9.6.11/firebase-app-compat.js');
importScripts('https://www.gstatic.com/firebasejs/9.6.11/firebase-messaging-compat.js');

// TODO: Add SDKs for Firebase products that you want to use
// https://firebase.google.com/docs/web/setup#available-libraries

// Your web app's Firebase configuration
// For Firebase JS SDK v7.20.0 and later, measurementId is optional

firebase.initializeApp({
    apiKey: "AIzaSyAVZKYOx5VLbeWrGj0I6q2TxTE0sNTaXfQ",
    authDomain: "unicam-turns-app.firebaseapp.com",
    projectId: "unicam-turns-app",
    storageBucket: "unicam-turns-app.appspot.com",
    messagingSenderId: "95967144560",
    appId: "1:95967144560:web:659f83a346b7c285afa2b5",
    measurementId: "G-086RJ9HYC2"
  });

const messaging = firebase.messaging();

messaging.onBackgroundMessage((payload) => {
    console.log('[firebase-messaging-sw.js] Received background message ', payload);
    // Customize notification here
    const notificationTitle = 'Background Message Title';
    const notificationOptions = {
      body: 'Background Message body.',
      icon: '/firebase-logo.png'
    };
  
    self.registration.showNotification(notificationTitle,
      notificationOptions);
  });
  