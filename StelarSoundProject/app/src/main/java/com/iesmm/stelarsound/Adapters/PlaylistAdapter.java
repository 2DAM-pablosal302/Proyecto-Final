package com.iesmm.stelarsound.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.iesmm.stelarsound.Models.Playlist;
import com.iesmm.stelarsound.R;

import java.util.ArrayList;
import java.util.List;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.PlaylistViewHolder> {

    private List<Playlist> playlists;
    private final OnPlaylistClickListener listener;

    public interface OnPlaylistClickListener {
        void onPlaylistClick(Playlist playlist);
        void onPlaylistOptionsClick(Playlist playlist, View anchorView);
        void onPlaylistDeleteClick(Playlist playlist, int position);
    }

    public PlaylistAdapter(List<Playlist> playlists, OnPlaylistClickListener listener) {
        this.playlists = playlists;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PlaylistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_playlist, parent, false);
        return new PlaylistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaylistViewHolder holder, int position) {
        Playlist playlist = playlists.get(position);
        holder.bind(playlist, listener);

        holder.btnDeletePlaylist.setOnClickListener(v -> {
            if (listener != null) {
                listener.onPlaylistDeleteClick(playlist, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return playlists.size();
    }

    public void updateData(List<Playlist> newPlaylists) {
        playlists.clear();
        playlists.addAll(newPlaylists);
        notifyDataSetChanged();
    }

    static class PlaylistViewHolder extends RecyclerView.ViewHolder {
        private final TextView nameTextView;
        private final TextView creatorTextView;
        private final TextView songCountTextView;
        private final ImageView coverImageView;
        private final ImageView optionsImageView;
        public ImageView btnDeletePlaylist;

        public PlaylistViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.playlistName);
            creatorTextView = itemView.findViewById(R.id.playlistCreator);
            songCountTextView = itemView.findViewById(R.id.songCount);
            coverImageView = itemView.findViewById(R.id.playlistCover);
            optionsImageView = itemView.findViewById(R.id.optionsButton);
            btnDeletePlaylist = itemView.findViewById(R.id.btnDeletePlaylist);
        }

        public void bind(Playlist playlist, OnPlaylistClickListener listener) {
            nameTextView.setText(playlist.getName());
            creatorTextView.setText("Creada por: " + playlist.getCreator());
            songCountTextView.setText(playlist.getSongCount() + " canciones");

            // Aquí deberías cargar la imagen de la playlist usando Glide/Picasso
            Glide.with(itemView.getContext()).load(playlist.getCoverUrl()).into(coverImageView);

            itemView.setOnClickListener(v -> listener.onPlaylistClick(playlist));
            optionsImageView.setOnClickListener(v -> 
                listener.onPlaylistOptionsClick(playlist, optionsImageView));
        }

    }

    public void removeItem(int position) {
        playlists.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, playlists.size());
    }
}