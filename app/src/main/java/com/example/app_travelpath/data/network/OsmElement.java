package com.example.app_travelpath.data.network;

import java.util.Map;

public class OsmElement {
    public long id;
    public double lat;
    public double lon;
    public Center center;
    public Map<String, String> tags;

    public static class Center {
        public double lat;
        public double lon;
    }
}