package com.iesmm.stelarsound;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

// PlayerViewModel.java
public class PlayerViewModel extends ViewModel {
    private MutableLiveData<PlayerState> playerState = new MutableLiveData<>();
    private MusicRepository musicRepository;
    
    public PlayerViewModel(MusicRepository musicRepository) {
        this.musicRepository = musicRepository;
        playerState.setValue(PlayerState.IDLE);
    }
    
    public LiveData<PlayerState> getPlayerState() {
        return playerState;
    }
    
    public void playSong(Song song) {
        playerState.setValue(PlayerState.LOADING);
        musicRepository.playSong(song, new MusicRepository.PlaybackCallback() {
            @Override
            public void onSuccess() {
                playerState.postValue(new PlayerState.PLAYING(song));
            }
            
            @Override
            public void onError(String error) {
                playerState.postValue(new PlayerState.ERROR(error));
            }
        });
    }
    
    public abstract static class PlayerState {
        public static final PlayerState IDLE = new Idle();
        public static final PlayerState LOADING = new Loading();
        
        public static final class PLAYING extends PlayerState {
            public final Song currentSong;
            
            public PLAYING(Song song) {
                this.currentSong = song;
            }
        }
        
        public static final class ERROR extends PlayerState {
            public final String message;
            
            public ERROR(String message) {
                this.message = message;
            }
        }
        
        private static final class Idle extends PlayerState {}
        private static final class Loading extends PlayerState {}
    }
}