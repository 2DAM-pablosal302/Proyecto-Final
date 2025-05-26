package com.iesmm.stelarsound.Views;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.iesmm.stelarsound.Adapters.PlaylistAdapter;
import com.iesmm.stelarsound.Models.Playlist;
import com.iesmm.stelarsound.Models.Token;
import com.iesmm.stelarsound.R;
import com.iesmm.stelarsound.Services.PlaylistService;

import java.util.ArrayList;
import java.util.List;


public class PlaylistFragment extends Fragment implements PlaylistAdapter.OnPlaylistClickListener{

    private RecyclerView playlistsRecyclerView;
    private TextView emptyState, errorState;
    private PlaylistAdapter playlistAdapter;
    private Token token;

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
        // Navegar al detalle de la playlist
        Toast.makeText(getContext(), "Seleccionada: " + playlist.getName(), Toast.LENGTH_SHORT).show();
        // Aquí deberías implementar la navegación al fragment/activity de detalle
        Intent intent = new Intent(getActivity(), PlaylistDetailActivity.class);
        intent.putExtra("playlist_id", playlist.getId());
        intent.putExtra("auth_token", token.getBody());
        startActivity(intent);
    }

    @Override
    public void onPlaylistOptionsClick(Playlist playlist, View anchorView) {
        // Mostrar menú de opciones para la playlist
        // Implementa esto según tus necesidades
    }

}