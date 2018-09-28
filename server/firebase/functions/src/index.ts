import * as functions from 'firebase-functions';
import * as admin from 'firebase-admin'
admin.initializeApp();

export const createUserProperties = functions.auth.user().onCreate((user) => {
    console.log('creating properties for user "' + user.uid + '"');

    return admin.firestore().collection("UserProperties").doc(user.uid).set({
        postEventsAllowed: false,
        postMapMarkersAllowed: true,
        commentAllowed: true,
        deactivated: false
    });  
});

export const deleteUserProperties = functions.auth.user().onDelete((user) => {
    console.log('deleting properties ofuser"' + user.uid + '"');

    return admin.firestore().doc("/UserProperties/" + user.uid).delete();
});
