package com.iesmm.stelarsound.Services;

import com.iesmm.stelarsound.Models.User;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Header;

import java.util.List;
import java.util.Map;

public interface ApiService {

    @POST("loginAndroid")
    Call<LoginResponse> login(@Body Map<String, String> body);

    @POST("logoutAndroid")
    Call<Void> logout(@Header("Authorization") String token);

    @GET("usuarios")
    Call<List<User>> getUsers(@Header("Authorization") String token);
}
