package com.iesmm.stelarsound.ViewModels;

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

    public LiveData<List<Song>> getQueue() {
        return queue;
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
