package com.iesmm.stelarsound.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.iesmm.stelarsound.Models.Song;
import com.iesmm.stelarsound.R;

import java.util.List;

public class SimpleSongAdapter extends RecyclerView.Adapter<SimpleSongAdapter.SongViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(Song song);
    }

    private final List<Song> songList;
    private final OnItemClickListener listener;

    public SimpleSongAdapter(List<Song> songList, OnItemClickListener listener) {
        this.songList = songList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_simple_song, parent, false);
        return new SongViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SongViewHolder holder, int position) {
        holder.bind(songList.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return songList.size();
    }

    static class SongViewHolder extends RecyclerView.ViewHolder {
        ImageView cover;
        TextView title, artist;

        public SongViewHolder(@NonNull View itemView) {
            super(itemView);
            cover = itemView.findViewById(R.id.songCoverImageView);
            title = itemView.findViewById(R.id.songTitleTextView);
            artist = itemView.findViewById(R.id.songArtistTextView);
        }

        void bind(Song song, OnItemClickListener listener) {
            title.setText(song.getTitle());
            artist.setText(song.getArtist());

            Glide.with(itemView.getContext())
                    .load(song.getCover())
                    .placeholder(R.drawable.ic_music_note)
                    .into(cover);

            itemView.setOnClickListener(v -> listener.onItemClick(song));
        }
    }
}
