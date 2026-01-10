package com.example.app_travelpath.data.repository;

import android.app.Application;
import android.util.Log;

import com.example.app_travelpath.data.local.AppDatabase;
import com.example.app_travelpath.data.local.SpotDao;
import com.example.app_travelpath.data.network.NominatimResult;
import com.example.app_travelpath.data.network.OsmApiService;
import com.example.app_travelpath.data.network.OsmElement;
import com.example.app_travelpath.data.network.OsmResponse;
import com.example.app_travelpath.data.network.RetrofitClient;
import com.example.app_travelpath.model.CategoryType;
import com.example.app_travelpath.model.EffortLevel;
import com.example.app_travelpath.model.Spot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SpotRepository {

    private SpotDao spotDao;
    private OsmApiService apiService;
    private ExecutorService executor;

    public SpotRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        spotDao = db.spotDao();
        apiService = RetrofitClient.getService();
        executor = Executors.newSingleThreadExecutor();
    }

    public CompletableFuture<List<Spot>> getSpotsByCity(String city) {
        CompletableFuture<List<Spot>> future = new CompletableFuture<>();

        executor.execute(() -> {
            // On vérifie le cache local
            List<Spot> localSpots = spotDao.getSpotsByCity(city);

            if (!localSpots.isEmpty()) {
                Log.d("REPO", "Données trouvées en local pour : " + city);
                future.complete(localSpots);
            } else {
                Log.d("REPO", "Rien en local, appel API pour : " + city);
                fetchFromNetwork(city, future);
            }
        });

        return future;
    }

    private void fetchFromNetwork(String city, CompletableFuture<List<Spot>> future) {
        // 1. Géocodage (Nominatim)
        String geocodeUrl = "https://nominatim.openstreetmap.org/search";

        apiService.searchCity(geocodeUrl, city, "json", 5).enqueue(new Callback<List<NominatimResult>>() {
            @Override
            public void onResponse(Call<List<NominatimResult>> call, Response<List<NominatimResult>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {

                    // Trouver le meilleur résultat
                    NominatimResult bestMatch = response.body().get(0);
                    for (NominatimResult result : response.body()) {
                        if (result.importance > bestMatch.importance) bestMatch = result;
                    }

                    double lat = Double.parseDouble(bestMatch.lat);
                    double lon = Double.parseDouble(bestMatch.lon);
                    Log.d("REPO", "Ville trouvée : " + bestMatch.display_name);

                    // Lancer la recherche optimisée
                    fetchSpotsAroundOptimized(city, lat, lon, future);

                } else {
                    Log.e("REPO", "Ville introuvable.");
                    future.complete(new ArrayList<>());
                }
            }

            @Override
            public void onFailure(Call<List<NominatimResult>> call, Throwable t) {
                future.completeExceptionally(t);
            }
        });
    }

    private void fetchSpotsAroundOptimized(String city, double lat, double lon, CompletableFuture<List<Spot>> future) {

        double offset = 0.035;

        double south = lat - offset;
        double west = lon - offset;
        double north = lat + offset;
        double east = lon + offset;

        String bbox = "(" + south + "," + west + "," + north + "," + east + ")";
        String query = "[out:json][timeout:200];" +
                "(" +
                // CULTURE
                "  node" + bbox + "[\"tourism\"=\"museum\"];" +
                "  node" + bbox + "[\"tourism\"=\"attraction\"];" +
                "  node" + bbox + "[\"historic\"=\"monument\"];" +
                "  node" + bbox + "[\"historic\"=\"castle\"];" +

                // FOOD
                "  node" + bbox + "[\"amenity\"=\"restaurant\"];" +
                "  node" + bbox + "[\"amenity\"=\"cafe\"];" +

                // LEISURE
                "  node" + bbox + "[\"leisure\"=\"park\"];" +
                "  way"  + bbox + "[\"leisure\"=\"park\"];" +
                "  node" + bbox + "[\"leisure\"=\"garden\"];" +
                "  way"  + bbox + "[\"leisure\"=\"garden\"];" +
                ");" +
                "out center;";

        String baseUrl = "https://overpass-api.de/api/interpreter";

        Log.d("REPO", "Envoi requête Overpass (Optimisée Light)...");

        apiService.getPlacesSecure(baseUrl, query).enqueue(new Callback<OsmResponse>() {
            @Override
            public void onResponse(Call<OsmResponse> call, Response<OsmResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Spot> newSpots = new ArrayList<>();
                    if (response.body().elements != null) {
                        for (OsmElement element : response.body().elements) {
                            Spot spot = mapElementToSpot(city, element);
                            if (spot != null) newSpots.add(spot);
                        }
                    }
                    Log.d("REPO", "Reçu " + newSpots.size() + " lieux.");

                    executor.execute(() -> {
                        spotDao.clearCity(city);
                        spotDao.insertAll(newSpots);
                        future.complete(newSpots);
                    });
                } else {
                    Log.e("REPO", "Erreur Overpass: " + response.code());
                    future.complete(new ArrayList<>());
                }
            }

            @Override
            public void onFailure(Call<OsmResponse> call, Throwable t) {
                Log.e("REPO", "Timeout ou Erreur Réseau", t);
                future.completeExceptionally(t);
            }
        });
    }

    private Spot mapElementToSpot(String city, OsmElement element) {
        Map<String, String> tags = element.tags;
        if (tags == null || !tags.containsKey("name")) return null;

        String name = tags.get("name");

        // Gestion coordonnée (Node simple ou Way centré)
        double lat = element.lat;
        double lon = element.lon;
        if (element.center != null) {
            lat = element.center.lat;
            lon = element.center.lon;
        }

        CategoryType category = CategoryType.DISCOVERY;
        double price = 0.0;
        EffortLevel effort = EffortLevel.LOW;
        double duration = 1.0;

        String tourism = tags.get("tourism");
        String amenity = tags.get("amenity");
        String leisure = tags.get("leisure");
        String historic = tags.get("historic");
        String landuse = tags.get("landuse");

        // 1. LEISURE
        if ("park".equals(leisure) || "garden".equals(leisure) || "grass".equals(landuse)) {
            category = CategoryType.LEISURE;
            price = 0.0;
            effort = EffortLevel.LOW;
            duration = 1.0;
        }
        // 2. CULTURE
        else if ("museum".equals(tourism) || "attraction".equals(tourism) || historic != null) {
            category = CategoryType.CULTURE;
            price = 10.0 + (Math.random() * 15);
            effort = EffortLevel.MEDIUM;
            duration = 2.0;
        }
        // 3. FOOD
        else if ("restaurant".equals(amenity) || "cafe".equals(amenity) || "bar".equals(amenity)) {
            category = CategoryType.FOOD;
            price = 15.0 + (Math.random() * 25);
            effort = EffortLevel.VERY_LOW;
            duration = 1.5;
        }

        price = Math.round(price * 10.0) / 10.0;

        return new Spot(city, name, lat, lon, category, price, effort, duration, null);
    }
}