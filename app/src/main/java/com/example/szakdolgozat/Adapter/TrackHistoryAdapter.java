package com.example.szakdolgozat.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.szakdolgozat.Object.TrackHistory;
import com.example.szakdolgozat.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class TrackHistoryAdapter extends RecyclerView.Adapter<TrackHistoryAdapter.viewHolder> implements Filterable {
    private ArrayList<TrackHistory> mTrackHistoryData;
    private ArrayList<TrackHistory> mTrackHistoryDataAll;

    private FirebaseAuth mAuth;

    private FirebaseFirestore mStore;

    private String currentUserUID;
    private Context mContext;

    private int lastPosition = -1;

    TrackHistoryAdapter (Context context, ArrayList<TrackHistory> itemsData) {
        this.mTrackHistoryData = itemsData;
        this.mTrackHistoryDataAll = itemsData;
        this.mContext = context;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mAuth = FirebaseAuth.getInstance();
        mStore = FirebaseFirestore.getInstance();
        currentUserUID = mAuth.getCurrentUser().getUid();

        return new viewHolder(LayoutInflater.from(mContext).inflate(R.layout.activity_history_menu, parent, false));
    }

    @Override
    public void onBindViewHolder(TrackHistoryAdapter.viewHolder holder, int position) {
        TrackHistory currentTrack = mTrackHistoryData.get(position);

        holder.bindTo(currentTrack);
    }

    @Override
    public int getItemCount() {
        return mTrackHistoryData.size();
    }

    @Override
    public Filter getFilter() {
        return trackFilter;
    }

    private Filter trackFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            ArrayList<TrackHistory> filteredList = new ArrayList<>();
            FilterResults results = new FilterResults();

            if (charSequence == null || charSequence.length() == 0) {

            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {

        }
    };



    class viewHolder extends RecyclerView.ViewHolder {
        private TextView mDate;
        private TextView mDistance;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            mDate = itemView.findViewById(R.id.trackDate);
            mDistance = itemView.findViewById(R.id.trackDistance);

            itemView.findViewById(R.id.track).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
        }

        public void bindTo(TrackHistory currentTrack) {
            mDate.setText(currentTrack.getDate().toString());
            mDistance.setText(currentTrack.getDistance().toString() + " km");
        }
    }
}


