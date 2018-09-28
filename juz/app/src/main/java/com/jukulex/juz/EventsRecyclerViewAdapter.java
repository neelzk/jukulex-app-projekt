package com.jukulex.juz;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class EventsRecyclerViewAdapter extends RecyclerView.Adapter<EventsRecyclerViewAdapter.ViewHolder> {
    private static final String LOGTAG = "Juzapp EventsRCA";
    private ArrayList<Event> mEvents;

    public EventsRecyclerViewAdapter(ArrayList<Event> events) {
        mEvents = events;
    }

    @NonNull
    @Override
    public EventsRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_events_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final EventsRecyclerViewAdapter.ViewHolder holder, int position) {
        Event event = mEvents.get(position);
        holder.tv_title.setText(event.getTitle());
        holder.tv_description.setText(event.getDescription());
        if (event.getStartDate() != null) {
            holder.tv_date.setText(event.getStartDate().toString());
        }

        if (holder.isExpanded) {
            holder.detailLayout.setVisibility(View.VISIBLE);
        } else {
            holder.detailLayout.setVisibility(View.GONE);
        }

        // FIXME: when scrolling and views are recycled, they may incorrectly be visible/gone

        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (holder.isExpanded) {
                    holder.isExpanded = false;
                    holder.detailLayout.setVisibility(View.GONE);
                } else {
                    holder.isExpanded = true;
                    holder.detailLayout.setVisibility(View.VISIBLE);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return mEvents.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout parentLayout;
        LinearLayout detailLayout;
        ImageView img;
        TextView tv_title;
        TextView tv_description;
        TextView tv_date;
        boolean isExpanded = false;

        public ViewHolder(View itemView) {
            super(itemView);

            parentLayout = itemView.findViewById(R.id.parent_layout);
            detailLayout = itemView.findViewById(R.id.layout_details);
            img = itemView.findViewById(R.id.image);
            tv_title = itemView.findViewById(R.id.tv_title);
            tv_description = itemView.findViewById(R.id.tv_description);
            tv_date = itemView.findViewById(R.id.tv_date);
        }
    }
    
}
