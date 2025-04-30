package com.iesmm.stelarsound;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.iesmm.stelarsound.Services.ApiClient;
import com.iesmm.stelarsound.Services.ApiService;
import com.iesmm.stelarsound.Services.LoginResponse;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;


// MainActivity.java
public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


    }

    ApiService apiService = ApiClient.getClient().create(ApiService.class);

    Map<String, String> loginData = new HashMap<>();


    Call<LoginResponse> call = apiService.login(loginData);




}