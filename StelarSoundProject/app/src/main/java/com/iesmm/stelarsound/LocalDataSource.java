package com.iesmm.stelarsound;




import static android.os.Looper.getMainLooper;

import android.os.Handler;

import java.util.List;
import java.util.concurrent.Executor;


public class LocalDataSource {
    private SongDao songDao;
    private Executor executor;

    public LocalDataSource(SongDao songDao, Executor executor) {
        this.songDao = songDao;
        this.executor = executor;
    }

    public void cacheSongs(List<Song> songs) {
        executor.execute(() -> {
            songDao.insertAll(songs);
        });
    }

    public void getCachedSongs(SongCallback callback) {
        executor.execute(() -> {
            List<Song> cachedSongs = songDao.getAll();
            new Handler(getMainLooper()).post(() -> {
                if (cachedSongs != null && !cachedSongs.isEmpty()) {
                    callback.onSuccess(cachedSongs);
                } else {
                    callback.onError("No hay datos en caché");
                }
            });
        });
    }

    public interface SongCallback {
        void onSuccess(List<Song> songs);
        void onError(String error);
    }
}