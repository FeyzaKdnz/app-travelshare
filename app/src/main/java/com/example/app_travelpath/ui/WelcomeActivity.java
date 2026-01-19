package com.example.app_travelpath.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.app_travelpath.R;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class WelcomeActivity extends AppCompatActivity {

    private static class BackgroundOption {
        int imageResId;
        String cityName;
        public BackgroundOption(int imageResId, String cityName) {
            this.imageResId = imageResId;
            this.cityName = cityName;
        }
    }

    private List<BackgroundOption> options = new ArrayList<>();
    private int currentIndex = 0;
    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable carouselRunnable;
    private ImageView imgBackground;
    private TextView tvCityLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_activity);

        imgBackground = findViewById(R.id.imgBackground);
        tvCityLocation = findViewById(R.id.tvCityLocation);
        MaterialButton btnLogin = findViewById(R.id.btnLogin);
        MaterialButton btnRegister = findViewById(R.id.btnRegister);

        initBackgroundOptions();

        carouselRunnable = new Runnable() {
            @Override
            public void run() {
                changeBackgroundWithAnimation();
                handler.postDelayed(this, 5000);
            }
        };

        btnLogin.setOnClickListener(v -> {
            Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
            startActivity(intent);
        });

        btnRegister.setOnClickListener(v -> {
            Intent intent = new Intent(WelcomeActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    private void initBackgroundOptions() {
        options.add(new BackgroundOption(R.drawable.img_manarola, "Manarola, Italia"));
        options.add(new BackgroundOption(R.drawable.img_istanbul2, "Istanbul, Türkiye"));
        options.add(new BackgroundOption(R.drawable.img_colmar, "Colmar, France"));
        options.add(new BackgroundOption(R.drawable.img_shinjuku, "Shinjuku, Japan"));
        options.add(new BackgroundOption(R.drawable.img_edinburg, "Edinburgh, Scotland"));
        options.add(new BackgroundOption(R.drawable.img_georgia, "Tbilisi, Georgia"));
        options.add(new BackgroundOption(R.drawable.img_venise, "Venice, Italia"));
        options.add(new BackgroundOption(R.drawable.img_capadocce, "Cappadocia, Türkiye"));
        options.add(new BackgroundOption(R.drawable.img_paris, "Paris, France"));
        options.add(new BackgroundOption(R.drawable.img_iran, "Shiraz, Iran"));
        options.add(new BackgroundOption(R.drawable.img_grece, "Mykonos, Greece"));
        options.add(new BackgroundOption(R.drawable.img_istanbul3, "Istanbul, Türkiye"));
        options.add(new BackgroundOption(R.drawable.img_kirgiz, "Kara-Köl, Kyrgyzstan"));
        options.add(new BackgroundOption(R.drawable.img_roma, "Roma, Italia"));
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
            currentIndex = (currentIndex + 1) % options.size();
        });
    }
}
