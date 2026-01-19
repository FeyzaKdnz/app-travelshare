package com.example.app_travelpath.ui;

import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app_travelpath.R;
import com.example.app_travelpath.model.Spot;

import java.util.List;

public class TimelineAdapter extends RecyclerView.Adapter<TimelineAdapter.TimelineViewHolder> {

    private List<Spot> route;
    private int currentStepIndex = 0;

    public TimelineAdapter(List<Spot> route) {
        this.route = route;
    }

    public void setCurrentStep(int index) {
        this.currentStepIndex = index;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TimelineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_timeline, parent, false);
        return new TimelineViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TimelineViewHolder holder, int position) {
        Spot spot = route.get(position);
        holder.tvName.setText(spot.getName());

        if (position < currentStepIndex) {
            // Étape passée (Vert)
            holder.dot.setColorFilter(Color.parseColor("#4CAF50"));
            holder.topLine.setBackgroundColor(Color.parseColor("#4CAF50"));
            holder.bottomLine.setBackgroundColor(Color.parseColor("#4CAF50"));
            holder.tvName.setTextColor(Color.parseColor("#4CAF50"));
            holder.tvName.setTypeface(null, Typeface.NORMAL);
        } else if (position == currentStepIndex) {
            holder.dot.setColorFilter(Color.parseColor("#6200EE"));
            holder.topLine.setBackgroundColor(position == 0 ? Color.TRANSPARENT : Color.parseColor("#4CAF50"));
            holder.bottomLine.setBackgroundColor(Color.parseColor("#DDDDDD"));
            holder.tvName.setTextColor(Color.BLACK);
            holder.tvName.setTypeface(null, Typeface.BOLD);
        } else {
            holder.dot.setColorFilter(Color.parseColor("#BDBDBD"));
            holder.topLine.setBackgroundColor(Color.parseColor("#DDDDDD"));
            holder.bottomLine.setBackgroundColor(Color.parseColor("#DDDDDD"));
            holder.tvName.setTextColor(Color.parseColor("#BDBDBD"));
            holder.tvName.setTypeface(null, Typeface.NORMAL);
        }

        if (position == 0) holder.topLine.setVisibility(View.INVISIBLE);
        else holder.topLine.setVisibility(View.VISIBLE);

        if (position == route.size() - 1) holder.bottomLine.setVisibility(View.INVISIBLE);
        else holder.bottomLine.setVisibility(View.VISIBLE);
    }

    @Override
    public int getItemCount() {
        return route.size();
    }

    static class TimelineViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        ImageView dot;
        View topLine, bottomLine;

        public TimelineViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvTimelineSpotName);
            dot = itemView.findViewById(R.id.imgStatusDot);
            topLine = itemView.findViewById(R.id.viewTopLine);
            bottomLine = itemView.findViewById(R.id.viewBottomLine);
        }
    }
}
