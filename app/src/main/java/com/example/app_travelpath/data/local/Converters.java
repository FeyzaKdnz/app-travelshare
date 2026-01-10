package com.example.app_travelpath.data.local;

import androidx.room.TypeConverter;
import com.example.app_travelpath.model.CategoryType;
import com.example.app_travelpath.model.EffortLevel;

public class Converters {

    @TypeConverter
    public static CategoryType toCategoryType(String value) {
        return value == null ? null : CategoryType.valueOf(value);
    }

    @TypeConverter
    public static String fromCategoryType(CategoryType category) {
        return category == null ? null : category.name();
    }

    @TypeConverter
    public static EffortLevel toEffortLevel(String value) {
        return value == null ? null : EffortLevel.valueOf(value);
    }

    @TypeConverter
    public static String fromEffortLevel(EffortLevel effort) {
        return effort == null ? null : effort.name();
    }
}