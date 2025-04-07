package com.iesmm.stelarsound;

import java.util.List;

// MusicRepository.java
public class MusicRepository {
    private LocalDataSource localDataSource;
    private RemoteDataSource remoteDataSource;
    
    public MusicRepository(LocalDataSource localDataSource, 
                         RemoteDataSource remoteDataSource) {
        this.localDataSource = localDataSource;
        this.remoteDataSource = remoteDataSource;
    }
    
    public void searchSongs(String query, SearchCallback callback) {
        remoteDataSource.searchSongs(query, new RemoteDataSource.SongCallback() {
            @Override
            public void onSuccess(List<Song> songs) {
                localDataSource.cacheSongs(songs);
                callback.onSuccess(songs);
            }
            
            @Override
            public void onError(String error) {
                localDataSource.getCachedSongs(new LocalDataSource.SongCallback() {
                    @Override
                    public void onSuccess(List<Song> songs) {
                        callback.onSuccess(songs);
                    }
                    
                    @Override
                    public void onError(String error) {
                        callback.onError(error);
                    }
                });
            }
        });
    }
    
    public void playSong(Song song, PlaybackCallback callback) {
        remoteDataSource.getStreamUrl(song.getId(), new RemoteDataSource.StreamCallback() {
            @Override
            public void onSuccess(String streamUrl) {
                song.setStreamUrl(streamUrl);
                // Iniciar reproducción
                callback.onSuccess();
            }
            
            @Override
            public void onError(String error) {
                callback.onError(error);
            }
        });
    }
    
    public interface SearchCallback {
        void onSuccess(List<Song> songs);
        void onError(String error);
    }
    
    public interface PlaybackCallback {
        void onSuccess();
        void onError(String error);
    }
}