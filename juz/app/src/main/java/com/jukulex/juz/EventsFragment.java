package com.jukulex.juz;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class EventsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private static final String LOGTAG = "juzapp - EventsFragment";
    private UserProperties mCurrentUserProperties;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private CollectionReference mEventsColRef = db.collection("Events");
    private EventsRecyclerViewAdapter mEventsAdapter;
    private ArrayList<Event> mEventsList;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private FloatingActionButton mFabAdd;
    private Context mActivity;

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

        mActivity = getActivity();
        mEventsList = new ArrayList<>();

        // if a user is logged in, get his/her properties
        if (mAuth.getCurrentUser() != null) {
            String userPropDocPath = "UserProperties/" + mAuth.getUid();

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
        if (mCurrentUserProperties == null || !mCurrentUserProperties.isPostMapMarkersAllowed()) {
            Toast.makeText(mActivity, "Du darfst keine Events posten.", Toast.LENGTH_LONG).show();
            return;
        }

        LayoutInflater inflater = LayoutInflater.from(mActivity);
        View view = getLayoutInflater().inflate( R.layout.event_input_dialog, null);

        final EditText inputTitle = view.findViewById(R.id.ed_title);
        final EditText inputDescr = view.findViewById(R.id.ed_desc);
        final DatePicker dp = view.findViewById(R.id.datepicker);
        final TimePicker tp = view.findViewById(R.id.timepicker);
        tp.setIs24HourView(true);

        // create an alert dialog to let the user enter a title and an optional description
        // by creating two EditTexts which are added to a LinearLayout
        AlertDialog.Builder alert = new AlertDialog.Builder(mActivity);

        alert.setTitle("Bitte Titel, Beschreibung und Datum eingeben");
        alert.setView(view);

        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // title is mandatory
                String title = inputTitle.getText().toString();
                if (title.length() == 0)
                    return;

                // FIXME: using deprecated methods here
                Calendar calendar = new GregorianCalendar(dp.getYear(), dp.getMonth(), dp.getDayOfMonth(), tp.getCurrentHour(), tp.getCurrentMinute());
                Date date = new Date(calendar.getTimeInMillis());

                String description = inputDescr.getText().toString();
                if (description.length() == 0)
                    description = null;

                // save the new event to the database
                Event ev = new Event(title, description, date, null, null ,null);
                mEventsColRef.add(ev).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(LOGTAG, "Event added");
                        fetchEventsFromDb();
                    }
                });
            }
        });

        alert.setNegativeButton("Abbrechen", null);
        alert.show();

    }

    private void fetchEventsFromDb() {
        mSwipeRefreshLayout.setRefreshing(true);
        // get the events from the db ordered by the date they start.
        // events that started more than 12 hours (4.23 mio milliseconds) ago are excluded.
        Date twelveHoursAgo = new Date();
        twelveHoursAgo.setTime(twelveHoursAgo.getTime() - 43200000);

        Query eventsQuery = mEventsColRef
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
                    // get the Id from the document in the db and set is as member of the object
                    // to be able to recognize it on click
                    // seems to be a bit hacky
                    ev.setId(ds.getId());
                    mEventsList.add(ev);
                }
                mEventsAdapter.notifyDataSetChanged();
            }
        });

    }
}
