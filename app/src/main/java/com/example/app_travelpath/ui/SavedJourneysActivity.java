package com.example.app_travelpath.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app_travelpath.R;
import com.example.app_travelpath.data.local.AppDatabase;
import com.example.app_travelpath.model.SavedJourney;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.Executors;

public class SavedJourneysActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SavedJourneysAdapter adapter;
    private LinearLayout layoutEmptyState;
    private ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_journeys);

        recyclerView = findViewById(R.id.recyclerSavedJourneys);
        layoutEmptyState = findViewById(R.id.tvEmptyState);
        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());
        
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SavedJourneysAdapter();
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(journey -> {
            Intent intent = new Intent(SavedJourneysActivity.this, JourneyResultActivity.class);
            intent.putExtra("final_route", (Serializable) journey.spotList);
            startActivity(intent);
        });

        loadJourneys();
        setupSwipeToDelete();
    }

    private void loadJourneys() {
        SharedPreferences prefs = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String currentUsername = prefs.getString("logged_in_username", null);

        if (currentUsername == null) {
            layoutEmptyState.setVisibility(View.VISIBLE);
            return;
        }

        Executors.newSingleThreadExecutor().execute(() -> {
            List<SavedJourney> list = AppDatabase.getDatabase(this).savedJourneyDao().getJourneysByUser(currentUsername);
            
            runOnUiThread(() -> {
                if (list.isEmpty()) {
                    layoutEmptyState.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                } else {
                    layoutEmptyState.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    adapter.setJourneys(list);
                }
            });
        });
    }

    private void setupSwipeToDelete() {
        ItemTouchHelper.SimpleCallback callback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                SavedJourney journeyToDelete = adapter.getJourneyAt(position);

                // --- NOUVEAU : Boîte de dialogue de confirmation ---
                AlertDialog.Builder builder = new AlertDialog.Builder(SavedJourneysActivity.this);
                builder.setTitle("Confirm Deletion");
                builder.setMessage("Are you sure you want to delete this trip?");
                
                builder.setPositiveButton("Yes", (dialog, which) -> {
                    // Suppression confirmée
                    Executors.newSingleThreadExecutor().execute(() -> {
                        AppDatabase.getDatabase(SavedJourneysActivity.this).savedJourneyDao().delete(journeyToDelete);
                    });

                    adapter.removeJourney(position);
                    Toast.makeText(SavedJourneysActivity.this, "Trip deleted", Toast.LENGTH_SHORT).show();

                    if (adapter.getItemCount() == 0) {
                        layoutEmptyState.setVisibility(View.VISIBLE);
                    }
                });

                builder.setNegativeButton("Cancel", (dialog, which) -> {
                    // Action annulée : on remet l'élément en place visuellement
                    adapter.notifyItemChanged(position);
                    dialog.dismiss();
                });

                builder.setCancelable(false); // Empêche de fermer en cliquant à côté
                builder.show();
            }
        };

        new ItemTouchHelper(callback).attachToRecyclerView(recyclerView);
    }
}
