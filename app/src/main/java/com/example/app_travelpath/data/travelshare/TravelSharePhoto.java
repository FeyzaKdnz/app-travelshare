package com.example.app_travelpath.data.travelshare;

import com.google.gson.annotations.SerializedName;

public class TravelSharePhoto {
    private int id;
    @SerializedName("image")
    private String filename;
    private double latitude;
    private double longitude;
    @SerializedName("frame_id")
    private int frameId;

    /* --- Getters / Setters --- */

    public int getId() { return id; }
    public String getFilename() { return filename; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    public int getFrameId() { return frameId; }

    public String getFullImageUrl() {
        return "https://api.travelshare.mb-labs.dev/media/photos/" + filename;
    }
}