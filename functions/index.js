const functions = require("firebase-functions");
const admin = require("firebase-admin");
admin.initializeApp();

exports.sendNewsNotification = functions.firestore
  .document("news/{newsId}")
  .onCreate((snap, context) => {
    const data = snap.data();
    const title = data.title || "Új hír";
    const body  = data.summary || data.body || "";

    const payload = {
      notification: {
        title: title,
        body: body,
      },
      topic: "news"
    };

    return admin.messaging().send(payload)
      .then(response => {
        console.log("Értesítés elküldve:", response);
      })
      .catch(err => {
        console.error("Hiba értesítés küldésekor:", err);
      });
  });
