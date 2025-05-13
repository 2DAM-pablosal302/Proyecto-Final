package com.iesmm.stelarsound;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.iesmm.stelarsound.Adapters.CancionAdapter;
import com.iesmm.stelarsound.Models.Song;
import com.iesmm.stelarsound.Models.Token;
import com.iesmm.stelarsound.Services.SongService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements CancionAdapter.OnPlayButtonClickListener {
    private Map<String, String> loginData;
    private RecyclerView recyclerView;
    private CancionAdapter adapter;
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Bundle bundle = getIntent().getExtras();
        Token token = bundle.getParcelable("token");

        loginData = new HashMap<>();
        recyclerView = findViewById(R.id.popularRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        mediaPlayer = new MediaPlayer();

        SongService.obtenerCanciones(this, token.getBody(), new SongService.VolleyCallback() {
            @Override
            public void onSuccess(ArrayList<Song> lista) {
                Log.d("MainActivity", "Canciones recibidas: " + lista.size());
                adapter = new CancionAdapter(MainActivity.this, lista);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onError(String mensaje) {
                Toast.makeText(MainActivity.this, "Error: " + mensaje, Toast.LENGTH_LONG).show();
                Log.d("ERROR VOLLEY", mensaje);
            }
        });
    }

    @Override
    public void onPlayButtonClick(int position, Song song) {
        if (adapter == null) return;

        // Si la misma canción ya se está reproduciendo, pausar
        if (adapter.getCurrentlyPlayingPosition() == position && mediaPlayer.isPlaying()) {
            pauseSong();
            adapter.setCurrentlyPlayingPosition(-1);
        } else {
            // Si hay otra canción reproduciéndose, detenerla primero
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
                mediaPlayer.reset();
            }

            // Reproducir la nueva canción
            playSong(song.getAudio());
            adapter.setCurrentlyPlayingPosition(position);
        }
    }

    private void playSong(String audioUrl) {
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(audioUrl);
            mediaPlayer.prepareAsync();

            mediaPlayer.setOnPreparedListener(mp -> mp.start());

            mediaPlayer.setOnCompletionListener(mp -> {
                adapter.setCurrentlyPlayingPosition(-1);
            });

            mediaPlayer.setOnErrorListener((mp, what, extra) -> {
                Toast.makeText(this, "Error al reproducir la canción", Toast.LENGTH_SHORT).show();
                return false;
            });
        } catch (IOException e) {
            Toast.makeText(this, "Error al cargar la canción", Toast.LENGTH_SHORT).show();
        }
    }

    private void pauseSong() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}