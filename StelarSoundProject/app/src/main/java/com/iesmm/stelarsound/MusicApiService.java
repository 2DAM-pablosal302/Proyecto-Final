package com.iesmm.stelarsound;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface MusicApiService {
    @GET("search")
    Call<ApiResponse<List<SongResponse>>> searchSongs(@Query("q") String query);
    
    @GET("stream/{id}")
    Call<StreamResponse> getStreamUrl(@Path("id") String id);
}

