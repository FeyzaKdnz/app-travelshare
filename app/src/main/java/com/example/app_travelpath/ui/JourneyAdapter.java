package com.example.app_travelpath.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.app_travelpath.R;
import com.example.app_travelpath.model.Spot;
import java.util.ArrayList;
import java.util.List;

public class JourneyAdapter extends RecyclerView.Adapter<JourneyAdapter.SpotViewHolder> {

    private List<Spot> spots = new ArrayList<>();
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Spot spot);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setSpots(List<Spot> newSpots) {
        this.spots = newSpots;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SpotViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_spot, parent, false);
        return new SpotViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SpotViewHolder holder, int position) {
        Spot spot = spots.get(position);

        holder.name.setText((position + 1) + ". " + spot.getName());
        holder.details.setText(spot.getCategoryType() + " • " + spot.getPrice() + "€");

        /* -------- GESTION DES IMAGES -------- */

        if (spot.getExternalImageUrl() != null && !spot.getExternalImageUrl().isEmpty()) {

            Glide.with(holder.itemView.getContext())
                    .load(spot.getExternalImageUrl())
                    .placeholder(R.drawable.culture)
                    .error(R.drawable.culture)
                    .centerCrop()
                    .into(holder.image);

        } else {
            if (spot.getCategoryType() != null) {
                switch (spot.getCategoryType()) {
                    case MUSEUM:
                    case CULTURE:
                        holder.image.setImageResource(R.drawable.culture);
                        break;
                    case FOOD:
                        holder.image.setImageResource(R.drawable.food);
                        break;
                    case LEISURE:
                    case DISCOVERY:
                        holder.image.setImageResource(R.drawable.detente);
                        break;
                    default:
                        holder.image.setImageResource(R.drawable.culture);
                        break;
                }
            } else {
                holder.image.setImageResource(R.drawable.culture);
            }
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(spot);
        });
    }

    @Override
    public int getItemCount() { return spots.size(); }

    static class SpotViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView details;
        ImageView image;

        public SpotViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tvName);
            details = itemView.findViewById(R.id.tvInfo);
            image = itemView.findViewById(R.id.imgSpot);
        }
    }
}