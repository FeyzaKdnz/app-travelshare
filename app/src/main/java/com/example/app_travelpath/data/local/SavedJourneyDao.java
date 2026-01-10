package com.example.app_travelpath.data.local;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import com.example.app_travelpath.model.SavedJourney;
import java.util.List;

@Dao
public interface SavedJourneyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(SavedJourney journey);

    @Query("SELECT * FROM saved_journeys ORDER BY date DESC")
    List<SavedJourney> getAllJourneys();

    @Delete
    void delete(SavedJourney journey);
}