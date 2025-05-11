package com.iesmm.stelarsound.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.iesmm.stelarsound.Models.Song;
import com.iesmm.stelarsound.R;

import java.util.List;

public class CancionAdapter extends RecyclerView.Adapter<CancionAdapter.ViewHolder> {
    private Context context;
    private List<Song> canciones;

    public CancionAdapter(Context context, List<Song> canciones) {
        this.context = context;
        this.canciones = canciones;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_song, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Song cancion = canciones.get(position);
        holder.titulo.setText(cancion.getTitle());
        holder.artista.setText(cancion.getArtist());
        Glide.with(context).load(cancion.getCover()).into(holder.portada);
    }

    @Override
    public int getItemCount() {
        return canciones.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView titulo, artista;
        ImageView portada;

        public ViewHolder(View itemView) {
            super(itemView);
            titulo = itemView.findViewById(R.id.textTitulo);
            artista = itemView.findViewById(R.id.textArtista);
            portada = itemView.findViewById(R.id.imagenPortada);
        }
    }
}
