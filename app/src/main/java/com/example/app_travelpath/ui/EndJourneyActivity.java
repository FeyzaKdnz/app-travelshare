package com.example.app_travelpath.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.app_travelpath.MainActivity;
import com.example.app_travelpath.R;
import com.example.app_travelpath.data.local.AppDatabase;
import com.example.app_travelpath.model.SavedJourney;
import com.example.app_travelpath.model.Spot;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;

public class EndJourneyActivity extends AppCompatActivity {

    private List<Spot> completedRoute;
    private Button btnSaveJourney, btnHome;
    private TextView tvSavePrompt;
    private boolean isSaved = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end_journey);

        completedRoute = (List<Spot>) getIntent().getSerializableExtra("finished_route");
        btnSaveJourney = findViewById(R.id.btnSaveJourney);
        btnHome = findViewById(R.id.btnHome);
        tvSavePrompt = findViewById(R.id.tvSavePrompt);
        btnSaveJourney.setOnClickListener(v -> {
            if (!isSaved) {
                showSaveDialog();
            } else {
                Toast.makeText(this, "Déjà enregistré !", Toast.LENGTH_SHORT).show();
            }
        });

        btnHome.setOnClickListener(v -> {
            Intent intent = new Intent(EndJourneyActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void showSaveDialog() {
        if (completedRoute == null || completedRoute.isEmpty()) {
            Toast.makeText(this, "Erreur : Pas de données à sauvegarder.", Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Name your memory");
        builder.setMessage("Give a name to this finished journey:");

        final EditText input = new EditText(this);
        input.setHint("Ex: My Amazing Trip");
        builder.setView(input);

        builder.setPositiveButton("Save", (dialog, which) -> {
            String name = input.getText().toString();
            if (!name.isEmpty()) {
                saveJourneyToDB(name);
            } else {
                Toast.makeText(this, "Name required", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void saveJourneyToDB(String name) {
        // --- CORRECTION : Récupération de l'utilisateur connecté ---
        SharedPreferences prefs = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String username = prefs.getString("logged_in_username", "Unknown");

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        String date = sdf.format(new Date());

        // On passe maintenant 4 arguments au constructeur, dont le username
        SavedJourney journey = new SavedJourney(name, date, username, completedRoute);

        Executors.newSingleThreadExecutor().execute(() -> {
            AppDatabase.getDatabase(this).savedJourneyDao().insert(journey);

            runOnUiThread(() -> {
                isSaved = true;
                btnSaveJourney.setText("Trip Saved ✓");
                btnSaveJourney.setEnabled(false);
                btnSaveJourney.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
                tvSavePrompt.setText("Memory safely stored.");

                Toast.makeText(this, "Saved successfully!", Toast.LENGTH_SHORT).show();
            });
        });
    }
}
