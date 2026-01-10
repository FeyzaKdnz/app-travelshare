package com.example.app_travelpath.data.local;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import com.example.app_travelpath.model.Spot;

import java.util.List;

@Dao
public interface SpotDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Spot> spots);

    @Query("SELECT * FROM spots WHERE city = :city")
    List<Spot> getSpotsByCity(String city);

    @Query("DELETE FROM spots WHERE city = :city")
    void clearCity(String city);
}