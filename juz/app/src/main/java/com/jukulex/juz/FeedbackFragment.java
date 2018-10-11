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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FeedbackFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private static final String LOGTAG = "juzapp FeedbackFragment";
    private UserProperties mCurrentUserProperties;
    private FeedbackRecyclerViewAdapter mFeedbackAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private CollectionReference mFeedbackColRef = db.collection("Feedback");
    private ArrayList<String> mFeedbackList;
    private FloatingActionButton mFabAdd;
    private Context mActivity;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_feedback, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = getActivity();
        mFeedbackList = new ArrayList<>();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recView = getView().findViewById(R.id.recyclerview);
        mFeedbackAdapter = new FeedbackRecyclerViewAdapter(mFeedbackList, mAuth);
        recView.setAdapter(mFeedbackAdapter);
        recView.setLayoutManager(new LinearLayoutManager(getContext()));

        mSwipeRefreshLayout = (SwipeRefreshLayout) getView().findViewById(R.id.swiperefresh);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        mFabAdd = getView().findViewById(R.id.fab);
        if (mAuth.getUid() != null) {
            mFabAdd.setVisibility(View.VISIBLE);
            mFabAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    postNewFeedback();
                }
            });
        }

        fetchFeedbackFromDb();


    }

    private void fetchFeedbackFromDb() {
        mSwipeRefreshLayout.setRefreshing(true);

        Query eventsQuery = mFeedbackColRef.orderBy("timestamp");

        eventsQuery.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                mSwipeRefreshLayout.setRefreshing(false);

                List<DocumentSnapshot> docSnapshots = queryDocumentSnapshots.getDocuments();
                mFeedbackList.clear();
                for (DocumentSnapshot ds : docSnapshots) {
                    // TODO: stringbuilder for "[Time] ([Name]) [message]"
                    if (!ds.exists())
                        continue;

                    StringBuilder feedbackBuilder = new StringBuilder("");
                    Object ts = ds.get("timestamp");
                    Object nm = ds.get("name");
                    Object msg = ds.get("message");

                    if (msg == null)
                        continue;

                    if (msg.toString().length() == 0)
                        continue;

                    if (ts == null)
                        continue;

                    feedbackBuilder.append(ts.toString());

                    if (nm != null) {
                        feedbackBuilder.append(" (von: ").append(nm.toString()).append(")");
                    }

                    feedbackBuilder.append("\n");

                    feedbackBuilder.append(msg.toString());

                    mFeedbackList.add(feedbackBuilder.toString());
                }
                mFeedbackAdapter.notifyDataSetChanged();
            }
        }).addOnFailureListener(new OnFailureListener() {

            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(LOGTAG, "error: " + e.toString());
            }
        });

    }

    private void postNewFeedback() {
        Log.d(LOGTAG, "postNewFeedback");

        final String userId = mAuth.getUid();

        if (userId == null) {
            return;
        }

        LayoutInflater inflater = LayoutInflater.from(mActivity);
        AlertDialog.Builder alert = new AlertDialog.Builder(mActivity);

        LinearLayout ll = new LinearLayout(mActivity);
        ll.setOrientation(LinearLayout.VERTICAL);

        TextView tv_name = new TextView(mActivity);
        tv_name.setText("Name (optional):");
        final EditText ed_name = new EditText(mActivity);

        TextView tv_message = new TextView(mActivity);
        tv_message.setText("Nachricht:");

        final EditText ed_message = new EditText(mActivity);
        ll.addView(tv_message);
        ll.addView(ed_message);
        ll.addView(tv_name);
        ll.addView(ed_name);

//        alert.setTitle("Neue Nachricht");
        alert.setView(ll);

        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String fb = ed_message.getText().toString();
                if (fb.length() == 0) {
                    return;
                }

                String name = ed_name.getText().toString();
                if (name.length() == 0) {
                    name = null;
                }

                Map<String, Object> docData = new HashMap<>();
                docData.put("message", fb);
                docData.put("userId", userId);
                docData.put("timestamp", new Date());
                docData.put("name", name);

                mFeedbackColRef.add(docData).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(LOGTAG, "Message added");
                        fetchFeedbackFromDb();
                    }
                });
            }
        });

        alert.setNegativeButton("Abbrechen", null);
        alert.show();

    }

    @Override
    public void onRefresh() {
        fetchFeedbackFromDb();
    }
}
