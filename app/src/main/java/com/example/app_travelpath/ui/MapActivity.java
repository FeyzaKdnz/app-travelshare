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

        // 1. Configuration indispensable d'OSMdroid (User Agent)
        Configuration.getInstance().load(getApplicationContext(),
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext()));

        setContentView(R.layout.activity_map);

        // 2. Initialisation de la carte
        map = findViewById(R.id.map);
        map.setMultiTouchControls(true); // Autorise le zoom avec deux doigts
        map.getController().setZoom(14.0); // Niveau de zoom initial

        // 3. Récupération des données
        ArrayList<Spot> spots = (ArrayList<Spot>) getIntent().getSerializableExtra("spots_list");

        if (spots != null && !spots.isEmpty()) {
            // A. Centrer la carte
            Spot firstSpot = spots.get(0);
            GeoPoint startPoint = new GeoPoint(firstSpot.getLatitude(), firstSpot.getLongitude());
            map.getController().setCenter(startPoint);

            // B. Préparer la ligne (Le tracé)
            Polyline line = new Polyline();
            line.setWidth(15f); // Épaisseur de la ligne
            line.setColor(android.graphics.Color.parseColor("#6200EE"));

            // Liste des points géographiques pour la ligne
            ArrayList<GeoPoint> routePoints = new ArrayList<>();

            // 4. Boucle pour ajouter Marqueurs + Points de la ligne
            for (int i = 0; i < spots.size(); i++) {
                Spot spot = spots.get(i);
                GeoPoint point = new GeoPoint(spot.getLatitude(), spot.getLongitude());

                // Ajout au tracé
                routePoints.add(point);

                // Ajout du marqueur visuel
                Marker marker = new Marker(map);
                marker.setPosition(point);
                marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);

                // --- MODIFICATION ICI : Numérotation des étapes ---
                // On utilise (i + 1) pour commencer à "1" au lieu de "0"
                marker.setTitle((i + 1) + ". " + spot.getName());
                
                // Description détaillée
                marker.setSubDescription(spot.getCategoryType() + " - " + spot.getPrice() + "€");

                map.getOverlays().add(marker);
            }

            // C. Ajouter la ligne à la carte
            line.setPoints(routePoints);
            // On ajoute la ligne à l'index 0 pour qu'elle soit DESSOUS les marqueurs
            map.getOverlays().add(0, line);

            // D. Rafraîchir la carte
            map.invalidate();
        }
    }

    // Méthode utilitaire (gardée au cas où, mais plus utilisée directement dans le onCreate)
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
        map.onResume(); // Nécessaire pour la gestion de la batterie/ressources
    }

    @Override
    public void onPause() {
        super.onPause();
        map.onPause();
    }
}