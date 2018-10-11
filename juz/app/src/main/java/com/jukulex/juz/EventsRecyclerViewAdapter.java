package com.jukulex.juz;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class EventsRecyclerViewAdapter extends RecyclerView.Adapter<EventsRecyclerViewAdapter.ViewHolder> {
    private static final String LOGTAG = "Juzapp EventsRCA";
    private ArrayList<Event> mEvents;
    private FirebaseAuth mAuth;

    public EventsRecyclerViewAdapter(ArrayList<Event> events, FirebaseAuth auth) {
        mEvents = events;
        mAuth = auth;
    }

    @NonNull
    @Override
    public EventsRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_events_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final EventsRecyclerViewAdapter.ViewHolder holder, final int position) {
        Event event = mEvents.get(position);
        holder.tv_title.setText(event.getTitle());
        holder.tv_description.setText(event.getDescription());

        if (mAuth.getUid() == null) {
            holder.btn_participate.setVisibility(View.GONE);
        } else {
            holder.btn_participate.setVisibility(View.VISIBLE);
        }

        if (event.getStartDate() != null) {
            holder.tv_date.setText(event.getStartDate().toString());
        } else {
            holder.tv_date.setText("");
        }

        if (holder.isExpanded) {
            holder.detailLayout.setVisibility(View.VISIBLE);
        } else {
            holder.detailLayout.setVisibility(View.GONE);
        }

        holder.btn_participate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(LOGTAG, "btn clicked");
                ((Button) view).setEnabled(false);

                if (mAuth.getUid() == null)
                    return;

                Log.d(LOGTAG, "clicked pos " + position + " id " + mEvents.get(position).getId());
                // TODO: add user mAuth.getUid() to event mEvents.get(position).getId()
            }
        });

        holder.img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(LOGTAG, "TODO: upload picture for event " + mEvents.get(position).getId());
            }
        });

    }

    @Override
    public int getItemCount() {
        return mEvents.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private RelativeLayout parentLayout;
        private LinearLayout detailLayout;
        private ImageView img;
        private Button btn_participate;
        private TextView tv_title;
        private TextView tv_description;
        private TextView tv_date;
        private boolean isExpanded = false;

        public ViewHolder(View itemView) {
            super(itemView);

            parentLayout = itemView.findViewById(R.id.parent_layout);
            detailLayout = itemView.findViewById(R.id.layout_details);
            img = itemView.findViewById(R.id.image);
            btn_participate = itemView.findViewById(R.id.btn_participate);
            tv_title = itemView.findViewById(R.id.tv_title);
            tv_description = itemView.findViewById(R.id.tv_description);
            tv_date = itemView.findViewById(R.id.tv_date);

            img.setClickable(true);

            parentLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (isExpanded) {
                        isExpanded = false;
                        detailLayout.setVisibility(View.GONE);
                    } else {
                        isExpanded = true;
                        detailLayout.setVisibility(View.VISIBLE);
                    }
                }
            });

        }

    }
    
}
