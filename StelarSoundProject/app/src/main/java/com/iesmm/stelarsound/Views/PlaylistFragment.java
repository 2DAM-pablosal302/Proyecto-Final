package com.iesmm.stelarsound.Views;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.iesmm.stelarsound.Adapters.PlaylistAdapter;
import com.iesmm.stelarsound.MainActivity;
import com.iesmm.stelarsound.Models.Playlist;
import com.iesmm.stelarsound.Models.Song;
import com.iesmm.stelarsound.Models.Token;
import com.iesmm.stelarsound.R;
import com.iesmm.stelarsound.Services.PlaylistService;
import com.iesmm.stelarsound.Services.SongService;
import com.iesmm.stelarsound.ViewModels.SongViewModel;

import java.util.ArrayList;
import java.util.List;


public class PlaylistFragment extends Fragment implements PlaylistAdapter.OnPlaylistClickListener{

    private RecyclerView playlistsRecyclerView;
    private TextView emptyState, errorState;
    private PlaylistAdapter playlistAdapter;
    private Token token;
    private FloatingActionButton addPlaylistBtn;
    private ImageButton btnDeletePlaylist;


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        Bundle bundle = getActivity().getIntent().getExtras();
        if (bundle != null) {
            token = bundle.getParcelable("token");
        }else{
            Log.e("Token ERROR", "Token no recibido");
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_playlist, container, false);

        playlistsRecyclerView = view.findViewById(R.id.playlistsRecyclerView);

        // Start views
        emptyState = view.findViewById(R.id.emptyState);
        errorState = view.findViewById(R.id.errorState);

        // Adapter configurations
        playlistAdapter = new PlaylistAdapter(new ArrayList<>(), this);
        playlistsRecyclerView.setAdapter(playlistAdapter);

        addPlaylistBtn = view.findViewById(R.id.addButton);
        addPlaylistBtn.setOnClickListener(v -> showCreatePlaylistDialog());


        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadPlaylists();
    }

    private void loadPlaylists(){
        PlaylistService.getUserPlaylists(requireContext(), token.getBody(), new PlaylistService.PlaylistCallback() {
            @Override
            public void onSuccess(List<Playlist> playlists) {
                requireActivity().runOnUiThread(() -> {
                    hideLoading();
                    if (playlists.isEmpty()) {
                        showEmptyState();
                    } else {
                        showContent();
                        playlistAdapter.updateData(playlists);
                    }
                });
            }

            @Override
            public void onError(String message) {
                requireActivity().runOnUiThread(() -> {
                    hideLoading();
                    showErrorState();
                    Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    // View States
    private void showLoading() {
        playlistsRecyclerView.setVisibility(View.GONE);
        emptyState.setVisibility(View.GONE);
        errorState.setVisibility(View.GONE);
        // Aquí podrías mostrar un ProgressBar si lo agregas al layout
    }

    private void hideLoading() {
        // Ocultar ProgressBar si lo estás usando
    }

    private void showContent() {
        playlistsRecyclerView.setVisibility(View.VISIBLE);
        emptyState.setVisibility(View.GONE);
        errorState.setVisibility(View.GONE);
    }

    private void showEmptyState() {
        playlistsRecyclerView.setVisibility(View.GONE);
        emptyState.setVisibility(View.VISIBLE);
        errorState.setVisibility(View.GONE);
    }

    private void showErrorState() {
        playlistsRecyclerView.setVisibility(View.GONE);
        emptyState.setVisibility(View.GONE);
        errorState.setVisibility(View.VISIBLE);
    }

    @Override
    public void onPlaylistClick(Playlist playlist) {
        PlaylistDetailFragment detailFragment = PlaylistDetailFragment.newInstance(playlist.getId(), token.getBody());
        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, detailFragment)
                .addToBackStack(null)
                .commit();
    }


    @Override
    public void onPlaylistOptionsClick(Playlist playlist, View anchorView) {
        PlaylistService.getPlaylistSongs(requireContext(), token.getBody(), playlist.getId(), new PlaylistService.SongListCallback() {
            @Override
            public void onSuccess(List<Song> songs) {
                if (songs.isEmpty()) {
                    Toast.makeText(getContext(), "La playlist está vacía", Toast.LENGTH_SHORT).show();
                    return;
                }

                Song firstSong = songs.get(0);
                List<Song> queue = new ArrayList<>(songs);
                queue.remove(0); // quitamos la primera, que ya va a reproducirse

                MainActivity activity = (MainActivity) getActivity();
                if (activity != null) {
                    try {
                        activity.mediaPlayer.reset();
                        activity.mediaPlayer.setDataSource(firstSong.getAudio());
                        activity.mediaPlayer.prepare();
                        activity.mediaPlayer.start();

                        SongViewModel songViewModel = new ViewModelProvider(requireActivity()).get(SongViewModel.class);
                        songViewModel.setCurrentSong(firstSong);
                        songViewModel.setIsPlaying(true);
                        songViewModel.setSongDuration(activity.mediaPlayer.getDuration());
                        songViewModel.setQueue(queue);

                        activity.getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container, new PlayFragment())
                                .addToBackStack(null)
                                .commit();
                        activity.getBottomNav().setSelectedItemId(R.id.nav_play);

                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), "Error al reproducir la playlist "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.d("ERROR PLAYLIST", e.getMessage());
                        Log.e("AUDIO_URL", "URL del audio: " + firstSong.getAudio());

                    }
                }
            }

            @Override
            public void onError(String message) {
                Toast.makeText(getContext(), "Error al cargar canciones de la playlist", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private static final int PICK_IMAGE_REQUEST_CODE = 1001;
    private Uri selectedCoverUri = null;

    private void showCreatePlaylistDialog() {
        // Inflamos un layout personalizado para el diálogo
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_create_playlist, null);

        EditText input = dialogView.findViewById(R.id.editTextPlaylistName);
        ImageView coverImage = dialogView.findViewById(R.id.imageViewCover);

        coverImage.setOnClickListener(v -> {
            // Abrir selector de imágenes
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, PICK_IMAGE_REQUEST_CODE);
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Nueva Playlist");
        builder.setView(dialogView);

        builder.setPositiveButton("Crear", (dialog, which) -> {
            String name = input.getText().toString().trim();
            if (!name.isEmpty()) {
                // Pasamos la URI en string o null si no se seleccionó imagen
                createPlaylist(name, selectedCoverUri != null ? selectedCoverUri.toString() : null);
            } else {
                Toast.makeText(getContext(), "El nombre no puede estar vacío", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());

        builder.show();
    }

    // Recibimos el resultado del selector de imágenes
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            selectedCoverUri = data.getData();
            // Actualizar la imagen en el diálogo (si está visible)
            // Para eso necesitarías guardar referencia al ImageView o usar un campo global, o abrir un nuevo diálogo
            // Por simplicidad puedes refrescar el dialogo o usar otro mecanismo
        }
    }


    private void createPlaylist(String name, String coverUri) {
        PlaylistService.createPlaylist(requireContext(), token.getBody(), name, coverUri, new PlaylistService.PlaylistDetailCallback() {
            @Override
            public void onSuccess(Playlist playlist) {
                Toast.makeText(getContext(), "Playlist creada: " + playlist.getName(), Toast.LENGTH_SHORT).show();
                loadPlaylists();
            }

            @Override
            public void onError(String message) {
                Toast.makeText(getContext(), "Error al crear playlist: " + message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onPlaylistDeleteClick(Playlist playlist, int position) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Eliminar playlist")
                .setMessage("¿Estás seguro de que quieres eliminar '" + playlist.getName() + "'?")
                .setPositiveButton("Eliminar", (dialog, which) -> {
                    deletePlaylist(playlist.getId(), position);
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void deletePlaylist(int playlistId, int position) {
        PlaylistService.deletePlaylist(requireContext(), token.getBody(), playlistId,
                new PlaylistService.PlaylistDeleteCallback() {
                    @Override
                    public void onSuccess(String message) {
                        requireActivity().runOnUiThread(() -> {
                            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                            // Eliminar de la lista local y actualizar el RecyclerView
                            playlistAdapter.removeItem(position);

                            // Mostrar estado vacío si es necesario
                            if (playlistAdapter.getItemCount() == 0) {
                                showEmptyState();
                            }
                        });
                    }

                    @Override
                    public void onError(String error) {
                        requireActivity().runOnUiThread(() -> {
                            Toast.makeText(getContext(), error, Toast.LENGTH_LONG).show();
                        });
                    }
                });
    }





}