package com.example.app_travelpath.data.travelshare;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface TravelShareApiService {
    @GET("photos")
    Call<List<TravelSharePhoto>> getPhotosAround(
            @Query("latitude") double latitude,
            @Query("longitude") double longitude,
            @Query("radiusKm") int radiusKm
    );

    @GET("frames/{id}")
    Call<TravelShareFrame> getFrameById(@Path("id") int frameId);
}
