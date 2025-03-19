package com.iesmm.stelarsound;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private static final String APP_NAME = "StelarSound";
    private RecyclerView recyclerView;
    private CancionesAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        AudiusApiService api = AudiusApiClient.getService();


        api.getTrendingTracks(APP_NAME).enqueue(new Callback<>() {
            
            @Override
            public void onResponse(Call<List<AudiusTrack>> call, Response<List<AudiusTrack>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<AudiusTrack> canciones = response.body();
                    adapter = new CancionesAdapter(MainActivity.this, canciones);
                    recyclerView.setAdapter(adapter);
                } else {
                    Toast.makeText(MainActivity.this, "No se obtuvieron canciones", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<AudiusTrack>> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("ERROR CALLBACK", Objects.requireNonNull(t.getLocalizedMessage()));
            }
        });

    }
}