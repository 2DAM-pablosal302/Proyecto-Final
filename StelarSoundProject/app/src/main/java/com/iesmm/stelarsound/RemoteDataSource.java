package com.iesmm.stelarsound;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Response;

public class RemoteDataSource {
    private MusicApiService apiService;

    public RemoteDataSource(MusicApiService apiService) {
        this.apiService = apiService;
    }

    public void searchSongs(String query, SongCallback callback) {
        new Thread(() -> {
            try {
                Response<ApiResponse<List<SongResponse>>> response = 
                    apiService.searchSongs(query).execute();
                
                if (response.isSuccessful() && response.body() != null) {
                    List<Song> songs = new ArrayList<>();
                    for (SongResponse songResponse : response.body().getData()) {
                        songs.add(songResponse.toDomain());
                    }
                    callback.onSuccess(songs);
                } else {
                    callback.onError("Error en la respuesta: " + response.message());
                }
            } catch (IOException e) {
                callback.onError("Error de red: " + e.getMessage());
            }
        }).start();
    }

    public void getStreamUrl(String songId, StreamCallback callback) {
        new Thread(() -> {
            try {
                Response<StreamResponse> response =
                    apiService.getStreamUrl(songId).execute();
                
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body().getUrl());
                } else {
                    callback.onError("Error al obtener URL de stream");
                }
            } catch (IOException e) {
                callback.onError("Error de red: " + e.getMessage());
            }
        }).start();
    }

    public interface SongCallback {
        void onSuccess(List<Song> songs);
        void onError(String error);
    }

    public interface StreamCallback {
        void onSuccess(String streamUrl);
        void onError(String error);
    }
}