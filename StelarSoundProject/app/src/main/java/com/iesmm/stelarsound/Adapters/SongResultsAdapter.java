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

public class SongResultsAdapter extends RecyclerView.Adapter<SongResultsAdapter.SongViewHolder> {

    private List<Song> songs;
    private final OnSongClickListener onSongClickListener;

    public interface OnSongClickListener {
        void onSongClick(Song song);
        void onPlayClick(Song song);
    }

    public SongResultsAdapter(List<Song> songs, OnSongClickListener listener) {
        this.songs = songs;
        this.onSongClickListener = listener;
    }

    public void updateData(List<Song> newSongs) {
        this.songs = newSongs;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_song_result, parent, false);
        return new SongViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SongViewHolder holder, int position) {
        Song song = songs.get(position);
        holder.bind(song);
    }

    @Override
    public int getItemCount() {
        return songs != null ? songs.size() : 0;
    }

    public class SongViewHolder extends RecyclerView.ViewHolder {
        private final ImageView songCoverImageView;
        private final TextView songTitleTextView;
        private final TextView songArtistTextView;
        private final ImageButton songPlayButton;

        public SongViewHolder(@NonNull View itemView) {
            super(itemView);
            songCoverImageView = itemView.findViewById(R.id.songCoverImageView);
            songTitleTextView = itemView.findViewById(R.id.songTitleTextView);
            songArtistTextView = itemView.findViewById(R.id.songArtistTextView);
            songPlayButton = itemView.findViewById(R.id.songPlayButton);
        }

        public void bind(Song song) {
            songTitleTextView.setText(song.getTitle());
            songArtistTextView.setText(song.getArtist());

            Glide.with(itemView.getContext())
                    .load(song.getCover())
                    .placeholder(R.drawable.ic_music_note)
                    .into(songCoverImageView);

            itemView.setOnClickListener(v -> onSongClickListener.onSongClick(song));
            songPlayButton.setOnClickListener(v -> onSongClickListener.onPlayClick(song));
        }
    }
}