package com.iesmm.stelarsound.Views;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.iesmm.stelarsound.Adapters.PlaylistSongAdapter;
import com.iesmm.stelarsound.MainActivity;
import com.iesmm.stelarsound.Models.Playlist;
import com.iesmm.stelarsound.Models.Song;
import com.iesmm.stelarsound.R;
import com.iesmm.stelarsound.Services.PlaylistService;
import com.iesmm.stelarsound.ViewModels.SongViewModel;

import java.util.ArrayList;
import java.util.List;


public class PlaylistDetailFragment extends Fragment implements PlaylistSongAdapter.OnSongClickListener {

    private TextView playlistNameTextView;
    private TextView playlistDetailsTextView;
    private RecyclerView songsRecyclerView;
    private FloatingActionButton playButton;

    private PlaylistSongAdapter adapter;
    private List<Song> songs = new ArrayList<>();

    private String authToken;
    private int playlistId;

    public PlaylistDetailFragment() {}

    private Button addSongBtn;

    public static PlaylistDetailFragment newInstance(int playlistId, String authToken) {
        PlaylistDetailFragment fragment = new PlaylistDetailFragment();
        Bundle args = new Bundle();
        args.putInt("playlist_id", playlistId);
        args.putString("auth_token", authToken);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            playlistId = getArguments().getInt("playlist_id");
            authToken = getArguments().getString("auth_token");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_playlist_detail, container, false);

        playlistNameTextView = view.findViewById(R.id.playlistName);
        playlistDetailsTextView = view.findViewById(R.id.playlistDetails);
        songsRecyclerView = view.findViewById(R.id.songsRecyclerView);
        playButton = view.findViewById(R.id.playButton);
        addSongBtn = view.findViewById(R.id.addSongButton);

        adapter = new PlaylistSongAdapter(songs, this, true);
        songsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        songsRecyclerView.setAdapter(adapter);

        loadPlaylistDetails();

        playButton.setOnClickListener(v -> {
            if (!songs.isEmpty()) {
                playSong(0);
            } else {
                Toast.makeText(getContext(), "No hay canciones en esta playlist", Toast.LENGTH_SHORT).show();
            }
        });

        addSongBtn.setOnClickListener(v -> {
            com.iesmm.stelarsound.Views.SongPickerDialogFragment dialog = com.iesmm.stelarsound.Views.SongPickerDialogFragment.newInstance(playlistId, authToken);
            dialog.show(getParentFragmentManager(), "SongPickerDialog");
            loadPlaylistDetails();
        });


        return view;
    }

    private void loadPlaylistDetails() {
        PlaylistService.getPlaylistDetail(requireContext(), authToken, playlistId,
                new PlaylistService.PlaylistDetailCallback() {
                    @Override
                    public void onSuccess(Playlist playlist) {
                        requireActivity().runOnUiThread(() -> updateUI(playlist));
                    }

                    @Override
                    public void onError(String message) {
                        requireActivity().runOnUiThread(() -> {
                            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                        });
                    }
                });
    }

    private void updateUI(Playlist playlist) {
        playlistNameTextView.setText(playlist.getName());
        playlistDetailsTextView.setText(String.format("%s • %d canciones",
                playlist.getCreator(), playlist.getSongCount()));
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
        List<Song> queue = new ArrayList<>(songs);
        queue.remove(position);

        MainActivity activity = (MainActivity) getActivity();
        if (activity != null) {
            try {
                activity.mediaPlayer.reset();
                activity.mediaPlayer.setDataSource(song.getAudio());
                activity.mediaPlayer.prepare();
                activity.mediaPlayer.start();

                SongViewModel songViewModel = new ViewModelProvider(requireActivity()).get(SongViewModel.class);
                songViewModel.setCurrentSong(song);
                songViewModel.setIsPlaying(true);
                songViewModel.setSongDuration(activity.mediaPlayer.getDuration());
                songViewModel.setQueue(queue);

                activity.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new PlayFragment())
                        .addToBackStack(null)
                        .commit();
                activity.getBottomNav().setSelectedItemId(R.id.nav_play);
            } catch (Exception e) {
                Toast.makeText(getContext(), "Error al reproducir la canción: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("PLAY_ERROR", e.getMessage());
            }
        }
    }

    public void removeSongFromPlaylist(int songId) {
        PlaylistService.removeSongFromPlaylist(requireContext(), authToken, playlistId, songId,
                new PlaylistService.SongListCallback() {
                    @Override
                    public void onSuccess(List<Song> updatedSongs) {
                        requireActivity().runOnUiThread(() -> {
                            songs.clear();
                            songs.addAll(updatedSongs);
                            adapter.notifyDataSetChanged();
                            Toast.makeText(getContext(), "Canción eliminada", Toast.LENGTH_SHORT).show();
                        });
                    }

                    @Override
                    public void onError(String message) {
                        requireActivity().runOnUiThread(() -> {
                            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                        });
                    }
                });
    }


    @Override
    public void onDeleteClick(int songId, int position) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Eliminar canción")
                .setMessage("¿Estás seguro de que quieres eliminar esta canción de la playlist?")
                .setPositiveButton("Eliminar", (dialog, which) -> removeSongFromPlaylist(songId))
                .setNegativeButton("Cancelar", null)
                .show();
    }

    @Override
    public void onOptionsClick(Song song, View anchorView) {
        // Opcional: menú contextual
    }
}
