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
    private Button btnSaveJourney, btnHome, btnShareJourney;
    private TextView tvSavePrompt;
    private boolean isSaved = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end_journey);

        completedRoute = (List<Spot>) getIntent().getSerializableExtra("finished_route");
        btnSaveJourney = findViewById(R.id.btnSaveJourney);
        btnShareJourney = findViewById(R.id.btnShareJourney);
        btnHome = findViewById(R.id.btnHome);
        tvSavePrompt = findViewById(R.id.tvSavePrompt);

        btnSaveJourney.setOnClickListener(v -> {
            if (!isSaved) {
                showSaveDialog(false);
            } else {
                Toast.makeText(this, "Already saved!", Toast.LENGTH_SHORT).show();
            }
        });

        btnShareJourney.setOnClickListener(v -> {
            if (!isSaved) {
                showSaveDialog(true);
            } else {
                Toast.makeText(this, "Already shared!", Toast.LENGTH_SHORT).show();
            }
        });

        btnHome.setOnClickListener(v -> {
            Intent intent = new Intent(EndJourneyActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void showSaveDialog(boolean shareAfterSave) {
        if (completedRoute == null || completedRoute.isEmpty()) {
            Toast.makeText(this, "Error: No data to save.", Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(shareAfterSave ? "Share your memory" : "Name your memory");
        builder.setMessage("Give a name to this finished journey:");

        final EditText input = new EditText(this);
        input.setHint("Ex: My Amazing Trip");
        builder.setView(input);

        builder.setPositiveButton(shareAfterSave ? "Share" : "Save", (dialog, which) -> {
            String name = input.getText().toString();
            if (!name.isEmpty()) {
                saveJourneyToDB(name, shareAfterSave);
            } else {
                Toast.makeText(this, "Name required", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void saveJourneyToDB(String name, boolean shareAfterSave) {
        SharedPreferences prefs = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String username = prefs.getString("logged_in_username", "Unknown");

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        String date = sdf.format(new Date());
        
        SavedJourney journey = new SavedJourney(name, date, username, completedRoute);
        if (shareAfterSave) {
            journey.isShared = true;
        }

        Executors.newSingleThreadExecutor().execute(() -> {
            AppDatabase.getDatabase(this).savedJourneyDao().insert(journey);

            runOnUiThread(() -> {
                isSaved = true;
                btnSaveJourney.setEnabled(false);
                btnShareJourney.setEnabled(false);
                btnSaveJourney.setAlpha(0.5f);
                btnShareJourney.setAlpha(0.5f);
                
                String msg = shareAfterSave ? "Trip shared with community!" : "Trip saved to my trips!";
                tvSavePrompt.setText(msg);
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
            });
        });
    }
}
