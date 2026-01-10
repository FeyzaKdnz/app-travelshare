package com.example.app_travelpath.data.network;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface OsmApiService {

    @GET
    Call<OsmResponse> getPlacesSecure(@Url String url, @Query("data") String data);

    @GET
    Call<List<NominatimResult>> searchCity(@Url String url, @Query("q") String cityName, @Query("format") String format, @Query("limit") int limit);

    // --- NOUVEAU : Appel Météo ---
    @GET
    Call<WeatherResponse> getCurrentWeather(
        @Url String url,
        @Query("lat") double lat,
        @Query("lon") double lon,
        @Query("appid") String apiKey,
        @Query("units") String units
    );
}
