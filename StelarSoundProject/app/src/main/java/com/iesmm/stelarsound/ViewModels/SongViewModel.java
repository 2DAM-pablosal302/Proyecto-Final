package com.iesmm.stelarsound.ViewModels;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.iesmm.stelarsound.Models.Song;

import java.util.ArrayList;
import java.util.List;

public class SongViewModel extends ViewModel {
    private final MutableLiveData<Song> currentSong = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isPlaying = new MutableLiveData<>(false);
    private final MutableLiveData<Integer> currentPosition = new MutableLiveData<>(0);
    private final MutableLiveData<Integer> songDuration = new MutableLiveData<>(0);
    private MutableLiveData<List<Song>> queue = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<List<Song>> recentlyPlayed = new MutableLiveData<>(new ArrayList<>());


    public LiveData<List<Song>> getQueue() {
        return queue;
    }
    public LiveData<List<Song>> getRecentlyPlayed() {
        return recentlyPlayed;
    }

    public void addToRecentlyPlayed(Song song) {
        List<Song> currentList = recentlyPlayed.getValue();
        if (currentList == null) {
            currentList = new ArrayList<>();
        }

        List<Song> newList = new ArrayList<>(currentList);

        // Verificar duplicado consecutivo
        if (!newList.isEmpty()) {
            Song last = newList.get(newList.size() - 1);
            boolean isDuplicate = last.getTitle().equals(song.getTitle()) &&
                    last.getArtist().equals(song.getArtist());

            if (isDuplicate) {
                Log.d("SongViewModel", "Canción ignorada por ser duplicada consecutiva");
                return;
            }
        }

        // Eliminar cualquier instancia anterior de la misma canción (por ID, título y artista o lo que prefieras)
        for (int i = 0; i < newList.size(); i++) {
            Song s = newList.get(i);
            if (s.getTitle().equals(song.getTitle()) && s.getArtist().equals(song.getArtist())) {
                newList.remove(i);
                break;
            }
        }

        // Añadir al final
        newList.add(song);

        // Limitar a las últimas 10 canciones
        if (newList.size() > 10) {
            newList.remove(0);
        }

        Log.d("SongViewModel", "Canción añadida a historial: " + song.getTitle());
        recentlyPlayed.setValue(newList); // Notifica a los observers
    }






    public void setQueue(List<Song> newQueue) {
        queue.setValue(newQueue);
    }

    public void addToQueue(Song song) {
        List<Song> updatedQueue = new ArrayList<>(queue.getValue());
        updatedQueue.add(song);
        queue.setValue(updatedQueue);
    }
    public void setCurrentSong(Song song) {
        currentSong.setValue(song);
    }

    public LiveData<Song> getCurrentSong() {
        return currentSong;
    }

    public void setIsPlaying(boolean playing) {
        isPlaying.setValue(playing);
    }

    public LiveData<Boolean> getIsPlaying() {
        return isPlaying;
    }

    public void setCurrentPosition(int position) {
        currentPosition.setValue(position);
    }

    public LiveData<Integer> getCurrentPosition() {
        return currentPosition;
    }

    public void setSongDuration(int duration) {
        songDuration.setValue(duration);
    }

    public LiveData<Integer> getSongDuration() {
        return songDuration;
    }
}
