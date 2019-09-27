const functions = require('firebase-functions');

const admin = require('firebase-admin');
admin.initializeApp();

exports.sendNotification = functions.firestore
  .document('messages/{message}')
  .onCreate((snap, context) => {
    const docSnap = snap.data();
    const token = docSnap['token'];

    const payload = {
      notification: {
        title: "Hello, you got a notification!",
        body: "I DID IT! Cloud function worked!"
      }
    }
    return admin.messaging().sendToDevice(token, payload).then(response => {
      console.log("Sent Message to Token: " + token);
      return;
    });
  });
