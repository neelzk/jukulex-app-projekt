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

import java.util.ArrayList;

public class FeedbackRecyclerViewAdapter extends RecyclerView.Adapter<FeedbackRecyclerViewAdapter.ViewHolder> {
    private static final String LOGTAG = "juzapp FeedbackRCA";
    private ArrayList<String> mFeedback;
    private FirebaseAuth mAuth;

    public FeedbackRecyclerViewAdapter(ArrayList<String> feedback, FirebaseAuth auth) {
        mFeedback = feedback;
        mAuth = auth;
    }

    @NonNull
    @Override
    public FeedbackRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_feedback_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final FeedbackRecyclerViewAdapter.ViewHolder holder, final int position) {
        holder.tv_message.setText(mFeedback.get(position));
    }

    @Override
    public int getItemCount() {
        return mFeedback.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_message;

        public ViewHolder(View itemView) {
            super(itemView);
            tv_message = itemView.findViewById(R.id.tv_feedback);
        }
    }
}
