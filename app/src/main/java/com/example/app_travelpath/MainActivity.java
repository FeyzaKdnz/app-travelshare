package com.example.app_travelpath;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.app_travelpath.ui.CreateJourneyActivity;
import com.example.app_travelpath.ui.SavedJourneysActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    // Classe interne pour les données
    private static class BackgroundOption {
        int imageResId;
        String cityName;

        public BackgroundOption(int imageResId, String cityName) {
            this.imageResId = imageResId;
            this.cityName = cityName;
        }
    }

    // --- VARIABLES POUR LE CARROUSEL ---
    private List<BackgroundOption> options = new ArrayList<>();
    private int currentIndex = 0; // Pour savoir quelle image on affiche
    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable carouselRunnable; // La tâche qui se répète

    // Composants UI
    private ImageView imgBackground;
    private TextView tvCityLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 1. Initialisation des Vues
        imgBackground = findViewById(R.id.imgBackground);
        tvCityLocation = findViewById(R.id.tvCityLocation);
        Button btnCreateJourney = findViewById(R.id.btnCreateJourney);
        Button btnViewRoute = findViewById(R.id.btnViewRoute);

        options.add(new BackgroundOption(R.drawable.manarola, "Manarola, Italia"));
        options.add(new BackgroundOption(R.drawable.istanbul3, "Istanbul, Türkiye"));
        options.add(new BackgroundOption(R.drawable.colmar, "Colmar, France"));
        options.add(new BackgroundOption(R.drawable.shinjuku, "Shinjuku, Japan"));
        options.add(new BackgroundOption(R.drawable.edinburg, "Edinburgh, Scotland"));
        options.add(new BackgroundOption(R.drawable.georgia, "Tbilisi, Georgia"));
        options.add(new BackgroundOption(R.drawable.venise, "Venice, Italia"));
        options.add(new BackgroundOption(R.drawable.capadocce, "Cappadocia, Türkiye"));
        options.add(new BackgroundOption(R.drawable.paris, "Paris, France"));
        options.add(new BackgroundOption(R.drawable.iran, "Shiraz, Iran"));
        options.add(new BackgroundOption(R.drawable.grece, "Mykonos, Greece"));
        options.add(new BackgroundOption(R.drawable.istanbul2, "Istanbul, Türkiye"));
        options.add(new BackgroundOption(R.drawable.kirgiz, "Kara-Köl, Kyrgyzstan"));
        options.add(new BackgroundOption(R.drawable.roma, "Roma, Italia"));


        // 3. Définition de la tâche du Carrousel
        carouselRunnable = new Runnable() {
            @Override
            public void run() {
                changeBackgroundWithAnimation();
                // On relance cette même tâche dans 5 secondes (5000 ms)
                handler.postDelayed(this, 5000);
            }
        };

        // Navigation
        btnCreateJourney.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CreateJourneyActivity.class);
            intent.putExtra("city_name", "");
            startActivity(intent);
        });

        btnViewRoute.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SavedJourneysActivity.class);
            startActivity(intent);
        });
    }

    // --- GESTION DU CYCLE DE VIE (Important !) ---

    @Override
    protected void onResume() {
        super.onResume();
        // Quand l'appli s'ouvre ou revient au premier plan : on lance le carrousel
        // On lance immédiatement la première image
        if (!options.isEmpty()) {
            handler.post(carouselRunnable);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Quand l'utilisateur quitte l'écran : on arrête le carrousel pour économiser la batterie
        handler.removeCallbacks(carouselRunnable);
    }

    // --- LOGIQUE D'ANIMATION ---
    private void changeBackgroundWithAnimation() {
        if (options.isEmpty()) return;

        // 1. Fade OUT (L'image devient transparente en 500ms)
        imgBackground.animate().alpha(0f).setDuration(500).withEndAction(() -> {

            // Une fois invisible, on change l'image et le texte
            BackgroundOption current = options.get(currentIndex);
            imgBackground.setImageResource(current.imageResId);
            tvCityLocation.setText(current.cityName);

            // 2. Fade IN (L'image réapparaît en 500ms)
            imgBackground.animate().alpha(1f).setDuration(500);

            // On prépare l'index pour la prochaine fois (0, 1, 2, 3, 0, 1...)
            currentIndex++;
            if (currentIndex >= options.size()) {
                currentIndex = 0;
            }
        });
    }
}