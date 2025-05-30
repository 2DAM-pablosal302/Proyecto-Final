package com.iesmm.stelarsound.Adapters;

import android.content.Context;
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

public class CancionAdapter extends RecyclerView.Adapter<CancionAdapter.ViewHolder> {
    private Context context;
    private List<Song> canciones;
    private OnPlayButtonClickListener playButtonClickListener;
    private int currentlyPlayingPosition = -1;

    // Interfaz simplificada solo para el botón de play
    public interface OnPlayButtonClickListener {
        void onPlayButtonClick(int position, Song song);
    }

    public void setPlayButtonClickListener(OnPlayButtonClickListener listener) {
        this.playButtonClickListener = listener;
    }
    public CancionAdapter(Context context, List<Song> canciones) {
        this.context = context;
        this.canciones = canciones;

        if (context instanceof OnPlayButtonClickListener) {
            this.playButtonClickListener = (OnPlayButtonClickListener) context;
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_song, parent, false);
        return new ViewHolder(v);
    }

    public List<Song> getCanciones() {
        return canciones;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Song cancion = canciones.get(position);

        holder.titulo.setText(cancion.getTitle());
        holder.artista.setText(cancion.getArtist());
        holder.album.setText(cancion.getAlbum());
        Glide.with(context).load(cancion.getCover()).into(holder.portada);

        // Actualizar el estado del botón play/pause
        if (position == currentlyPlayingPosition) {
            holder.botonPlay.setImageResource(R.drawable.ic_pause_circle);
        } else {
            holder.botonPlay.setImageResource(R.drawable.ic_play_circle);
        }

        holder.botonPlay.setOnClickListener(v -> {
            if (playButtonClickListener != null) {
                playButtonClickListener.onPlayButtonClick(position, cancion);
            }
        });
    }

    @Override
    public int getItemCount() {
        return canciones.size();
    }

    // Metodo para actualizar la posición de la canción que se está reproduciendo
    public void setCurrentlyPlayingPosition(int position) {
        int previousPosition = currentlyPlayingPosition;
        currentlyPlayingPosition = position;

        // Notificar cambios para actualizar los botones
        if (previousPosition != -1) {
            notifyItemChanged(previousPosition);
        }
        if (currentlyPlayingPosition != -1) {
            notifyItemChanged(currentlyPlayingPosition);
        }
    }

    public int getCurrentlyPlayingPosition(){
        return currentlyPlayingPosition;
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView titulo, artista, album;
        ImageView portada, botonPlay;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titulo = itemView.findViewById(R.id.textTitulo);
            artista = itemView.findViewById(R.id.textArtista);
            album = itemView.findViewById(R.id.songAlbum);
            portada = itemView.findViewById(R.id.imagenPortada);
            botonPlay = itemView.findViewById(R.id.play_button);
        }
    }

    public void setCanciones(List<Song> nuevasCanciones) {
        this.canciones = nuevasCanciones;
        notifyDataSetChanged();
    }

}