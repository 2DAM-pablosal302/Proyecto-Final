package com.iesmm.stelarsound.Views;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.iesmm.stelarsound.Adapters.CancionAdapter;
import com.iesmm.stelarsound.MainActivity;
import com.iesmm.stelarsound.Models.Song;
import com.iesmm.stelarsound.Models.Token;
import com.iesmm.stelarsound.R;
import com.iesmm.stelarsound.Services.SongService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class HomeFragment extends Fragment {
    private RecyclerView recyclerView;
    private CancionAdapter adapter;
    private Map<String, String> loginData = new HashMap<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = view.findViewById(R.id.popularRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        // Obtener token del intent de la actividad
        Bundle bundle = getActivity().getIntent().getExtras();
        if (bundle != null) {
            Token token = bundle.getParcelable("token");
            loadSongs(token);
        }

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

        if (adapter.getCurrentlyPlayingPosition() == position && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            adapter.setCurrentlyPlayingPosition(-1);
        } else {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
                mediaPlayer.reset();
            }
            reproducirCancion(mediaPlayer, song.getAudio());
            adapter.setCurrentlyPlayingPosition(position);
        }
    }

    private void reproducirCancion(MediaPlayer mediaPlayer, String audioUrl) {
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(audioUrl);
            mediaPlayer.prepareAsync();

            mediaPlayer.setOnPreparedListener(mp -> mp.start());

            mediaPlayer.setOnCompletionListener(mp -> {
                if (adapter != null) {
                    adapter.setCurrentlyPlayingPosition(-1);
                }
            });

            mediaPlayer.setOnErrorListener((mp, what, extra) -> {
                Toast.makeText(getContext(), "Error al reproducir la canción", Toast.LENGTH_SHORT).show();
                return false;
            });
        } catch (Exception e) {
            Toast.makeText(getContext(), "Error al cargar la canción", Toast.LENGTH_SHORT).show();
        }
    }
}