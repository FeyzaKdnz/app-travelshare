package com.example.app_travelpath.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.app_travelpath.R;
import com.example.app_travelpath.data.local.AppDatabase;
import com.example.app_travelpath.model.User;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class RegisterActivity extends AppCompatActivity {

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
    // Note: On n'affiche pas tvCityLocation sur cette vue d'après le Figma, mais on le garde pour la cohérence
    private TextView tvCityLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_activity);

        imgBackground = findViewById(R.id.imgBackground);
        tvCityLocation = findViewById(R.id.tvCityLocation); // Sera null si pas dans le XML, ce qui est géré
        EditText etUsername = findViewById(R.id.etUsername);
        EditText etEmail = findViewById(R.id.etEmail);
        EditText etPassword = findViewById(R.id.etPassword);
        EditText etConfirmPassword = findViewById(R.id.etConfirmPassword);
        MaterialButton btnRegister = findViewById(R.id.btnRegister);
        TextView tvLoginNow = findViewById(R.id.tvLoginNow);
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

        tvLoginNow.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        btnRegister.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String confirmPassword = etConfirmPassword.getText().toString().trim();

            if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!password.equals(confirmPassword)) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            User newUser = new User(username, email, password);
            Executors.newSingleThreadExecutor().execute(() -> {
                try {
                    AppDatabase.getDatabase(getApplicationContext()).userDao().registerUser(newUser);
                    runOnUiThread(() -> {
                        Toast.makeText(this, "Account created successfully!", Toast.LENGTH_SHORT).show();
                        finish(); 
                    });
                } catch (Exception e) {
                    runOnUiThread(() -> Toast.makeText(this, "Username or Email already exists", Toast.LENGTH_SHORT).show());
                }
            });
        });
    }

    private void initBackgroundOptions() {
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
            if (tvCityLocation != null) tvCityLocation.setText(current.cityName);
            imgBackground.animate().alpha(1f).setDuration(500);
            currentIndex = (currentIndex + 1) % options.size();
        });
    }
}
