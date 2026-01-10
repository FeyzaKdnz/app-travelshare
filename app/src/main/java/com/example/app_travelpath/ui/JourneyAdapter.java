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

        // --- GESTION INTELLIGENTE DES IMAGES ---

        // 1. D'abord, on vérifie si on a une vraie photo venant de TravelShare (l'API du binôme)
        if (spot.getExternalImageUrl() != null && !spot.getExternalImageUrl().isEmpty()) {

            Glide.with(holder.itemView.getContext())
                    .load(spot.getExternalImageUrl())
                    .placeholder(R.drawable.culture) // Image d'attente
                    .error(R.drawable.culture)       // En cas d'erreur de chargement
                    .centerCrop()
                    .into(holder.image); // <--- CORRECTION : On utilise 'holder.image' défini en bas

        } else {
            // 2. Sinon, on met l'image par défaut selon la catégorie
            if (spot.getCategoryType() != null) {
                switch (spot.getCategoryType()) {
                    case MUSEUM:
                    case CULTURE:
                        // Assure-toi que ces drawables (culture, food, detente) existent bien !
                        // Sinon remplace par R.drawable.bg_paris ou autre
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

        // Clic sur l'élément
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(spot);
        });
    }

    @Override
    public int getItemCount() { return spots.size(); }

    // --- ViewHolder ---
    static class SpotViewHolder extends RecyclerView.ViewHolder {
        // C'est ici qu'on définit les noms des variables JAVA
        TextView name;
        TextView details;
        ImageView image; // <--- La variable s'appelle "image"

        public SpotViewHolder(@NonNull View itemView) {
            super(itemView);
            // On lie les variables Java aux IDs XML
            name = itemView.findViewById(R.id.tvName);
            details = itemView.findViewById(R.id.tvInfo);
            image = itemView.findViewById(R.id.imgSpot);   // "image" contrôle l'ImageView "imgSpot"
        }
    }
}