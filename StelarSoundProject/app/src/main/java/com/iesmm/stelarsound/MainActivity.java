package com.iesmm.stelarsound;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.iesmm.stelarsound.Adapters.CancionAdapter;
import com.iesmm.stelarsound.Models.Song;
import com.iesmm.stelarsound.Models.Token;
import com.iesmm.stelarsound.Services.ApiClient;
import com.iesmm.stelarsound.Services.ApiService;
import com.iesmm.stelarsound.Services.LoginResponse;
import com.iesmm.stelarsound.Services.SongService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;


// MainActivity.java
public class MainActivity extends AppCompatActivity {

    private ApiService apiService;
    private Map<String, String> loginData;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Bundle bundle = getIntent().getExtras();
        Token token = bundle.getParcelable("token");


        loginData = new HashMap<>();
        recyclerView = findViewById(R.id.popularRecyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        SongService.obtenerCanciones(this, token.getBody(), new SongService.VolleyCallback() {
            @Override
            public void onSuccess(ArrayList<Song> lista) {
                Log.d("MainActivity", "Canciones recibidas: " + lista.size());
                CancionAdapter adapter = new CancionAdapter(MainActivity.this, lista);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onError(String mensaje) {
                Toast.makeText(MainActivity.this, "Error: " + mensaje, Toast.LENGTH_LONG).show();
                Log.d("ERROR VOLLEY", mensaje);
            }
        });
    }


}