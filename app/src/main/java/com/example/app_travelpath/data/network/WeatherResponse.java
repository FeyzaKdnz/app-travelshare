package com.example.app_travelpath.data.network;

import java.util.List;

public class WeatherResponse {
    public Main main;
    public List<Weather> weather;
    public static class Main {
        public double temp;
    }

    public static class Weather {
        public String icon;
        public String description;
    }
}
