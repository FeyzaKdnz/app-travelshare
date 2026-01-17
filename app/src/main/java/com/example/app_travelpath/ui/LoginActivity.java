package com.example.app_travelpath.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.app_travelpath.MainActivity;
import com.example.app_travelpath.R;
import com.example.app_travelpath.data.local.AppDatabase;
import com.example.app_travelpath.model.User;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class LoginActivity extends AppCompatActivity {

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
        setContentView(R.layout.login_activity);

        imgBackground = findViewById(R.id.imgBackground);
        tvCityLocation = findViewById(R.id.tvCityLocation);
        EditText etEmail = findViewById(R.id.etEmail);
        EditText etPassword = findViewById(R.id.etPassword);
        MaterialButton btnLogin = findViewById(R.id.btnLogin);
        TextView tvRegisterNow = findViewById(R.id.tvRegisterNow);
        LinearLayout btnBackLayout = findViewById(R.id.btnBackLayout);

        initBackgroundOptions();

        carouselRunnable = new Runnable() {
            @Override
            public void run() {
                changeBackgroundWithAnimation();
                handler.postDelayed(this, 5000);
            }
        };

        btnBackLayout.setOnClickListener(v -> finish());

        tvRegisterNow.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter your credentials", Toast.LENGTH_SHORT).show();
                return;
            }

            Executors.newSingleThreadExecutor().execute(() -> {
                User user = AppDatabase.getDatabase(getApplicationContext()).userDao().login(email, password);
                runOnUiThread(() -> {
                    if (user != null) {
                        SharedPreferences prefs = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
                        prefs.edit().putString("logged_in_username", user.getUsername()).apply();
                        Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    } else {
                        Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show();
                    }
                });
            });
        });
    }

    private void initBackgroundOptions() {
        options.add(new BackgroundOption(R.drawable.img_manarola, "Manarola, Italia"));
        options.add(new BackgroundOption(R.drawable.img_istanbul3, "Istanbul, Türkiye"));
        options.add(new BackgroundOption(R.drawable.img_colmar, "Colmar, France"));
        options.add(new BackgroundOption(R.drawable.img_shinjuku, "Shinjuku, Japan"));
        options.add(new BackgroundOption(R.drawable.img_edinburg, "Edinburgh, Scotland"));
        options.add(new BackgroundOption(R.drawable.img_georgia, "Tbilisi, Georgia"));
        options.add(new BackgroundOption(R.drawable.img_venise, "Venice, Italia"));
        options.add(new BackgroundOption(R.drawable.img_capadocce, "Cappadocia, Türkiye"));
        options.add(new BackgroundOption(R.drawable.img_paris, "Paris, France"));
        options.add(new BackgroundOption(R.drawable.img_iran, "Shiraz, Iran"));
        options.add(new BackgroundOption(R.drawable.img_grece, "Mykonos, Greece"));
        options.add(new BackgroundOption(R.drawable.img_istanbul2, "Istanbul, Türkiye"));
        options.add(new BackgroundOption(R.drawable.img_kirgiz, "Kara-Köl, Kyrgyzstan"));
        options.add(new BackgroundOption(R.drawable.img_roma, "Roma, Italia"));
    }

    @Override
    protected void onResume() {
        super.onResume();
        handler.post(carouselRunnable);
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
