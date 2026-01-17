package com.example.app_travelpath.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;
import com.example.app_travelpath.data.local.DataConverter;
import java.util.ArrayList;
import java.util.List;

@Entity(tableName = "saved_journeys")
public class SavedJourney {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String name;
    public String date;
    public String creatorUsername;

    public boolean isShared = false;
    public int likesCount = 0;

    // NOUVEAU : Liste des utilisateurs qui ont liké ce parcours
    // Cela permet de savoir si l'utilisateur actuel a déjà liké
    public List<String> likedByUsers = new ArrayList<>();

    @TypeConverters(DataConverter.class)
    public List<Spot> spotList;

    public SavedJourney(String name, String date, String creatorUsername, List<Spot> spotList) {
        this.name = name;
        this.date = date;
        this.creatorUsername = creatorUsername;
        this.spotList = spotList;
    }
}
