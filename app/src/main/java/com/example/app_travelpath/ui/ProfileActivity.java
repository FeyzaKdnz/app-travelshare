package com.example.app_travelpath.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.app_travelpath.R;
import com.example.app_travelpath.data.local.AppDatabase;
import com.example.app_travelpath.model.SavedJourney;
import com.example.app_travelpath.model.User;

import java.util.List;
import java.util.concurrent.Executors;

public class ProfileActivity extends AppCompatActivity {

    private TextView tvUsername, tvEmail;
    private Button btnLikedTrips, btnParameters, btnLogout;
    private ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        tvUsername = findViewById(R.id.tvUsername);
        tvEmail = findViewById(R.id.tvEmail);
        btnLikedTrips = findViewById(R.id.btnLikedTrips);
        btnParameters = findViewById(R.id.btnParameters);
        btnLogout = findViewById(R.id.btnLogout);
        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        loadUserProfile();

        btnLogout.setOnClickListener(v -> handleLogout());
        btnParameters.setOnClickListener(v -> {
            Toast.makeText(this, "Parameters coming soon...", Toast.LENGTH_SHORT).show();
        });
    }

    private void loadUserProfile() {
        SharedPreferences prefs = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String username = prefs.getString("logged_in_username", "Guest");

        tvUsername.setText(username);
        Executors.newSingleThreadExecutor().execute(() -> {
            User user = AppDatabase.getDatabase(this).userDao().checkUsername(username);
            List<SavedJourney> userJourneys = AppDatabase.getDatabase(this).savedJourneyDao().getJourneysByUser(username);
            int totalLikes = 0;
            for (SavedJourney j : userJourneys) {
                totalLikes += j.likesCount;
            }

            final int finalLikes = totalLikes;
            runOnUiThread(() -> {
                if (user != null) {
                    tvEmail.setText(user.getEmail());
                }
                btnLikedTrips.setText("Liked trips : " + finalLikes);
            });
        });
    }

    private void handleLogout() {
        SharedPreferences prefs = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        prefs.edit().clear().apply();
        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(ProfileActivity.this, WelcomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
