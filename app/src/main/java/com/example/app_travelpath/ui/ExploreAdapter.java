package com.example.app_travelpath.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app_travelpath.R;
import com.example.app_travelpath.model.SavedJourney;

import java.util.ArrayList;
import java.util.List;

public class ExploreAdapter extends RecyclerView.Adapter<ExploreAdapter.ExploreViewHolder> {

    private List<SavedJourney> sharedJourneys = new ArrayList<>();
    private OnExploreItemClickListener listener;

    public interface OnExploreItemClickListener {
        void onItemClick(SavedJourney journey);
        void onLikeClick(SavedJourney journey, int position);
    }

    public void setOnExploreItemClickListener(OnExploreItemClickListener listener) {
        this.listener = listener;
    }

    public void setJourneys(List<SavedJourney> journeys) {
        this.sharedJourneys = journeys;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ExploreViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_shared_journey, parent, false);
        return new ExploreViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExploreViewHolder holder, int position) {
        SavedJourney journey = sharedJourneys.get(position);
        holder.tvName.setText(journey.name);
        holder.tvAuthor.setText("by " + journey.creatorUsername);
        holder.tvDate.setText(journey.date);
        holder.tvLikeCount.setText(String.valueOf(journey.likesCount));

        // On gère le clic sur la carte
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(journey);
        });

        // On gère le clic sur le bouton Like
        holder.btnLike.setOnClickListener(v -> {
            if (listener != null) listener.onLikeClick(journey, position);
        });
    }

    @Override
    public int getItemCount() {
        return sharedJourneys.size();
    }

    public void updateItem(int position, SavedJourney updatedJourney) {
        sharedJourneys.set(position, updatedJourney);
        notifyItemChanged(position);
    }

    static class ExploreViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvAuthor, tvDate, tvLikeCount;
        LinearLayout btnLike;
        ImageView imgLike;

        public ExploreViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvJourneyName);
            tvAuthor = itemView.findViewById(R.id.tvAuthorName);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvLikeCount = itemView.findViewById(R.id.tvLikeCount);
            btnLike = itemView.findViewById(R.id.btnLike);
            imgLike = itemView.findViewById(R.id.imgLike);
        }
    }
}
