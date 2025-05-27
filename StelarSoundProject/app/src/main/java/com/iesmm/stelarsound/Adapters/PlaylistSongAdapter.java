package com.iesmm.stelarsound.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.iesmm.stelarsound.Models.Song;
import com.iesmm.stelarsound.R;

import java.util.List;

public class PlaylistSongAdapter extends RecyclerView.Adapter<PlaylistSongAdapter.SongViewHolder> {

    private List<Song> songs;
    private OnSongClickListener listener;

    public interface OnSongClickListener {
        void onSongClick(int position);
        void onOptionsClick(Song song, View anchorView);
    }

    public PlaylistSongAdapter(List<Song> songs, OnSongClickListener listener) {
        this.songs = songs;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_song_playlist, parent, false);
        return new SongViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SongViewHolder holder, int position) {
        Song song = songs.get(position);
        holder.bind(song, position + 1, listener);
    }

    @Override
    public int getItemCount() {
        return songs.size();
    }

    public void updateData(List<Song> newSongs) {
        songs = newSongs;
        notifyDataSetChanged();
    }

    static class SongViewHolder extends RecyclerView.ViewHolder {
        private final ImageView songCover;
        private final TextView songNumber;
        private final TextView songTitle;
        private final TextView songArtist;
        private final ImageButton optionsButton;

        public SongViewHolder(@NonNull View itemView) {
            super(itemView);

            songNumber = itemView.findViewById(R.id.songNumber);
            songCover = itemView.findViewById(R.id.songCoverImageViewDetail);
            songTitle = itemView.findViewById(R.id.songTitle);
            songArtist = itemView.findViewById(R.id.songArtist);
            optionsButton = itemView.findViewById(R.id.optionsButton);
        }

        public void bind(Song song, int position, OnSongClickListener listener) {
            songNumber.setText(String.valueOf(position));
            // Cargar imagen de la canción
            Glide.with(itemView.getContext())
                    .load(song.getCover())
                    .placeholder(R.drawable.ic_music_note)
                    .into(songCover);
            songTitle.setText(song.getTitle());
            songArtist.setText(song.getArtist());

            itemView.setOnClickListener(v -> listener.onSongClick(getAdapterPosition()));
            optionsButton.setOnClickListener(v -> 
                listener.onOptionsClick(song, optionsButton));
        }
    }
}