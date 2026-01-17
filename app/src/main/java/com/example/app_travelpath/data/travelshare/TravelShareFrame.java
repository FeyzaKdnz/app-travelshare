package com.example.app_travelpath.data.travelshare;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class TravelShareFrame {
    private int id;
    private String title;
    private String description;
    @SerializedName("authorName")
    private String authorName;
    private List<TravelSharePhoto> photos;

    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getAuthorName() { return authorName; }
    public List<TravelSharePhoto> getPhotos() { return photos; }
}
