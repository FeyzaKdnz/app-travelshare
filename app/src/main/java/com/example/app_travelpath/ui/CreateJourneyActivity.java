package com.example.app_travelpath.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.app_travelpath.R;
import com.example.app_travelpath.data.repository.SpotRepository;
import com.example.app_travelpath.model.EffortLevel;
import com.example.app_travelpath.model.Spot;
import com.google.android.material.chip.Chip;
import com.google.android.material.slider.RangeSlider;
import com.google.android.material.slider.Slider;
import com.google.android.material.textfield.TextInputEditText;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

public class CreateJourneyActivity extends AppCompatActivity {

    private SpotRepository spotRepository;
    private TextInputEditText etCityInput;
    private TextView tvDurationValue, tvSummary, tvBudgetRange;
    private Slider sliderDuration;
    private RangeSlider sliderBudget;
    private Chip chipFood, chipCulture, chipLeisure;
    private LinearLayout cardEffortLow, cardEffortMedium, cardEffortHigh;
    private EffortLevel selectedEffort = EffortLevel.LOW;

    private ImageButton btnBack;
    private ProgressBar loadingIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_journey);

        spotRepository = new SpotRepository(getApplication());

        etCityInput = findViewById(R.id.etCityInput);
        loadingIndicator = findViewById(R.id.loadingIndicator);
        btnBack = findViewById(R.id.btnBack);

        chipFood = findViewById(R.id.chipFood);
        chipCulture = findViewById(R.id.chipCulture);
        chipLeisure = findViewById(R.id.chipLeisure);

        sliderDuration = findViewById(R.id.sliderDuration);
        tvDurationValue = findViewById(R.id.tvDurationValue);
        sliderBudget = findViewById(R.id.sliderBudget);
        tvBudgetRange = findViewById(R.id.tvBudgetRange);

        cardEffortLow = findViewById(R.id.cardEffortLow);
        cardEffortMedium = findViewById(R.id.cardEffortMedium);
        cardEffortHigh = findViewById(R.id.cardEffortHigh);

        tvSummary = findViewById(R.id.tvSummary);
        Button btnGenerate = findViewById(R.id.btnGenerate);

        String intentCity = getIntent().getStringExtra("city_name");
        if (intentCity != null && !intentCity.isEmpty()) {
            etCityInput.setText(intentCity);
        }

        sliderBudget.setValues(0.0f, 100.0f);
        btnBack.setOnClickListener(v -> finish());

        etCityInput.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { updateSummary(); }
            @Override public void afterTextChanged(Editable s) {}
        });

        sliderDuration.addOnChangeListener((slider, value, fromUser) -> {
            tvDurationValue.setText(value + "h");
            updateSummary();
        });

        sliderBudget.addOnChangeListener((slider, value, fromUser) -> {
            List<Float> values = slider.getValues();
            tvBudgetRange.setText((int)values.get(0).floatValue() + "€ - " + (int)values.get(1).floatValue() + "€");
            updateSummary();
        });

        setupEffortCard(cardEffortLow, EffortLevel.LOW);
        setupEffortCard(cardEffortMedium, EffortLevel.MEDIUM);
        setupEffortCard(cardEffortHigh, EffortLevel.HIGH);
        updateEffortVisuals();

        updateSummary();
        btnGenerate.setOnClickListener(v -> {
            String cityInput = etCityInput.getText().toString().trim();

            if (cityInput.isEmpty()) {
                etCityInput.setError("Required");
                return;
            }

            loadingIndicator.setVisibility(View.VISIBLE);
            btnGenerate.setEnabled(false);
            btnGenerate.setText("Searching...");

            spotRepository.getSpotsByCity(cityInput).thenAccept(spots -> {
                runOnUiThread(() -> {
                    loadingIndicator.setVisibility(View.GONE);
                    btnGenerate.setEnabled(true);
                    btnGenerate.setText("Create Journey");

                    if (spots == null || spots.isEmpty()) {
                        Toast.makeText(this, "No spots found in " + cityInput, Toast.LENGTH_LONG).show();
                        return;
                    }

                    boolean wantFood = chipFood.isChecked();
                    boolean wantCulture = chipCulture.isChecked();
                    boolean wantLeisure = chipLeisure.isChecked();

                    if (!wantCulture && !wantFood && !wantLeisure) {
                        Toast.makeText(this, "Select at least one activity", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    List<Float> budgetValues = sliderBudget.getValues();
                    double minB = budgetValues.get(0);
                    double maxB = budgetValues.get(1);
                    float duration = sliderDuration.getValue();

                    com.example.app_travelpath.domain.JourneyEngine engine = new com.example.app_travelpath.domain.JourneyEngine();
                    List<Spot> results = engine.generateRoute(
                            spots, wantCulture, wantFood, wantLeisure,
                            minB, maxB, duration, selectedEffort
                    );

                    if (results.isEmpty()) {
                        Toast.makeText(this, "No route possible with these criteria.", Toast.LENGTH_LONG).show();
                    } else {
                        Intent intent = new Intent(CreateJourneyActivity.this, JourneyResultActivity.class);
                        intent.putExtra("final_route", (Serializable) results);

                        List<Spot> safeAvailableSpots = spots;
                        if (spots.size() > 300) {
                            Collections.shuffle(spots);
                            safeAvailableSpots = new java.util.ArrayList<>(spots.subList(0, 300));
                        }
                        intent.putExtra("available_spots", (Serializable) safeAvailableSpots);
                        intent.putExtra("city_name", cityInput);
                        intent.putExtra("want_culture", wantCulture);
                        intent.putExtra("want_food", wantFood);
                        intent.putExtra("want_leisure", wantLeisure);
                        intent.putExtra("min_budget", minB);
                        intent.putExtra("max_budget", maxB);
                        intent.putExtra("duration", duration);
                        intent.putExtra("effort_level", selectedEffort);

                        startActivity(intent);
                    }
                });
            }).exceptionally(ex -> {
                runOnUiThread(() -> {
                    loadingIndicator.setVisibility(View.GONE);
                    btnGenerate.setEnabled(true);
                    btnGenerate.setText("Create Journey");
                    Toast.makeText(this, "Error: " + ex.getMessage(), Toast.LENGTH_LONG).show();
                });
                return null;
            });
        });
    }

    private void setupEffortCard(View card, EffortLevel level) {
        card.setOnClickListener(v -> {
            selectedEffort = level;
            updateEffortVisuals();
            updateSummary();
        });
    }

    private void updateEffortVisuals() {
        cardEffortLow.setSelected(selectedEffort == EffortLevel.LOW);
        cardEffortMedium.setSelected(selectedEffort == EffortLevel.MEDIUM);
        cardEffortHigh.setSelected(selectedEffort == EffortLevel.HIGH);
    }

    private void updateSummary() {
        List<Float> budgetValues = sliderBudget.getValues();
        String currentCity = (etCityInput != null && etCityInput.getText() != null)
                ? etCityInput.getText().toString().trim()
                : "";
        if(currentCity.isEmpty()) currentCity = "your destination";

        tvSummary.setText("Summary: " + sliderDuration.getValue() + "h in " + currentCity + " (" + selectedEffort.toString() + ")");
    }
}