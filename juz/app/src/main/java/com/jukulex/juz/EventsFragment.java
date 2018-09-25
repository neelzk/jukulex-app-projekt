package com.jukulex.juz;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class EventsFragment extends Fragment {
    private static final String LOGTAG = "juzapp - EventsFragment";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_events, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(LOGTAG, "created");

        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            CollectionReference userProperties = FirebaseFirestore.getInstance().collection("UserProperties");

            // get the Uid of the user and then the UserProperties document of that Uid
            userProperties.document(auth.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    // this works but sort of sucks. TODO: deserialize document to UserProperties object
                    if (documentSnapshot.getBoolean("postEventsAllowed")) {
                        Log.d(LOGTAG, "user is allowed to post new events");
                    } else {
                        Log.d(LOGTAG, "user is NOT allowed to post new events");
                    }
                }
            });
        }

    }
}
