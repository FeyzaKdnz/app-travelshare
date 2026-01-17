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
import com.example.app_travelpath.ui.ExploreActivity;
import com.example.app_travelpath.ui.SavedJourneysActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static class BackgroundOption {
        int imageResId;
        String cityName;

        public BackgroundOption(int imageResId, String cityName) {
            this.imageResId = imageResId;
            this.cityName = cityName;
        }
    }

    /* --- VARIABLES POUR LE CARROUSEL --- */

    private List<BackgroundOption> options = new ArrayList<>();
    private int currentIndex = 0;
    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable carouselRunnable;
    private ImageView imgBackground;
    private TextView tvCityLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imgBackground = findViewById(R.id.imgBackground);
        tvCityLocation = findViewById(R.id.tvCityLocation);
        Button btnCreateJourney = findViewById(R.id.btnCreateJourney);
        Button btnViewRoute = findViewById(R.id.btnViewRoute);
        Button btnProfile = findViewById(R.id.btnProfile);

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

        carouselRunnable = new Runnable() {
            @Override
            public void run() {
                changeBackgroundWithAnimation();
                handler.postDelayed(this, 5000);
            }
        };

        btnCreateJourney.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CreateJourneyActivity.class);
            intent.putExtra("city_name", "");
            startActivity(intent);
        });

        btnViewRoute.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SavedJourneysActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.btnExplore).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ExploreActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.btnProfile).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, com.example.app_travelpath.ui.ProfileActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!options.isEmpty()) {
            handler.post(carouselRunnable);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(carouselRunnable);
    }

    private void changeBackgroundWithAnimation() {
        if (options.isEmpty()) return;

        imgBackground.animate().alpha(0f).setDuration(500).withEndAction(() -> {

            BackgroundOption current = options.get(currentIndex);
            imgBackground.setImageResource(current.imageResId);
            tvCityLocation.setText(current.cityName);
            imgBackground.animate().alpha(1f).setDuration(500);
            currentIndex++;
            if (currentIndex >= options.size()) {
                currentIndex = 0;
            }
        });
    }
}