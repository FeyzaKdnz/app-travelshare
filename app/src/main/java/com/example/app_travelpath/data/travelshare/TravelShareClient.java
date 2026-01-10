package com.example.app_travelpath.data.travelshare;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TravelShareClient {

    private static final String BASE_URL = "https://api.travelshare.mb-labs.dev/";

    private static TravelShareApiService service;

    public static TravelShareApiService getService() {
        if (service == null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            service = retrofit.create(TravelShareApiService.class);
        }
        return service;
    }
}