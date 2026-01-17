package com.example.app_travelpath.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import java.io.Serializable;

@Entity(tableName = "spots")
public class Spot implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String city;
    private String name;
    private double latitude;
    private double longitude;
    private CategoryType categoryType;
    private EffortLevel effortLevel;

    private double price;
    private double duration;
    @androidx.room.Ignore
    private String externalImageUrl;
    @androidx.room.Ignore
    private String externalAuthorName;

    private String openingHours;

    public Spot() {
    }

    public Spot(String city, String name, double latitude, double longitude,
                CategoryType categoryType, double price, EffortLevel effortLevel, double duration, String externalImageUrl) {
        this.city = city;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.categoryType = categoryType;
        this.price = price;
        this.effortLevel = effortLevel;
        this.duration = duration;
        this.externalImageUrl = externalImageUrl;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }

    public CategoryType getCategoryType() { return categoryType; }
    public void setCategoryType(CategoryType categoryType) { this.categoryType = categoryType; }

    public EffortLevel getEffortLevel() { return effortLevel; }
    public void setEffortLevel(EffortLevel effortLevel) { this.effortLevel = effortLevel; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public double getDuration() { return duration; }
    public void setDuration(double duration) { this.duration = duration; }

    public String getExternalImageUrl() { return externalImageUrl; }
    public void setExternalImageUrl(String url) { this.externalImageUrl = url; }

    public String getExternalAuthorName() { return externalAuthorName; }
    public void setExternalAuthorName(String author) { this.externalAuthorName = author; }

    public String getOpeningHours() { return openingHours; }
    public void setOpeningHours(String openingHours) { this.openingHours = openingHours; }
}
