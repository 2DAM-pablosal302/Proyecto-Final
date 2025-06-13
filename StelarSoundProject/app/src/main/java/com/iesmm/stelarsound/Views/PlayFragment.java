package com.iesmm.stelarsound.Views;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
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
import com.iesmm.stelarsound.Models.Token;
import com.iesmm.stelarsound.R;
import com.iesmm.stelarsound.Services.SongService;
import com.iesmm.stelarsound.ViewModels.SongViewModel;

import java.util.ArrayList;
import java.util.List;

public class PlayFragment extends Fragment {
    private SongViewModel songViewModel;
    private ImageView albumCover, playPauseButton;
    private TextView songTitle, artistName, currentTime, totalTime;
    private SeekBar progressBar;
    private ImageView likeButton;
    private boolean isLiked = false;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_play, container, false);

        albumCover = view.findViewById(R.id.album_cover);
        songTitle = view.findViewById(R.id.song_title);
        artistName = view.findViewById(R.id.artist_name);
        playPauseButton = view.findViewById(R.id.btn_play_pause);
        progressBar = view.findViewById(R.id.song_progress);
        currentTime = view.findViewById(R.id.current_time);
        totalTime = view.findViewById(R.id.total_time);
        likeButton = view.findViewById(R.id.likeButton);


        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MainActivity activity = (MainActivity) getActivity();
        if (activity == null) return;

        songViewModel = new ViewModelProvider(requireActivity()).get(SongViewModel.class);

        songViewModel.getCurrentPosition().observe(getViewLifecycleOwner(), position -> {
            if (position != null && progressBar != null) {
                progressBar.setProgress(position);
                currentTime.setText(formatTime(position));
            }

        });

        progressBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    currentTime.setText(formatTime(progress));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

                activity.pauseProgressUpdates();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (activity.mediaPlayer != null) {
                    activity.mediaPlayer.seekTo(seekBar.getProgress());
                }

                activity.resumeProgressUpdates();
            }
            private boolean wasPlaying = false;
        });


        songViewModel.getSongDuration().observe(getViewLifecycleOwner(), duration -> {
            if (duration != null) {
                progressBar.setMax(duration);
                totalTime.setText(formatTime(duration));
            }
        });


        songViewModel.getCurrentPosition().observe(getViewLifecycleOwner(), position -> {
            if (position != null) {
                progressBar.setProgress(position);
                currentTime.setText(formatTime(position));
            }
        });


        songViewModel.getCurrentSong().observe(getViewLifecycleOwner(), song -> {
            if (song != null) {
                updateSongInfo(song);
                isLiked = song.isLiked();
                updateSongInfo(song);
            }
        });


        songViewModel.getIsPlaying().observe(getViewLifecycleOwner(), isPlaying -> {
            if (isPlaying != null) {
                updatePlayButton(isPlaying);
            }
        });


        songViewModel.getCurrentPosition().observe(getViewLifecycleOwner(), position -> {
            if (position != null) {
                updateProgress(position);
            }
        });


        playPauseButton.setOnClickListener(v -> {

            Boolean isPlaying = songViewModel.getIsPlaying().getValue();
            if (isPlaying != null) {
                if (isPlaying) {

                    activity.mediaPlayer.pause();
                } else {

                    activity.mediaPlayer.start();

                }
                songViewModel.setIsPlaying(!isPlaying);
            }
        });

        ImageView btnPrevious = view.findViewById(R.id.btn_previous);
        ImageView btnNext = view.findViewById(R.id.btn_next);

        btnPrevious.setOnClickListener(v -> playPreviousSong());
        btnNext.setOnClickListener(v -> playNextSong());

        likeButton.setOnClickListener(v -> toggleLike());

    }

    private void updateSongInfo(Song song) {
        songTitle.setText(song.getTitle());
        artistName.setText(song.getArtist());
        Glide.with(this)
                .load(song.getCover())
                .placeholder(R.drawable.ic_album_placeholder)
                .into(albumCover);
        updateLikeButton();

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
        playSong(nextSong, queue, true);

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


        history.remove(history.size() - 1);
        Song previous = history.remove(history.size() - 1);

        songViewModel.setCurrentSong(previous);
        playSong(previous, songViewModel.getQueue().getValue(), false);
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

    private void updateLikeButton() {
        if (isLiked) {
            likeButton.setImageResource(R.drawable.ic_heart_filled);
        } else {
            likeButton.setImageResource(R.drawable.ic_heart_empty);
        }
    }

    private void toggleLike() {
        Song currentSong = songViewModel.getCurrentSong().getValue();
        if (currentSong == null) return;

        Token token = null;
        Bundle bundle = getActivity().getIntent().getExtras();
        if (bundle != null) {
             token = bundle.getParcelable("token");
        }
        Context context = getContext();

        if (isLiked) {
            SongService.unlikeSong(context, token.getBody(), currentSong.getId(), new SongService.SimpleCallback() {
                @Override
                public void onSuccess() {
                    isLiked = false;
                    currentSong.setLiked(false);
                    updateLikeButton();
                }

                @Override
                public void onError(String errorMsg) {
                    Log.e("UNLIKE", errorMsg);
                }
            });
        } else {
            SongService.likeSong(context, token.getBody(), currentSong.getId(), new SongService.SimpleCallback() {
                @Override
                public void onSuccess() {
                    isLiked = true;
                    currentSong.setLiked(true);
                    updateLikeButton();
                }

                @Override
                public void onError(String errorMsg) {
                    Log.e("LIKE", errorMsg);
                }
            });
        }
    }




}