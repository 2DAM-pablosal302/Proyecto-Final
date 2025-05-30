package com.iesmm.stelarsound.Views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.iesmm.stelarsound.MainActivity;
import com.iesmm.stelarsound.Models.Song;
import com.iesmm.stelarsound.R;
import com.iesmm.stelarsound.ViewModels.SongViewModel;

import java.util.ArrayList;
import java.util.List;

public class PlayFragment extends Fragment {
    private SongViewModel songViewModel;
    private ImageView albumCover, playPauseButton;
    private TextView songTitle, artistName, currentTime, totalTime;
    private SeekBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_play, container, false);

        // Inicializar vistas
        albumCover = view.findViewById(R.id.album_cover);
        songTitle = view.findViewById(R.id.song_title);
        artistName = view.findViewById(R.id.artist_name);
        playPauseButton = view.findViewById(R.id.btn_play_pause);
        progressBar = view.findViewById(R.id.song_progress);
        currentTime = view.findViewById(R.id.current_time);
        totalTime = view.findViewById(R.id.total_time);


        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MainActivity activity = (MainActivity) getActivity();
        if (activity == null) return;

        songViewModel = new ViewModelProvider(requireActivity()).get(SongViewModel.class);

        // Observar la posición actual
        songViewModel.getCurrentPosition().observe(getViewLifecycleOwner(), position -> {
            if (position != null && progressBar != null) {
                progressBar.setProgress(position);
                currentTime.setText(formatTime(position));
            }
        });

        // Configurar el SeekBar
        progressBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    currentTime.setText(formatTime(progress));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Pausar las actualizaciones mientras el usuario mueve la barra
                activity.pauseProgressUpdates();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (activity.mediaPlayer != null) {
                    activity.mediaPlayer.seekTo(seekBar.getProgress());
                }
                // Reanudar actualizaciones
                activity.resumeProgressUpdates();
            }
            private boolean wasPlaying = false;
        });

        // Observar la duración de la canción
        songViewModel.getSongDuration().observe(getViewLifecycleOwner(), duration -> {
            if (duration != null) {
                progressBar.setMax(duration);
                totalTime.setText(formatTime(duration));
            }
        });

        // Observar la posición actual
        songViewModel.getCurrentPosition().observe(getViewLifecycleOwner(), position -> {
            if (position != null) {
                progressBar.setProgress(position);
                currentTime.setText(formatTime(position));
            }
        });

        // Observar cambios en la canción actual
        songViewModel.getCurrentSong().observe(getViewLifecycleOwner(), song -> {
            if (song != null) {
                updateSongInfo(song);
            }
        });

        // Observar cambios en el estado de reproducción
        songViewModel.getIsPlaying().observe(getViewLifecycleOwner(), isPlaying -> {
            if (isPlaying != null) {
                updatePlayButton(isPlaying);
            }
        });

        // Observar cambios en la posición actual
        songViewModel.getCurrentPosition().observe(getViewLifecycleOwner(), position -> {
            if (position != null) {
                updateProgress(position);
            }
        });

        // Configurar botón de play/pause
        playPauseButton.setOnClickListener(v -> {

            Boolean isPlaying = songViewModel.getIsPlaying().getValue();
            if (isPlaying != null) {
                if (isPlaying) {
                    // Pausar la reproducción
                    activity.mediaPlayer.pause();
                } else {
                    // Reanudar la reproducción
                    activity.mediaPlayer.start();
                    // No necesitamos actualizar la posición porque ya está guardada
                }
                songViewModel.setIsPlaying(!isPlaying);
            }
        });

        ImageView btnPrevious = view.findViewById(R.id.btn_previous);
        ImageView btnNext = view.findViewById(R.id.btn_next);

        btnPrevious.setOnClickListener(v -> playPreviousSong());
        btnNext.setOnClickListener(v -> playNextSong());

    }

    private void updateSongInfo(Song song) {
        songTitle.setText(song.getTitle());
        artistName.setText(song.getArtist());
        Glide.with(this)
                .load(song.getCover())
                .placeholder(R.drawable.ic_album_placeholder)
                .into(albumCover);

        // Aquí podrías cargar la duración total si está disponible
        // totalTime.setText(formatTime(song.getDuration()));
    }

    private void updatePlayButton(boolean isPlaying) {
        playPauseButton.setImageResource(
                isPlaying ? R.drawable.ic_pause_song : R.drawable.ic_play
        );
    }

    private void updateProgress(int position) {
        progressBar.setProgress(position);
        currentTime.setText(formatTime(position));
    }

    private String formatTime(int milliseconds) {
        int seconds = (milliseconds / 1000) % 60;
        int minutes = (milliseconds / (1000 * 60)) % 60;
        return String.format("%d:%02d", minutes, seconds);
    }

    private void playNextSong() {
        Song current = songViewModel.getCurrentSong().getValue();
        List<Song> queue = new ArrayList<>(songViewModel.getQueue().getValue());

        if (queue == null || queue.isEmpty()) {
            return;
        }

        Song nextSong = queue.remove(0);
        playSong(nextSong, queue, true); // SÍ añadir al historial

    }

    private void playPreviousSong() {
        MainActivity activity = (MainActivity) getActivity();
        if (activity == null) return;

        List<Song> history = new ArrayList<>(songViewModel.getRecentlyPlayed().getValue());
        Song current = songViewModel.getCurrentSong().getValue();

        if (history.size() < 2) {
            // Si no hay anterior o solo está la actual, reiniciar
            if (activity.mediaPlayer.getCurrentPosition() > 3000) {
                activity.mediaPlayer.seekTo(0);
            }
            return;
        }

        // Eliminar la canción actual y obtener la anterior
        history.remove(history.size() - 1); // Eliminar actual
        Song previous = history.remove(history.size() - 1); // Obtener anterior real

        songViewModel.setCurrentSong(previous); // Actualizar el ViewModel
        playSong(previous, songViewModel.getQueue().getValue(), false); // NO añadir al historial
        // Reproducir
    }



    private void playSong(Song song, List<Song> updatedQueue, boolean addToHistory) {
        MainActivity activity = (MainActivity) getActivity();
        if (activity == null) return;

        try {
            activity.mediaPlayer.reset();
            activity.mediaPlayer.setDataSource(song.getAudio());

            activity.mediaPlayer.setOnPreparedListener(mp -> {
                mp.start();
                songViewModel.setIsPlaying(true);
                songViewModel.setSongDuration(mp.getDuration());
            });

            activity.mediaPlayer.prepareAsync();

            songViewModel.setCurrentSong(song);
            songViewModel.setQueue(updatedQueue);
            if (addToHistory) {
                songViewModel.addToRecentlyPlayed(song);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }





}