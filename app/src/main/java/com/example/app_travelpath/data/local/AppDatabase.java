package com.example.app_travelpath.data.local;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.app_travelpath.model.Spot;
import com.example.app_travelpath.model.User;
import com.example.app_travelpath.model.SavedJourney;

// Augmentation de la version 2 -> 3 pour forcer la mise à jour de la table SavedJourney (changement date Long -> String)
@Database(entities = {Spot.class, User.class, SavedJourney.class}, version = 3, exportSchema = false)
@TypeConverters({Converters.class, DataConverter.class})
public abstract class AppDatabase extends RoomDatabase {
    private static volatile AppDatabase INSTANCE;
    public abstract SpotDao spotDao();
    public abstract UserDao userDao();
    public abstract SavedJourneyDao savedJourneyDao();

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "travel_path_database")
                            .fallbackToDestructiveMigration() // IMPORTANT: Va effacer la base existante pour recréer la nouvelle structure
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}