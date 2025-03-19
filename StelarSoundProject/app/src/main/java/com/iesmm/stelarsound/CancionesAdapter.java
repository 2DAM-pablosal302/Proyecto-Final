package com.iesmm.stelarsound;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.squareup.picasso.Picasso;
import java.util.List;

import android.media.MediaPlayer;
import android.widget.Button;
import java.io.IOException;



public class CancionesAdapter extends RecyclerView.Adapter<CancionesAdapter.CancionViewHolder> {
    private Context context;
    private List<AudiusTrack> listaCanciones;
    private MediaPlayer mediaPlayer;

    public CancionesAdapter(Context context, List<AudiusTrack> listaCanciones) {
        this.context = context;
        this.listaCanciones = listaCanciones;
        this.mediaPlayer = new MediaPlayer();
    }

    @NonNull
    @Override
    public CancionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cancion, parent, false);
        return new CancionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CancionViewHolder holder, int position) {
        AudiusTrack cancion = listaCanciones.get(position);
        holder.txtTitulo.setText(cancion.title);
        holder.txtArtista.setText(cancion.user.name);
        Picasso.get().load(cancion.artwork).into(holder.imgAlbum);

        holder.btnPlay.setOnClickListener(v -> {
            try {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                    mediaPlayer.reset();
                }
                mediaPlayer.setDataSource(cancion.stream_url);
                mediaPlayer.prepareAsync();
                mediaPlayer.setOnPreparedListener(MediaPlayer::start);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public int getItemCount() {
        return listaCanciones.size();
    }

    public static class CancionViewHolder extends RecyclerView.ViewHolder {
        TextView txtTitulo, txtArtista;
        ImageView imgAlbum;
        Button btnPlay;

        public CancionViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTitulo = itemView.findViewById(R.id.txtTitulo);
            txtArtista = itemView.findViewById(R.id.txtArtista);
            imgAlbum = itemView.findViewById(R.id.imgAlbum);
            btnPlay = itemView.findViewById(R.id.btnPlay);
        }
    }
}