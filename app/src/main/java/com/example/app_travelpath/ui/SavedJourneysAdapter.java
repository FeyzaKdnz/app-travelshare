package com.example.app_travelpath.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.app_travelpath.R;
import com.example.app_travelpath.model.SavedJourney;
import java.util.ArrayList;
import java.util.List;

public class SavedJourneysAdapter extends RecyclerView.Adapter<SavedJourneysAdapter.ViewHolder> {

    private List<SavedJourney> journeys = new ArrayList<>();
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(SavedJourney journey);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setJourneys(List<SavedJourney> journeys) {
        this.journeys = journeys;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_saved_journey, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SavedJourney journey = journeys.get(position);
        holder.tvName.setText(journey.name);
        holder.tvDate.setText(journey.date);
        
        // Affichage du nombre de likes récoltés par ce parcours
        holder.tvLikeCount.setText(String.valueOf(journey.likesCount));

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(journey);
        });
    }

    @Override
    public int getItemCount() {
        return journeys.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvDate, tvLikeCount;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvJourneyName);
            tvDate = itemView.findViewById(R.id.tvJourneyDate);
            tvLikeCount = itemView.findViewById(R.id.tvLikeCount);
        }
    }

    public SavedJourney getJourneyAt(int position) {
        return journeys.get(position);
    }

    public void removeJourney(int position) {
        journeys.remove(position);
        notifyItemRemoved(position);
    }
}
