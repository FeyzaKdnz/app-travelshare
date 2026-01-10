package com.example.app_travelpath.data.network;

import java.util.List;

// Classe principale qui mappe toute la réponse JSON
public class WeatherResponse {
    public Main main;
    public List<Weather> weather;

    // ex: {"main":{"temp":282.55}, "weather":[{"icon":"01d"}]}

    // Sous-classe pour l'objet "main" qui contient la température
    public static class Main {
        public double temp;
    }

    // Sous-classe pour le tableau "weather" qui contient l'icône
    public static class Weather {
        public String icon;
        public String description;
    }
}
