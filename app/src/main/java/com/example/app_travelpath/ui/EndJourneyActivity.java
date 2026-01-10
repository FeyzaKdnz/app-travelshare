package com.example.app_travelpath.ui;

import android.content.Intent;
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

        // 1. Récupérer les données du voyage fini
        completedRoute = (List<Spot>) getIntent().getSerializableExtra("finished_route");

        // 2. Init UI
        btnSaveJourney = findViewById(R.id.btnSaveJourney);
        btnHome = findViewById(R.id.btnHome);
        tvSavePrompt = findViewById(R.id.tvSavePrompt);

        // 3. Logique Bouton Save
        btnSaveJourney.setOnClickListener(v -> {
            if (!isSaved) {
                showSaveDialog();
            } else {
                Toast.makeText(this, "Déjà enregistré !", Toast.LENGTH_SHORT).show();
            }
        });

        // 4. Logique Bouton Home
        btnHome.setOnClickListener(v -> {
            Intent intent = new Intent(EndJourneyActivity.this, MainActivity.class);
            // On vide toute la pile d'activités pour revenir "proprement" à l'accueil
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
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        String date = sdf.format(new Date());

        SavedJourney journey = new SavedJourney(name, date, completedRoute);

        Executors.newSingleThreadExecutor().execute(() -> {
            AppDatabase.getDatabase(this).savedJourneyDao().insert(journey);

            runOnUiThread(() -> {
                isSaved = true;
                // On change l'apparence du bouton pour dire "C'est fait"
                btnSaveJourney.setText("Trip Saved ✓");
                btnSaveJourney.setEnabled(false); // On désactive le clic
                btnSaveJourney.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
                tvSavePrompt.setText("Memory safely stored.");

                Toast.makeText(this, "Saved successfully!", Toast.LENGTH_SHORT).show();
            });
        });
    }
}