package com.iesmm.stelarsound.Views;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.iesmm.stelarsound.Adapters.PlaylistSongAdapter;
import com.iesmm.stelarsound.Adapters.SimpleSongAdapter;
import com.iesmm.stelarsound.Models.Song;
import com.iesmm.stelarsound.R;
import com.iesmm.stelarsound.Services.PlaylistService;
import com.iesmm.stelarsound.Services.SongService;

import java.util.ArrayList;
import java.util.List;

public class SongPickerDialogFragment extends DialogFragment {

    private static final String ARG_PLAYLIST_ID = "playlist_id";
    private static final String ARG_AUTH_TOKEN = "auth_token";

    private int playlistId;
    private String authToken;
    private List<Song> allSongs = new ArrayList<>();
    private SimpleSongAdapter adapter;

    public static SongPickerDialogFragment newInstance(int playlistId, String authToken) {
        SongPickerDialogFragment fragment = new SongPickerDialogFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PLAYLIST_ID, playlistId);
        args.putString(ARG_AUTH_TOKEN, authToken);
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (getArguments() != null) {
            playlistId = getArguments().getInt(ARG_PLAYLIST_ID);
            authToken = getArguments().getString(ARG_AUTH_TOKEN);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_song_picker, null);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewSongs);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new SimpleSongAdapter(allSongs, selectedSong -> {
            Log.d("DEBUG", "Selected song ID: " + selectedSong.toString());
            Log.d("DEBUG", "Token: " + authToken);
            PlaylistService.addSongToPlaylist(getContext(), authToken, playlistId, selectedSong.getId(), new PlaylistService.SongListCallback() {
                @Override
                public void onSuccess(List<Song> updatedSongs) {
                    Toast.makeText(getContext(), "Canción añadida", Toast.LENGTH_SHORT).show();
                    dismiss();
                }

                @Override
                public void onError(String message) {
                    Toast.makeText(getContext(), "Error al añadir: " + message, Toast.LENGTH_SHORT).show();
                }
            });
        });


        recyclerView.setAdapter(adapter);

        loadAllSongs();

        builder.setView(view)
                .setTitle("Añadir canción a playlist")
                .setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());

        return builder.create();
    }

    private void loadAllSongs() {
        SongService.obtenerCanciones(getContext(), authToken, new SongService.VolleyCallback() {

            @Override
            public void onSuccess(ArrayList<Song> lista) {
                allSongs.clear();
                allSongs.addAll(lista);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(String message) {
                Toast.makeText(getContext(), "Error al cargar canciones: " + message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
