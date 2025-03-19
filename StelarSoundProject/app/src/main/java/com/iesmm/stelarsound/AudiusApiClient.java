package com.iesmm.stelarsound;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AudiusApiClient {
    private static final String BASE_URL = "https://discoveryprovider.audius.co/";
    private static Retrofit retrofit = null;

    public static AudiusApiService getService() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit.create(AudiusApiService.class);
    }
}
