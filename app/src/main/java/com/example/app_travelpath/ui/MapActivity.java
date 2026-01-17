package com.example.app_travelpath.ui;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.app_travelpath.R;
import com.example.app_travelpath.data.network.OsmApiService;
import com.example.app_travelpath.data.network.OsrmResponse;
import com.example.app_travelpath.data.network.RetrofitClient;
import com.example.app_travelpath.model.Spot;

import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapActivity extends AppCompatActivity {

    private MapView map = null;
    private Polyline roadOverlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Configuration.getInstance().load(getApplicationContext(),
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext()));

        setContentView(R.layout.activity_map);

        map = findViewById(R.id.map);
        map.setMultiTouchControls(true);
        map.getController().setZoom(14.0);

        ArrayList<Spot> spots = (ArrayList<Spot>) getIntent().getSerializableExtra("spots_list");

        if (spots != null && !spots.isEmpty()) {
            Spot firstSpot = spots.get(0);
            map.getController().setCenter(new GeoPoint(firstSpot.getLatitude(), firstSpot.getLongitude()));

            for (int i = 0; i < spots.size(); i++) {
                Spot spot = spots.get(i);
                GeoPoint point = new GeoPoint(spot.getLatitude(), spot.getLongitude());
                Marker marker = new Marker(map);
                marker.setPosition(point);
                marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                marker.setTitle((i + 1) + ". " + spot.getName());
                marker.setSubDescription(spot.getCategoryType() + " - " + spot.getPrice() + "€");
                map.getOverlays().add(marker);
            }

            fetchRealRoad(spots);
        }
    }

    private void fetchRealRoad(List<Spot> spots) {
        if (spots.size() < 2) return;

        StringBuilder coords = new StringBuilder();
        for (int i = 0; i < spots.size(); i++) {
            Spot s = spots.get(i);
            coords.append(s.getLongitude()).append(",").append(s.getLatitude());
            if (i < spots.size() - 1) coords.append(";");
        }

        String url = "https://router.project-osrm.org/route/v1/bike/" + coords.toString() + "?overview=full&geometries=geojson";

        Log.d("MAP", "Requête OSRM : " + url);

        OsmApiService api = RetrofitClient.getService();
        api.getRoute(url).enqueue(new Callback<OsrmResponse>() {
            @Override
            public void onResponse(Call<OsrmResponse> call, Response<OsrmResponse> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().routes.isEmpty()) {

                    List<List<Double>> coordsList = response.body().routes.get(0).geometry.coordinates;
                    ArrayList<GeoPoint> routePoints = new ArrayList<>();
                    
                    for (List<Double> pair : coordsList) {
                        routePoints.add(new GeoPoint(pair.get(1), pair.get(0)));
                    }

                    runOnUiThread(() -> {
                        if (roadOverlay != null) map.getOverlays().remove(roadOverlay);
                        
                        roadOverlay = new Polyline();
                        roadOverlay.setPoints(routePoints);
                        roadOverlay.setWidth(12f);
                        roadOverlay.setColor(android.graphics.Color.parseColor("#6200EE"));
                        
                        map.getOverlays().add(0, roadOverlay);
                        map.invalidate();
                    });
                } else {
                    Log.e("MAP", "Erreur OSRM : " + response.code());
                    drawFallbackLine(spots);
                }
            }

            @Override
            public void onFailure(Call<OsrmResponse> call, Throwable t) {
                Log.e("MAP", "Echec OSRM", t);
                drawFallbackLine(spots);
            }
        });
    }

    private void drawFallbackLine(List<Spot> spots) {
        Polyline line = new Polyline();
        ArrayList<GeoPoint> points = new ArrayList<>();
        for (Spot s : spots) points.add(new GeoPoint(s.getLatitude(), s.getLongitude()));
        line.setPoints(points);
        line.setWidth(10f);
        line.setColor(android.graphics.Color.GRAY);
        map.getOverlays().add(0, line);
        map.invalidate();
    }

    @Override
    public void onResume() {
        super.onResume();
        map.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        map.onPause();
    }
}
