package com.iesmm.stelarsound;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;
import java.util.Locale;

// SongAdapter.java
public class SongAdapter extends RecyclerView.Adapter<SongAdapter.SongViewHolder> {
    private List<Song> songs;
    private OnSongClickListener listener;

    public SongAdapter(List<Song> songs, OnSongClickListener listener) {
        this.songs = songs;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_song, parent, false);
        return new SongViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SongViewHolder holder, int position) {
        Song song = songs.get(position);
        holder.bind(song);
    }

    @Override
    public int getItemCount() {
        return songs.size();
    }

    public void updateSongs(List<Song> newSongs) {
        songs.clear();
        songs.addAll(newSongs);
        notifyDataSetChanged();
    }

    public interface OnSongClickListener {
        void onSongClick(Song song);
    }

    class SongViewHolder extends RecyclerView.ViewHolder {
        private ImageView coverImage;
        private TextView titleText;
        private TextView artistText;
        private TextView durationText;

        public SongViewHolder(@NonNull View itemView) {
            super(itemView);
            coverImage = itemView.findViewById(R.id.song_cover);
            titleText = itemView.findViewById(R.id.song_title);
            artistText = itemView.findViewById(R.id.song_artist);
            durationText = itemView.findViewById(R.id.song_duration);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onSongClick(songs.get(position));
                }
            });
        }

        public void bind(Song song) {
            titleText.setText(song.getTitle());
            artistText.setText(song.getArtist());
            durationText.setText(formatDuration(song.getDuration()));

            Glide.with(itemView.getContext())
                    .load(song.getCoverUrl())
                    .placeholder(R.drawable.ic_music_note)
                    .error(R.drawable.ic_music_note)
                    .into(coverImage);
        }

        private String formatDuration(int durationMs) {
            int seconds = (durationMs / 1000) % 60;
            int minutes = (durationMs / (1000 * 60)) % 60;
            return String.format(Locale.getDefault(), "%d:%02d", minutes, seconds);
        }
    }
}