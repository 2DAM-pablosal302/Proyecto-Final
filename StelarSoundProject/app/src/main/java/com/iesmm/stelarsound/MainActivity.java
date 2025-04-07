package com.iesmm.stelarsound;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

// MainActivity.java
public class MainActivity extends AppCompatActivity {
    private MusicRepository musicRepository;
    private RecyclerView songsRecyclerView;
    private SongAdapter songAdapter;
    private ProgressBar progressBar;
    private TextView emptyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicializar vistas
        songsRecyclerView = findViewById(R.id.songs_recycler_view);
        progressBar = findViewById(R.id.progress_bar);
        emptyView = findViewById(R.id.empty_view);

        // Configurar RecyclerView
        setupRecyclerView();

        // Obtener instancia del repositorio
        musicRepository = ((MusicApplication) getApplication()).getMusicRepository();

        // Cargar canciones
        loadSongs("popular"); // Puedes cambiar el término de búsqueda
    }

    private void setupRecyclerView() {
        // Inicializar el adaptador con una lista vacía
        songAdapter = new SongAdapter(new ArrayList<>(), new SongAdapter.OnSongClickListener() {
            @Override
            public void onSongClick(Song song) {
                // Manejar clic en la canción
                playSong(song);
            }
        });

        // Configurar el RecyclerView
        songsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        songsRecyclerView.setAdapter(songAdapter);
        songsRecyclerView.setHasFixedSize(true);

        // Añadir decoración si es necesario
        songsRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
    }

    private void loadSongs(String query) {
        // Mostrar progreso
        showLoading(true);

        musicRepository.searchSongs(query, new MusicRepository.SearchCallback() {
            @Override
            public void onSuccess(List<Song> songs) {
                runOnUiThread(() -> {
                    showLoading(false);

                    if (songs.isEmpty()) {
                        showEmptyView(true);
                    } else {
                        showEmptyView(false);
                        songAdapter.updateSongs(songs);
                    }
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    showLoading(false);
                    showEmptyView(true);
                    Toast.makeText(MainActivity.this,
                            "Error: " + error, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void playSong(Song song) {
        musicRepository.playSong(song, new MusicRepository.PlaybackCallback() {
            @Override
            public void onSuccess() {
                runOnUiThread(() -> {
                    Toast.makeText(MainActivity.this,
                            "Reproduciendo: " + song.getTitle(), Toast.LENGTH_SHORT).show();
                    // Aquí podrías iniciar el PlayerActivity o mostrar controles
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    Toast.makeText(MainActivity.this,
                            "Error al reproducir: " + error, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void showLoading(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        songsRecyclerView.setVisibility(isLoading ? View.GONE : View.VISIBLE);
    }

    private void showEmptyView(boolean show) {
        emptyView.setVisibility(show ? View.VISIBLE : View.GONE);
        songsRecyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
    }
}