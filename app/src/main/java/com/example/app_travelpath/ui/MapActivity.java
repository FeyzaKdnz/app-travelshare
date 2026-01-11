package com.example.app_travelpath.ui;

import android.os.Bundle;
import android.preference.PreferenceManager;

import androidx.appcompat.app.AppCompatActivity;

import com.example.app_travelpath.R;
import com.example.app_travelpath.model.Spot;

import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import org.osmdroid.views.overlay.Polyline;

import java.util.ArrayList;

public class MapActivity extends AppCompatActivity {

    private MapView map = null;

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
            GeoPoint startPoint = new GeoPoint(firstSpot.getLatitude(), firstSpot.getLongitude());
            map.getController().setCenter(startPoint);
            Polyline line = new Polyline();
            line.setWidth(15f);
            line.setColor(android.graphics.Color.parseColor("#6200EE"));
            ArrayList<GeoPoint> routePoints = new ArrayList<>();

            for (int i = 0; i < spots.size(); i++) {
                Spot spot = spots.get(i);
                GeoPoint point = new GeoPoint(spot.getLatitude(), spot.getLongitude());
                routePoints.add(point);
                Marker marker = new Marker(map);
                marker.setPosition(point);
                marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                marker.setTitle((i + 1) + ". " + spot.getName());
                marker.setSubDescription(spot.getCategoryType() + " - " + spot.getPrice() + "€");
                map.getOverlays().add(marker);
            }

            line.setPoints(routePoints);
            map.getOverlays().add(0, line);
            map.invalidate();

        }
    }

    private void addMarker(Spot spot) {
        Marker marker = new Marker(map);
        marker.setPosition(new GeoPoint(spot.getLatitude(), spot.getLongitude()));
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        marker.setTitle(spot.getName());
        map.getOverlays().add(marker);
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