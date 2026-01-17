package com.example.app_travelpath.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app_travelpath.R;
import com.example.app_travelpath.data.local.AppDatabase;
import com.example.app_travelpath.model.SavedJourney;
import com.google.android.material.chip.ChipGroup;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;

public class ExploreActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ExploreAdapter adapter;
    private LinearLayout layoutEmpty;
    private ImageButton btnBack;
    private SearchView searchCity;
    private ChipGroup chipGroupSort;

    private List<SavedJourney> allSharedJourneys = new ArrayList<>();
    private String currentSearchQuery = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore);

        recyclerView = findViewById(R.id.recyclerExplore);
        layoutEmpty = findViewById(R.id.layoutEmpty);
        btnBack = findViewById(R.id.btnBack);
        searchCity = findViewById(R.id.searchCity);
        chipGroupSort = findViewById(R.id.chipGroupSort);

        btnBack.setOnClickListener(v -> finish());

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ExploreAdapter();
        recyclerView.setAdapter(adapter);

        adapter.setOnExploreItemClickListener(new ExploreAdapter.OnExploreItemClickListener() {
            @Override
            public void onItemClick(SavedJourney journey) {
                Intent intent = new Intent(ExploreActivity.this, JourneyResultActivity.class);
                intent.putExtra("final_route", (Serializable) journey.spotList);
                startActivity(intent);
            }

            @Override
            public void onLikeClick(SavedJourney journey, int position) {
                handleLike(journey, position);
            }
        });

        searchCity.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) { return false; }

            @Override
            public boolean onQueryTextChange(String newText) {
                currentSearchQuery = newText.toLowerCase();
                applyFiltersAndSort();
                return true;
            }
        });

        chipGroupSort.setOnCheckedChangeListener((group, checkedId) -> applyFiltersAndSort());

        loadSharedJourneys();
    }

    private void loadSharedJourneys() {
        Executors.newSingleThreadExecutor().execute(() -> {
            allSharedJourneys = AppDatabase.getDatabase(this).savedJourneyDao().getSharedJourneys();
            runOnUiThread(this::applyFiltersAndSort);
        });
    }

    private void applyFiltersAndSort() {
        List<SavedJourney> filteredList = new ArrayList<>();
        for (SavedJourney j : allSharedJourneys) {
            boolean matchesSearch = false;
            if (currentSearchQuery.isEmpty() || j.name.toLowerCase().contains(currentSearchQuery)) {
                matchesSearch = true;
            } else if (j.spotList != null) {
                for (com.example.app_travelpath.model.Spot spot : j.spotList) {
                    if (spot.getCity().toLowerCase().contains(currentSearchQuery)) {
                        matchesSearch = true;
                        break;
                    }
                }
            }
            if (matchesSearch) filteredList.add(j);
        }

        int checkedId = chipGroupSort.getCheckedChipId();
        if (checkedId == R.id.chipOldest) {
            Collections.sort(filteredList, (a, b) -> a.date.compareTo(b.date));
        } else if (checkedId == R.id.chipMostLiked) {
            Collections.sort(filteredList, (a, b) -> Integer.compare(b.likesCount, a.likesCount));
        } else {
            Collections.sort(filteredList, (a, b) -> b.date.compareTo(a.date));
        }

        if (filteredList.isEmpty()) {
            layoutEmpty.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            layoutEmpty.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            adapter.setJourneys(filteredList);
        }
    }

    private void handleLike(SavedJourney journey, int position) {
        // 1. Récupérer l'utilisateur actuel
        SharedPreferences prefs = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String currentUsername = prefs.getString("logged_in_username", "Guest");

        if (journey.likedByUsers == null) {
            journey.likedByUsers = new ArrayList<>();
        }

        // 2. Logique de bascule (Toggle)
        if (journey.likedByUsers.contains(currentUsername)) {
            // Déjà liké -> On retire le like
            journey.likedByUsers.remove(currentUsername);
            journey.likesCount--;
            Toast.makeText(this, "Like removed", Toast.LENGTH_SHORT).show();
        } else {
            // Pas encore liké -> On ajoute le like
            journey.likedByUsers.add(currentUsername);
            journey.likesCount++;
            Toast.makeText(this, "You liked this trip!", Toast.LENGTH_SHORT).show();
        }

        // 3. Sauvegarde asynchrone
        Executors.newSingleThreadExecutor().execute(() -> {
            AppDatabase.getDatabase(this).savedJourneyDao().update(journey);
            runOnUiThread(() -> {
                // Mise à jour de la mémoire locale et de l'adaptateur
                for (SavedJourney j : allSharedJourneys) {
                    if (j.id == journey.id) {
                        j.likesCount = journey.likesCount;
                        j.likedByUsers = journey.likedByUsers;
                        break;
                    }
                }
                adapter.updateItem(position, journey);
            });
        });
    }
}
