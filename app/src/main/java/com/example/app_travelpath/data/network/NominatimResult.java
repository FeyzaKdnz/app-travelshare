package com.example.app_travelpath.data.network;

import com.google.gson.annotations.SerializedName;

public class NominatimResult {
    public String lat;
    public String lon;
    public String display_name;

    @SerializedName("importance")
    public double importance;
}
