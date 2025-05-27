package com.iesmm.stelarsound.Views;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.iesmm.stelarsound.Adapters.PlaylistSongAdapter;
import com.iesmm.stelarsound.Models.Playlist;
import com.iesmm.stelarsound.Models.Song;
import com.iesmm.stelarsound.R;
import com.iesmm.stelarsound.Services.PlaylistService;

import java.util.ArrayList;
import java.util.List;

public class PlaylistDetailActivity extends AppCompatActivity
        implements PlaylistSongAdapter.OnSongClickListener {

    // Vistas
    private TextView playlistNameTextView;
    private TextView playlistDetailsTextView;
    private RecyclerView songsRecyclerView;
    private FloatingActionButton playButton;

    // Adaptador y datos
    private PlaylistSongAdapter adapter;
    private List<Song> songs = new ArrayList<>();
    private MediaPlayer mediaPlayer;

    // Datos de la playlist
    private String authToken;
    private int playlistId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.playlist_detail);

        // Inicializar vistas con los IDs correctos
        playlistNameTextView = findViewById(R.id.playlistName);
        playlistDetailsTextView = findViewById(R.id.playlistDetails);
        songsRecyclerView = findViewById(R.id.songsRecyclerView);
        playButton = findViewById(R.id.playButton);

        // Obtener datos del intent
        playlistId = getIntent().getIntExtra("playlist_id", -1);
        authToken = getIntent().getStringExtra("auth_token");

        if (playlistId == -1 || authToken == null) {
            showErrorAndFinish();
            return;
        }

        setupRecyclerView();
        setupPlayButton();
        loadPlaylistDetails();
    }

    private void showErrorAndFinish() {
        Toast.makeText(this, "Error al cargar la playlist", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void setupRecyclerView() {
        adapter = new PlaylistSongAdapter(songs, this);
        songsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        songsRecyclerView.setAdapter(adapter);
    }

    private void setupPlayButton() {
        playButton.setOnClickListener(v -> {
            if (!songs.isEmpty()) {
                playSong(0); // Reproducir primera canción
            } else {
                Toast.makeText(this, "No hay canciones en esta playlist", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadPlaylistDetails() {
        PlaylistService.getPlaylistDetail(this, authToken, playlistId,
                new PlaylistService.PlaylistDetailCallback() {
                    @Override
                    public void onSuccess(Playlist playlist) {
                        runOnUiThread(() -> updateUI(playlist));
                    }

                    @Override
                    public void onError(String message) {
                        runOnUiThread(() -> {
                            Toast.makeText(PlaylistDetailActivity.this, message, Toast.LENGTH_SHORT).show();
                        });
                    }
                });
    }

    private void updateUI(Playlist playlist) {
        // Actualizar header
        playlistNameTextView.setText(playlist.getName());
        playlistDetailsTextView.setText(String.format("%s • %d canciones",
                playlist.getCreator(), playlist.getSongCount()));

        // Actualizar lista de canciones
        songs.clear();
        songs.addAll(playlist.getSongs());
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onSongClick(int position) {
        playSong(position);
    }

    private void playSong(int position) {
        Song song = songs.get(position);

        try {
            releaseMediaPlayer(); // Liberar recursos anteriores

            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(song.getAudio());
            mediaPlayer.prepareAsync();

            mediaPlayer.setOnPreparedListener(mp -> {
                mp.start();
                showNowPlayingMessage(song.getTitle());
            });

            mediaPlayer.setOnErrorListener((mp, what, extra) -> {
                showPlaybackError();
                return true;
            });

        } catch (Exception e) {
            showPlaybackError();
            e.printStackTrace();
        }
    }

    private void releaseMediaPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    private void showNowPlayingMessage(String songTitle) {
        Toast.makeText(this, "Reproduciendo: " + songTitle, Toast.LENGTH_SHORT).show();
    }

    private void showPlaybackError() {
        Toast.makeText(this, "Error al reproducir la canción", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onOptionsClick(Song song, View anchorView) {
        // Implementar menú de opciones
        Toast.makeText(this, "Opciones: " + song.getTitle(), Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseMediaPlayer();
    }
}
