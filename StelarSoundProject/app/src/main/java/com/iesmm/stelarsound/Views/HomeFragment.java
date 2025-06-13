package com.iesmm.stelarsound.Views;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.iesmm.stelarsound.Adapters.CancionAdapter;
import com.iesmm.stelarsound.MainActivity;
import com.iesmm.stelarsound.Models.Song;
import com.iesmm.stelarsound.Models.Token;
import com.iesmm.stelarsound.R;
import com.iesmm.stelarsound.Services.SongService;
import com.iesmm.stelarsound.ViewModels.SongViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class HomeFragment extends Fragment {
    private RecyclerView recyclerView, recentRecyclerView;
    private CancionAdapter adapter, recentAdapter;
    private Map<String, String> loginData = new HashMap<>();
    private SongViewModel songViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = view.findViewById(R.id.popularRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recentRecyclerView = view.findViewById(R.id.recentRecyclerView);
        recentRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));


        Bundle bundle = getActivity().getIntent().getExtras();
        if (bundle != null) {
            Token token = bundle.getParcelable("token");
            loadSongs(token);
        }

        songViewModel = new ViewModelProvider(requireActivity()).get(SongViewModel.class);
        recentAdapter = new CancionAdapter(getContext(), new ArrayList<>());
        recentAdapter.setPlayButtonClickListener((position, song) -> {
            manejarReproduccion(position, song);
        });
        recentRecyclerView.setAdapter(recentAdapter);

        songViewModel.getRecentlyPlayed().observe(getViewLifecycleOwner(), recentSongs -> {
            Log.d("HomeFragment", "Historial actualizado: " + recentSongs.size());
            if (recentSongs != null) {
                recentAdapter.setCanciones(recentSongs);
            }
        });


        return view;
    }

    private void loadSongs(Token token) {
        SongService.obtenerCanciones(getContext(), token.getBody(), new SongService.VolleyCallback() {
            @Override
            public void onSuccess(ArrayList<Song> lista) {
                Log.d("HomeFragment", "Canciones recibidas: " + lista.size());
                adapter = new CancionAdapter(getContext(), lista);

                adapter.setPlayButtonClickListener((position, song) -> {
                    manejarReproduccion(position, song);
                });

                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onError(String mensaje) {
                Toast.makeText(getContext(), "Error: " + mensaje, Toast.LENGTH_LONG).show();
                Log.d("ERROR VOLLEY", mensaje);
            }
        });
    }

    private void manejarReproduccion(int position, Song song) {
        MainActivity mainActivity = (MainActivity) getActivity();
        if (mainActivity == null) return;

        MediaPlayer mediaPlayer = mainActivity.mediaPlayer;

        if (adapter.getCurrentlyPlayingPosition() == position) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                songViewModel.setIsPlaying(false);
            } else {
                mediaPlayer.start();
                songViewModel.setIsPlaying(true);
                updateProgress(mediaPlayer);
            }
        }

        else {

            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }

            reproducirCancion(mediaPlayer, song.getAudio());
            adapter.setCurrentlyPlayingPosition(position);
            songViewModel.setCurrentSong(song);
            songViewModel.setIsPlaying(true);
            songViewModel.addToRecentlyPlayed(song);
        }
    }

    private void reproducirCancion(MediaPlayer mediaPlayer, String audioUrl) {
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(audioUrl);
            mediaPlayer.prepareAsync();

            mediaPlayer.setOnPreparedListener(mp -> {
                songViewModel.setSongDuration(mp.getDuration());
                mp.start();
                songViewModel.setIsPlaying(true);
            });

            mediaPlayer.setOnCompletionListener(mp -> {
                adapter.setCurrentlyPlayingPosition(-1);
                songViewModel.setIsPlaying(false);
            });

            mediaPlayer.setOnErrorListener((mp, what, extra) -> {
                Toast.makeText(getContext(), "Error al reproducir la canción", Toast.LENGTH_SHORT).show();
                return false;
            });
        } catch (Exception e) {
            Toast.makeText(getContext(), "Error al cargar la canción", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateProgress(MediaPlayer mediaPlayer) {
        Handler handler = new Handler();
        Runnable updateRunnable = new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer.isPlaying()) {
                    int currentPos = mediaPlayer.getCurrentPosition();
                    songViewModel.setCurrentPosition(currentPos);

                    handler.postDelayed(this, 1000);
                }
            }
        };


        handler.post(updateRunnable);
    }
}