package com.example.app_travelpath.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.app_travelpath.R;
import com.example.app_travelpath.model.Spot;

import java.util.List;

public class ActiveJourneyActivity extends AppCompatActivity {

    private List<Spot> route;
    private int currentStepIndex = 0;
    private TextView tvStepCounter, tvSpotName, tvSpotDetails, tvCategoryBadge, tvOpeningHours, tvAuthorName;
    private ImageView imgSpotMain;
    private Button btnNextStep, btnViewOnMap, btnFinalize;
    private SearchView searchStep;
    private DrawerLayout drawerLayout;
    private ImageButton btnOpenTimeline;
    private RecyclerView recyclerTimeline;
    private TimelineAdapter timelineAdapter;

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
        setupTimeline();
        loadSpot(currentStepIndex);

        btnNextStep.setOnClickListener(v -> {
            if (currentStepIndex < route.size() - 1) {
                currentStepIndex++;
                loadSpot(currentStepIndex);
            } else {
                Toast.makeText(this, "Destination finale atteinte !", Toast.LENGTH_SHORT).show();
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

        btnOpenTimeline.setOnClickListener(v -> {
            drawerLayout.openDrawer(GravityCompat.START);
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
        tvAuthorName = findViewById(R.id.tvAuthorName);
        drawerLayout = findViewById(R.id.drawerLayout);
        btnOpenTimeline = findViewById(R.id.btnOpenTimeline);
        recyclerTimeline = findViewById(R.id.recyclerTimeline);
    }

    private void setupTimeline() {
        timelineAdapter = new TimelineAdapter(route);
        recyclerTimeline.setLayoutManager(new LinearLayoutManager(this));
        recyclerTimeline.setAdapter(timelineAdapter);
    }

    private void loadSpot(int index) {
        Spot spot = route.get(index);

        timelineAdapter.setCurrentStep(index);
        recyclerTimeline.scrollToPosition(index);

        tvStepCounter.setText("Step " + (index + 1) + " of " + route.size());
        tvSpotName.setText(spot.getName());
        tvCategoryBadge.setText(String.valueOf(spot.getCategoryType()));

        int defaultImg = R.drawable.img_culture;
        if (spot.getCategoryType() != null) {
            switch (spot.getCategoryType()) {
                case FOOD: defaultImg = R.drawable.img_food; break;
                case LEISURE: defaultImg = R.drawable.img_culture; break;
            }
        }

        if (spot.getExternalImageUrl() != null && !spot.getExternalImageUrl().isEmpty()) {
            Glide.with(this).load(spot.getExternalImageUrl()).placeholder(defaultImg).error(defaultImg).centerCrop().into(imgSpotMain);
        } else {
            Glide.with(this).load(defaultImg).centerCrop().into(imgSpotMain);
        }

        if (index == route.size() - 1) {
            btnNextStep.setText("Finish");
        } else {
            btnNextStep.setText("Next Step");
        }

        tvSpotDetails.setText("Category: " + spot.getCategoryType() + "\nPrice: " + spot.getPrice() + "€\nDuration: " + spot.getDuration() + "h");

        if (spot.getOpeningHours() != null && !spot.getOpeningHours().isEmpty()) {
            tvOpeningHours.setVisibility(View.VISIBLE);
            tvOpeningHours.setText("OPENING HOURS :\n" + spot.getOpeningHours().replace(";", "\n"));
        } else {
            tvOpeningHours.setVisibility(View.GONE);
        }

        if (spot.getExternalAuthorName() != null && !spot.getExternalAuthorName().isEmpty()) {
            tvAuthorName.setVisibility(View.VISIBLE);
            tvAuthorName.setText("Photo by: " + spot.getExternalAuthorName());
        } else {
            tvAuthorName.setVisibility(View.GONE);
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
        String geoUri = "geo:" + spot.getLatitude() + "," + spot.getLongitude() + "?q=" + spot.getLatitude() + "," + spot.getLongitude() + "(" + Uri.encode(spot.getName()) + ")";
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(geoUri));
        try { startActivity(mapIntent); } catch (Exception e) { Toast.makeText(this, "Pas d'app de carte", Toast.LENGTH_SHORT).show(); }
    }
}
