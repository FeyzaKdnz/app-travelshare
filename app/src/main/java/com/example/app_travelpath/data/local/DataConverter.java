package com.example.app_travelpath.data.local;

import androidx.room.TypeConverter;
import com.example.app_travelpath.model.Spot;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

public class DataConverter {

    @TypeConverter
    public static List<Spot> fromSpotListString(String value) {
        if (value == null) {
            return Collections.emptyList();
        }
        Type listType = new TypeToken<List<Spot>>() {}.getType();
        return new Gson().fromJson(value, listType);
    }

    @TypeConverter
    public static String fromSpotList(List<Spot> list) {
        if (list == null) {
            return null;
        }
        Gson gson = new Gson();
        return gson.toJson(list);
    }

    // NOUVEAU : Convertisseur pour la liste des utilisateurs ayant liké (List<String>)
    @TypeConverter
    public static List<String> fromStringListString(String value) {
        if (value == null) {
            return Collections.emptyList();
        }
        Type listType = new TypeToken<List<String>>() {}.getType();
        return new Gson().fromJson(value, listType);
    }

    @TypeConverter
    public static String fromStringList(List<String> list) {
        if (list == null) {
            return null;
        }
        return new Gson().toJson(list);
    }
}
