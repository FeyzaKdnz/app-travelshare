package com.example.app_travelpath.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.app_travelpath.R;
import com.example.app_travelpath.data.local.AppDatabase;
import com.example.app_travelpath.model.EffortLevel;
import com.example.app_travelpath.model.SavedJourney;
import com.example.app_travelpath.model.Spot;
import com.example.app_travelpath.domain.JourneyEngine;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;

/* IMPORT POUR L'INTÉGRATION DU TRAVELSHARE */

import com.example.app_travelpath.data.travelshare.TravelShareClient;
import com.example.app_travelpath.data.travelshare.TravelSharePhoto;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/* IMPORT POUR L'EXPORTATION EN PDF */

import android.graphics.pdf.PdfDocument;
import android.graphics.Paint;
import android.graphics.Canvas;
import android.graphics.Color;
import androidx.core.content.FileProvider;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class JourneyResultActivity extends AppCompatActivity {
    private RecyclerView recyclerResult;
    private ImageView imgWeatherIcon;
    private TextView tvWeatherTemp;
    private final String API_KEY = "8e6007439d1df890335ca0104654409d";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journey_result);

        List<Spot> finalRoute = (List<Spot>) getIntent().getSerializableExtra("final_route");
        String cityName = getIntent().getStringExtra("city_name");

        TextView tvTotalCost = findViewById(R.id.tvTotalCost);
        TextView tvTotalDuration = findViewById(R.id.tvTotalDuration);
        recyclerResult = findViewById(R.id.recyclerResult);

        Button btnShowMapRoute = findViewById(R.id.btnShowMapRoute);
        Button btnRegenerate = findViewById(R.id.btnRegenerate);
        Button btnSaveJourney = findViewById(R.id.btnSaveJourney);
        Button btnStartJourney = findViewById(R.id.btnStartJourney);
        ImageButton btnBack = findViewById(R.id.btnBack);
        ImageView imgHeaderBg = findViewById(R.id.imgHeaderBg);
        imgHeaderBg.setImageResource(R.drawable.bag);

        imgWeatherIcon = findViewById(R.id.imgWeatherIcon);
        tvWeatherTemp = findViewById(R.id.tvWeatherTemp);

        if (cityName != null && !cityName.isEmpty()) {
            fetchWeather(cityName);
        } else {
            tvWeatherTemp.setText("N/A");
        }

        btnBack.setOnClickListener(v -> finish());

        updateUI(finalRoute, tvTotalCost, tvTotalDuration, recyclerResult, btnShowMapRoute);

        btnRegenerate.setOnClickListener(v -> {
            regenerateJourney(tvTotalCost, tvTotalDuration, recyclerResult, btnShowMapRoute);
        });

        btnSaveJourney.setOnClickListener(v -> {
            showSaveDialog();
        });

        btnStartJourney.setOnClickListener(v -> {
            Intent intent = new Intent(JourneyResultActivity.this, ActiveJourneyActivity.class);
            intent.putExtra("active_route", (Serializable) currentRouteDisplayed);
            startActivity(intent);
        });

        Button btnExportPdf = findViewById(R.id.btnExportPdf);
        btnExportPdf.setOnClickListener(v -> {
            generateAndSharePdf(currentRouteDisplayed, cityName);
        });

    }

    private List<Spot> currentRouteDisplayed;

    /* --------------------- GÉNÉRATION D'UN NOUVEAU PARCOURS --------------------- */
    private void regenerateJourney(TextView tvCost, TextView tvTime, RecyclerView recycler, Button btnMap) {
        List<Spot> availableSpots = (List<Spot>) getIntent().getSerializableExtra("available_spots");
        if (availableSpots == null || availableSpots.isEmpty()) {
            Toast.makeText(this, "Cannot regenerate (missing data)", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean wantCulture = getIntent().getBooleanExtra("want_culture", true);
        boolean wantFood = getIntent().getBooleanExtra("want_food", true);
        boolean wantLeisure = getIntent().getBooleanExtra("want_leisure", true);
        double minBudget = getIntent().getDoubleExtra("min_budget", 0);
        double maxBudget = getIntent().getDoubleExtra("max_budget", 100);
        float duration = getIntent().getFloatExtra("duration", 4.0f);

        EffortLevel effort = EffortLevel.LOW;
        if (getIntent().hasExtra("effort_level")) {
            effort = (EffortLevel) getIntent().getSerializableExtra("effort_level");
        }

        Collections.shuffle(availableSpots);

        JourneyEngine engine = new JourneyEngine();
        List<Spot> newRoute = engine.generateRoute(
                availableSpots,
                wantCulture, wantFood, wantLeisure,
                minBudget, maxBudget,
                duration,
                effort
        );

        if (newRoute.isEmpty()) {
            Toast.makeText(this, "No other route found.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "New route generated!", Toast.LENGTH_SHORT).show();
            updateUI(newRoute, tvCost, tvTime, recycler, btnMap);
            getIntent().putExtra("final_route", (Serializable) newRoute);
        }
    }

    private void updateUI(List<Spot> route, TextView tvCost, TextView tvTime, RecyclerView recycler, Button btnMap) {
        if (route == null) return;

        this.currentRouteDisplayed = route;

        double totalCost = 0;
        double totalDuration = 0;

        for (Spot s : route) {
            totalCost += s.getPrice();
            totalDuration += s.getDuration();
        }

        tvCost.setText((int)totalCost + "€");
        tvTime.setText(String.format("%.1fh", totalDuration));

        JourneyAdapter adapter = new JourneyAdapter();

        adapter.setOnItemClickListener(spot -> {
            launchNavigation(spot);
        });

        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setAdapter(adapter);
        adapter.setSpots(route);

        btnMap.setOnClickListener(v -> {
            Intent intent = new Intent(JourneyResultActivity.this, MapActivity.class);
            intent.putExtra("spots_list", (Serializable) route);
            startActivity(intent);
        });

        enrichRouteWithPhotos(route);
    }

    private void launchNavigation(Spot spot) {
        String geoUri = "geo:" + spot.getLatitude() + "," + spot.getLongitude() +
                "?q=" + spot.getLatitude() + "," + spot.getLongitude() +
                "(" + Uri.encode(spot.getName()) + ")";

        Intent mapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(geoUri));

        try {
            startActivity(mapIntent);
        } catch (Exception e) {
            Toast.makeText(this, "No map app found.", Toast.LENGTH_SHORT).show();
        }
    }

    /* --------------------- SAUVEGARDE DU PARCOURS --------------------- */
    private void showSaveDialog() {
        if (currentRouteDisplayed == null || currentRouteDisplayed.isEmpty()) {
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Save Trip");
        builder.setMessage("Name your journey to find it later:");

        final EditText input = new EditText(this);
        input.setHint("Ex: Weekend in Paris");
        builder.setView(input);

        builder.setPositiveButton("Save", (dialog, which) -> {
            String journeyName = input.getText().toString();
            if (!journeyName.isEmpty()) {
                saveJourneyToDatabase(journeyName);
            } else {
                Toast.makeText(this, "Name cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    /* --------------------- ENREGISTREMENT DU PARCOURS EN DB --------------------- */
    private void saveJourneyToDatabase(String name) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        String formattedDate = sdf.format(new Date());

        SavedJourney journey = new SavedJourney(
                name,
                formattedDate,
                currentRouteDisplayed
        );

        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                AppDatabase db = AppDatabase.getDatabase(getApplicationContext());
                db.savedJourneyDao().insert(journey);

                runOnUiThread(() ->
                        Toast.makeText(JourneyResultActivity.this, "Trip saved successfully!", Toast.LENGTH_LONG).show()
                );
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() ->
                        Toast.makeText(JourneyResultActivity.this, "Error saving trip.", Toast.LENGTH_SHORT).show()
                );
            }
        });
    }

    /* ----------------------------- INTÉGRATION MÉTÉO : OPENWEATHERMAP ----------------------------- */

    private void fetchWeather(String city) {
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                String urlString = "https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=" + API_KEY + "&units=metric";

                URL url = new URL(urlString);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                if (conn.getResponseCode() == 200) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();

                    JSONObject json = new JSONObject(response.toString());
                    JSONObject main = json.getJSONObject("main");
                    double temp = main.getDouble("temp");

                    String iconCode = json.getJSONArray("weather").getJSONObject(0).getString("icon");
                    String iconUrl = "https://openweathermap.org/img/wn/" + iconCode + "@2x.png";

                    runOnUiThread(() -> {
                        tvWeatherTemp.setText((int)temp + "°C");
                        Glide.with(this)
                                .load(iconUrl)
                                .into(imgWeatherIcon);
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /* ----------------------------- INTÉGRATION TRAVELSHARE ------------------------------ */

    private void enrichRouteWithPhotos(List<Spot> route) {
        if (route == null || route.isEmpty()) return;

        Spot center = route.get(0);

        TravelShareClient.getService().getPhotosAround(center.getLatitude(), center.getLongitude(), 10)
                .enqueue(new Callback<List<TravelSharePhoto>>() {
                    @Override
                    public void onResponse(Call<List<TravelSharePhoto>> call, Response<List<TravelSharePhoto>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            List<TravelSharePhoto> sharedPhotos = response.body();
                            android.util.Log.d("TRAVEL_DEBUG", "Réponse reçue : " + sharedPhotos.size() + " photos disponibles dans la zone.");

                            boolean dataChanged = false;

                            for (Spot mySpot : route) {
                                for (TravelSharePhoto photo : sharedPhotos) {
                                    float[] results = new float[1];
                                    android.location.Location.distanceBetween(
                                            mySpot.getLatitude(), mySpot.getLongitude(),
                                            photo.getLatitude(), photo.getLongitude(),
                                            results
                                    );
                                    float distance = results[0];

                                    if (distance < 500) {
                                        android.util.Log.d("TRAVEL_DEBUG", "Proximité : Photo à " + distance + "m de " + mySpot.getName());
                                    }

                                    if (distance < 200) {
                                        android.util.Log.d("TRAVEL_DEBUG", "MATCH TROUVÉ " + mySpot.getName() + " -> " + photo.getFullImageUrl());
                                        mySpot.setExternalImageUrl(photo.getFullImageUrl());
                                        dataChanged = true;
                                        break;
                                    }
                                }
                            }

                            if (dataChanged) {
                                runOnUiThread(() -> {
                                    if (recyclerResult.getAdapter() != null) {
                                        recyclerResult.getAdapter().notifyDataSetChanged();
                                    }
                                });
                            } else {
                                android.util.Log.d("TRAVEL_DEBUG", "Aucune correspondance trouvée (trop loin)");
                            }

                        } else {
                            android.util.Log.e("TRAVEL_DEBUG", "Erreur API : Code " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<List<TravelSharePhoto>> call, Throwable t) {
                        t.printStackTrace();
                    }
                });
    }

    /* ----------------------------- EXPORTATION EN PDF ----------------------------- */

    private void generateAndSharePdf(List<Spot> route, String cityName) {
        if (route == null || route.isEmpty()) return;

        /* ----- CRÉATION DU DOC PDF ET CONFIGURATION DE LA PAGE ----- */

        PdfDocument pdfDocument = new PdfDocument();
        Paint paint = new Paint();
        Paint titlePaint = new Paint();

        int pageHeight = Math.max(842, 200 + route.size() * 100);
        PdfDocument.PageInfo myPageInfo = new PdfDocument.PageInfo.Builder(595, pageHeight, 1).create();
        PdfDocument.Page myPage = pdfDocument.startPage(myPageInfo);
        Canvas canvas = myPage.getCanvas();

        int x = 40;
        int y = 60;

        titlePaint.setTextSize(24);
        titlePaint.setFakeBoldText(true);
        titlePaint.setColor(Color.BLACK);
        canvas.drawText("TravelPath - Itinerary", x, y, titlePaint);

        y += 40;
        paint.setTextSize(16);
        paint.setColor(Color.DKGRAY);
        canvas.drawText("Destination: " + (cityName != null ? cityName : "Custom Trip"), x, y, paint);

        y += 50;
        paint.setStrokeWidth(1);
        canvas.drawLine(x, y, 550, y, paint);
        y += 40;

        int step = 1;
        double totalCost = 0;

        for (Spot spot : route) {
            titlePaint.setTextSize(14);
            canvas.drawText(step + ". " + spot.getName(), x, y, titlePaint);
            y += 20;

            paint.setTextSize(12);
            paint.setColor(Color.GRAY);
            String details = "   Category: " + spot.getCategoryType() +
                    "  |  Price: " + spot.getPrice() + "€" +
                    "  |  Duration: " + spot.getDuration() + "h";
            canvas.drawText(details, x, y, paint);

            totalCost += spot.getPrice();
            y += 40;
            step++;
        }

        y += 20;
        canvas.drawLine(x, y, 550, y, paint);
        y += 40;
        titlePaint.setTextSize(16);
        canvas.drawText("Total Estimated Cost: " + (int)totalCost + "€", x, y, titlePaint);

        pdfDocument.finishPage(myPage);

        /* ----- SAUVEGARDE DANS LE CACHE ET PARTAGE IMMÉDIAT ----- */

        File file = new File(getCacheDir(), "TravelPath_Itinerary.pdf");
        try {
            pdfDocument.writeTo(new FileOutputStream(file));
            Uri uri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", file);

            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("application/pdf");
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(Intent.createChooser(shareIntent, "Share Itinerary via..."));

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error generating PDF", Toast.LENGTH_SHORT).show();
        } finally {
            pdfDocument.close();
        }
    }

}