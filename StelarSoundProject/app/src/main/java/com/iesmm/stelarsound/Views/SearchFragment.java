package com.iesmm.stelarsound.Views;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.iesmm.stelarsound.Adapters.CancionAdapter;
import com.iesmm.stelarsound.Adapters.SongResultsAdapter;
import com.iesmm.stelarsound.MainActivity;
import com.iesmm.stelarsound.Models.Song;
import com.iesmm.stelarsound.Models.Token;
import com.iesmm.stelarsound.R;
import com.iesmm.stelarsound.Services.SongService;
import com.iesmm.stelarsound.ViewModels.SongViewModel;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;


public class SearchFragment extends Fragment implements SongResultsAdapter.OnSongClickListener {

    private RecyclerView searchResultsRecyclerView;
    private EditText searchEditText;
    private ProgressBar progressBar;
    private TextView emptyStateView, errorStateView;
    private SongResultsAdapter songResultsAdapter;
    private Token token;
    private SongViewModel songViewModel;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Bundle bundle = getActivity().getIntent().getExtras();
        if (bundle != null) {
            token = bundle.getParcelable("token");
        } else {
            Log.e("Token ERROR", "Token no recibido");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        searchResultsRecyclerView = view.findViewById(R.id.searchResultsRecyclerView);
        searchEditText = view.findViewById(R.id.searchEditText);
        progressBar = view.findViewById(R.id.progressBar);
        emptyStateView = view.findViewById(R.id.emptyStateView);
        errorStateView = view.findViewById(R.id.errorStateView);
        songViewModel = new ViewModelProvider(requireActivity()).get(SongViewModel.class);

        showInitialState();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        songResultsAdapter = new SongResultsAdapter(new ArrayList<>(), this);
        searchResultsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        searchResultsRecyclerView.setAdapter(songResultsAdapter);

        setupSearchFunctionality();
    }

    private void setupSearchFunctionality() {
        searchEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch();
                return true;
            }
            return false;
        });

        searchEditText.addTextChangedListener(new TextWatcher() {
            private final Handler handler = new Handler();
            private Runnable runnable;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                handler.removeCallbacks(runnable);
            }

            @Override
            public void afterTextChanged(Editable s) {
                runnable = () -> performSearch();
                handler.postDelayed(runnable, 500);
            }
        });
    }

    private void performSearch() {
        String query = searchEditText.getText().toString().trim();
        if (query.isEmpty()) {
            showInitialState();
            return;
        }
        showLoadingState();
        executeSearchRequest(query);
    }

    private void executeSearchRequest(String query) {
        try {
            SongService.searchSongs(requireContext(), token.getBody(), query, new SongService.VolleyCallback() {
                @Override
                public void onSuccess(ArrayList<Song> songs) {
                    requireActivity().runOnUiThread(() -> handleSearchResults(songs));
                }

                @Override
                public void onError(String error) {
                    requireActivity().runOnUiThread(() -> handleSearchError(error));
                }
            });
        } catch (UnsupportedEncodingException e) {
            handleSearchError("Error en la codificación");
        }
    }

    private void handleSearchResults(ArrayList<Song> songs) {
        if (songs == null || songs.isEmpty()) {
            showEmptyResultsState();
        } else {
            songResultsAdapter.updateData(songs);
            showResultsState();
        }
    }

    private void handleSearchError(String error) {
        showErrorState();
        Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
    }

    // Estados de la UI
    private void showInitialState() {
        progressBar.setVisibility(View.GONE);
        searchResultsRecyclerView.setVisibility(View.GONE);
        emptyStateView.setVisibility(View.VISIBLE);
        emptyStateView.setText("Busca canciones, artistas o álbumes");
        errorStateView.setVisibility(View.GONE);
    }

    private void showLoadingState() {
        progressBar.setVisibility(View.VISIBLE);
        searchResultsRecyclerView.setVisibility(View.GONE);
        emptyStateView.setVisibility(View.GONE);
        errorStateView.setVisibility(View.GONE);
    }

    private void showResultsState() {
        progressBar.setVisibility(View.GONE);
        searchResultsRecyclerView.setVisibility(View.VISIBLE);
        emptyStateView.setVisibility(View.GONE);
        errorStateView.setVisibility(View.GONE);
    }

    private void showEmptyResultsState() {
        progressBar.setVisibility(View.GONE);
        searchResultsRecyclerView.setVisibility(View.GONE);
        emptyStateView.setVisibility(View.VISIBLE);
        emptyStateView.setText("No se encontraron resultados");
        errorStateView.setVisibility(View.GONE);
    }

    private void showErrorState() {
        progressBar.setVisibility(View.GONE);
        searchResultsRecyclerView.setVisibility(View.GONE);
        emptyStateView.setVisibility(View.GONE);
        errorStateView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onSongClick(Song song) {
        navigateToPlayFragment(song);
    }

    @Override
    public void onPlayClick(Song song) {
        navigateToPlayFragment(song);
    }

    private void navigateToPlayFragment(Song song) {
        songViewModel.setCurrentSong(song);

        MainActivity activity = (MainActivity) getActivity();
        if (activity != null) {
            try {
                activity.mediaPlayer.reset();
                activity.mediaPlayer.setDataSource(song.getAudio()); // <-- IMPORTANTE: usa getAudio() o el campo correcto
                activity.mediaPlayer.prepare(); // o prepareAsync si quieres hacerlo en background
                activity.mediaPlayer.start();

                songViewModel.setIsPlaying(true);
                songViewModel.setSongDuration(activity.mediaPlayer.getDuration());

            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "Error al reproducir la canción", Toast.LENGTH_SHORT).show();
                return;
            }

            FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, new PlayFragment());
            transaction.addToBackStack(null);
            transaction.commit();
            activity.getBottomNav().setSelectedItemId(R.id.nav_play);

        }
    }

}
