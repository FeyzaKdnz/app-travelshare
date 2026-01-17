package com.example.app_travelpath.data.network;

import java.util.List;

public class OsrmResponse {
    public List<Route> routes;

    public static class Route {
        public Geometry geometry;
        public double distance;
        public double duration;
    }

    public static class Geometry {
        public String type;
        public List<List<Double>> coordinates;
    }
}
