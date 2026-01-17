package com.example.app_travelpath.data.local;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import com.example.app_travelpath.model.SavedJourney;
import java.util.List;

@Dao
public interface SavedJourneyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(SavedJourney journey);

    @Update
    void update(SavedJourney journey);

    @Query("SELECT * FROM saved_journeys WHERE creatorUsername = :username ORDER BY date DESC")
    List<SavedJourney> getJourneysByUser(String username);

    @Query("SELECT * FROM saved_journeys WHERE isShared = 1 ORDER BY date DESC")
    List<SavedJourney> getSharedJourneys();

    @Query("SELECT * FROM saved_journeys ORDER BY date DESC")
    List<SavedJourney> getAllJourneys();

    @Delete
    void delete(SavedJourney journey);
}
