package com.jukulex.juz;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EventsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private static final String LOGTAG = "juzapp - EventsFragment";
    private UserProperties mCurrentUserProperties;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private EventsRecyclerViewAdapter mEventsAdapter;
    private ArrayList<Event> mEventsList;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private FloatingActionButton mFabAdd;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_events, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recView = getView().findViewById(R.id.recyclerview);
        mEventsAdapter = new EventsRecyclerViewAdapter(mEventsList, mAuth);
        recView.setAdapter(mEventsAdapter);
        recView.setLayoutManager(new LinearLayoutManager(getContext()));

        mSwipeRefreshLayout = (SwipeRefreshLayout) getView().findViewById(R.id.swiperefresh);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        fetchEventsFromDb();

        mFabAdd = getView().findViewById(R.id.fab);
        mFabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postNewEvent();
            }
        });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseAuth auth = FirebaseAuth.getInstance();

        mEventsList = new ArrayList<>();

        // if a user is logged in, get his/her properties
        if (auth.getCurrentUser() != null) {
            String userPropDocPath = "UserProperties/" + auth.getUid();

            DocumentReference userPropDocRef = db.document(userPropDocPath);
            userPropDocRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()) {
                        mCurrentUserProperties = documentSnapshot.toObject(UserProperties.class);
                        if (mCurrentUserProperties.isPostEventsAllowed()) {
                            Log.d(LOGTAG, "user is allowed to post new events");
                            mFabAdd.setVisibility(View.VISIBLE);
                        } else {
                            Log.d(LOGTAG, "user is NOT allowed to post new events");

                        }
                    }

                }
            });
        }

    }

    @Override
    public void onRefresh() {
        fetchEventsFromDb();
    }

    private void postNewEvent() {
        // TODO: implement me
    }

    private void fetchEventsFromDb() {
        mSwipeRefreshLayout.setRefreshing(true);
        // get the events from the db ordered by the date they start.
        // events that started more than 12 hours (4.23 mio milliseconds) ago are excluded.
        Date twelveHoursAgo = new Date();
        twelveHoursAgo.setTime(twelveHoursAgo.getTime() - 43200000);

        Query eventsQuery = db.collection("Events")
                .whereGreaterThan("startDate", twelveHoursAgo)
                .orderBy("startDate");

        eventsQuery.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                mSwipeRefreshLayout.setRefreshing(false);

                List<DocumentSnapshot> docSnapshots = queryDocumentSnapshots.getDocuments();
                mEventsList.clear();
                for (DocumentSnapshot ds : docSnapshots) {
                    Event ev = ds.toObject(Event.class);
                    mEventsList.add(ev);
                }
                mEventsAdapter.notifyDataSetChanged();
            }
        });

    }
}
