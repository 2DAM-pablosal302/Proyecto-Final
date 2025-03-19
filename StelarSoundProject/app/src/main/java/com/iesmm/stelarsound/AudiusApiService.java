package com.iesmm.stelarsound;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface AudiusApiService {
    @GET("v1/tracks/trending")
    Call<List<AudiusTrack>> getTrendingTracks(@Query("app_name") String appName);

}
