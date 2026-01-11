package com.example.app_travelpath.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import com.bumptech.glide.Glide;
import com.example.app_travelpath.R;
import com.example.app_travelpath.model.Spot;

import java.util.List;

public class ActiveJourneyActivity extends AppCompatActivity {

    private List<Spot> route;
    private int currentStepIndex = 0;
    private TextView tvStepCounter, tvSpotName, tvSpotDetails, tvCategoryBadge, tvOpeningHours;
    private ImageView imgSpotMain;
    private Button btnNextStep, btnViewOnMap, btnFinalize;
    private SearchView searchStep;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active_journey);

        route = (List<Spot>) getIntent().getSerializableExtra("active_route");

        if (route == null || route.isEmpty()) {
            Toast.makeText(this, "Erreur de chargement de la route.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        loadSpot(currentStepIndex);

        btnNextStep.setOnClickListener(v -> {
            if (currentStepIndex < route.size() - 1) {
                currentStepIndex++;
                loadSpot(currentStepIndex);
            } else {
                Toast.makeText(this, "Dernière étape atteinte !", Toast.LENGTH_SHORT).show();
            }
        });

        btnViewOnMap.setOnClickListener(v -> {
            Spot current = route.get(currentStepIndex);
            launchMap(current);
        });

        btnFinalize.setOnClickListener(v -> {
            Intent intent = new Intent(ActiveJourneyActivity.this, EndJourneyActivity.class);
            intent.putExtra("finished_route", (java.io.Serializable) route);
            startActivity(intent);
            finish();
        });

        searchStep.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                findAndJumpToSpot(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) { return false; }
        });
    }

    private void initViews() {
        tvStepCounter = findViewById(R.id.tvStepCounter);
        tvSpotName = findViewById(R.id.tvSpotName);
        tvSpotDetails = findViewById(R.id.tvSpotDetails);
        tvCategoryBadge = findViewById(R.id.tvCategoryBadge);
        imgSpotMain = findViewById(R.id.imgSpotMain);
        btnNextStep = findViewById(R.id.btnNextStep);
        btnViewOnMap = findViewById(R.id.btnViewOnMap);
        btnFinalize = findViewById(R.id.btnFinalize);
        searchStep = findViewById(R.id.searchStep);
        tvOpeningHours = findViewById(R.id.tvOpeningHours);
    }

    private void loadSpot(int index) {
        Spot spot = route.get(index);

        tvStepCounter.setText("Step " + (index + 1) + " of " + route.size());
        tvSpotName.setText(spot.getName());
        tvSpotDetails.setText("Category: " + spot.getCategoryType() + "\n" +
                "Price: " + spot.getPrice() + "€\n" +
                "Duration: " + spot.getDuration() + "h");

        tvCategoryBadge.setText(String.valueOf(spot.getCategoryType()));

        int defaultImageResId;

        /* -------------- GESTION TYPE DE LIEU -------------- */

        if (spot.getCategoryType() != null) {
            switch (spot.getCategoryType()) {
                case CULTURE:
                case MUSEUM:
                    defaultImageResId = R.drawable.culture;
                    break;
                case FOOD:
                    defaultImageResId = R.drawable.food;
                    break;
                case LEISURE:
                case DISCOVERY:
                    defaultImageResId = R.drawable.culture;
                    break;
                default:
                    defaultImageResId = R.drawable.colmar;
                    break;
            }
        } else {
            defaultImageResId = R.drawable.colmar;
        }

        /* ---------- AFFICHAGE DE L'IMAGE AVEC GLIDE ET TRAVELSHARE ---------- */

        if (spot.getExternalImageUrl() != null && !spot.getExternalImageUrl().isEmpty()) {

            Glide.with(this)
                    .load(spot.getExternalImageUrl())
                    .placeholder(defaultImageResId)
                    .error(defaultImageResId)
                    .centerCrop()
                    .into(imgSpotMain);

        } else {

            Glide.with(this)
                    .load(defaultImageResId)
                    .centerCrop()
                    .into(imgSpotMain);
        }

        if (index == route.size() - 1) {
            btnNextStep.setText("Finish");
        } else {
            btnNextStep.setText("Next Step");
            btnNextStep.setEnabled(true);
        }

        String detailsText = "Category: " + spot.getCategoryType() + "\n" +
                "Price: " + spot.getPrice() + "€\n" +
                "Duration: " + spot.getDuration() + "h";

        tvSpotDetails.setText(detailsText);

        /* --------- GESTION DES HORAIRES D'OUVERTURE --------- */

        if (spot.getOpeningHours() != null && !spot.getOpeningHours().isEmpty()) {
            tvOpeningHours.setVisibility(android.view.View.VISIBLE);
            String cleanHours = spot.getOpeningHours().replace(";", "\n");
            tvOpeningHours.setText("OPENING HOURS :\n" + cleanHours);
        } else {
            tvOpeningHours.setVisibility(android.view.View.GONE);
        }
    }

    private void findAndJumpToSpot(String query) {
        if (query == null || query.isEmpty()) return;
        String lowerQuery = query.toLowerCase();
        for (int i = 0; i < route.size(); i++) {
            if (route.get(i).getName().toLowerCase().contains(lowerQuery)) {
                currentStepIndex = i;
                loadSpot(currentStepIndex);
                searchStep.clearFocus();
                return;
            }
        }
        Toast.makeText(this, "Lieu non trouvé", Toast.LENGTH_SHORT).show();
    }

    private void launchMap(Spot spot) {
        String geoUri = "geo:" + spot.getLatitude() + "," + spot.getLongitude() +
                "?q=" + spot.getLatitude() + "," + spot.getLongitude() +
                "(" + Uri.encode(spot.getName()) + ")";
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(geoUri));
        try {
            startActivity(mapIntent);
        } catch (Exception e) {
            Toast.makeText(this, "Pas d'app de carte installée", Toast.LENGTH_SHORT).show();
        }
    }
}