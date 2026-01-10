package com.example.app_travelpath.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton; // Import ajouté
import android.widget.LinearLayout; // Import ajouté pour le nouvel EmptyState

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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
    private LinearLayout layoutEmptyState; // Changé de TextView à LinearLayout
    private ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_journeys);

        recyclerView = findViewById(R.id.recyclerSavedJourneys);
        layoutEmptyState = findViewById(R.id.tvEmptyState); // L'ID est resté le même mais c'est un Layout
        btnBack = findViewById(R.id.btnBack);

        // Action bouton retour
        btnBack.setOnClickListener(v -> finish());

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SavedJourneysAdapter();
        recyclerView.setAdapter(adapter);

        // Clic sur un item
        adapter.setOnItemClickListener(journey -> {
            Intent intent = new Intent(SavedJourneysActivity.this, JourneyResultActivity.class);
            intent.putExtra("final_route", (Serializable) journey.spotList);
            // On peut aussi passer le nom si besoin pour l'affichage
            startActivity(intent);
        });

        loadJourneys();
        setupSwipeToDelete();
    }

    private void loadJourneys() {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<SavedJourney> list = AppDatabase.getDatabase(this).savedJourneyDao().getAllJourneys();
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
        new androidx.recyclerview.widget.ItemTouchHelper(new androidx.recyclerview.widget.ItemTouchHelper.SimpleCallback(0, androidx.recyclerview.widget.ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                SavedJourney journeyToDelete = adapter.getJourneyAt(position);

                Executors.newSingleThreadExecutor().execute(() -> {
                    AppDatabase.getDatabase(SavedJourneysActivity.this).savedJourneyDao().delete(journeyToDelete);
                });

                adapter.removeJourney(position);
                android.widget.Toast.makeText(SavedJourneysActivity.this, "Trip deleted", android.widget.Toast.LENGTH_SHORT).show();

                if (adapter.getItemCount() == 0) {
                    layoutEmptyState.setVisibility(View.VISIBLE);
                }
            }
        }).attachToRecyclerView(recyclerView);
    }
}