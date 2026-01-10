package com.example.app_travelpath.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;
import com.example.app_travelpath.data.local.DataConverter;
import java.util.List;

@Entity(tableName = "saved_journeys")
public class SavedJourney {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String name;
    
    // MODIFICATION : On stocke la date sous forme de String lisible (ex: "05/01/2026")
    public String date;

    // On utilise notre convertisseur pour stocker la liste des spots en JSON
    @TypeConverters(DataConverter.class)
    public List<Spot> spotList;

    public SavedJourney(String name, String date, List<Spot> spotList) {
        this.name = name;
        this.date = date;
        this.spotList = spotList;
    }
}