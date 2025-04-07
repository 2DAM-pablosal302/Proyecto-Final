package com.iesmm.stelarsound;

import static com.google.common.reflect.Reflection.getPackageName;

import android.media.browse.MediaBrowser;
import android.net.Uri;
import android.os.Bundle;
import android.service.media.MediaBrowserService;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.media3.common.AudioAttributes;
import androidx.media3.common.MediaItem;
import androidx.media3.common.MediaMetadata;
import androidx.media3.exoplayer.ExoPlayer;

import java.util.List;

// MusicPlayerService.java
public class MusicPlayerService extends MediaBrowserService {
    private ExoPlayer exoPlayer;
    
    @Override
    public void onCreate() {
        super.onCreate();
        initializePlayer();
    }
    
    private void initializePlayer() {
        exoPlayer = new ExoPlayer.Builder(this)
            .setAudioAttributes(AudioAttributes.DEFAULT, true)
            .setHandleAudioBecomingNoisy(true)
            .build();
    }
    
    public void playSong(Song song) {
        MediaItem mediaItem = new MediaItem.Builder()
            .setUri(song.getStreamUrl())
            .setMediaMetadata(new MediaMetadata.Builder()
                .setTitle(song.getTitle())
                .setArtist(song.getArtist())
                .setAlbumTitle(song.getAlbum())
                .setArtworkUri(Uri.parse(song.getCoverUrl()))
                .build())
            .build();
        
        exoPlayer.setMediaItem(mediaItem);
        exoPlayer.prepare();
        exoPlayer.play();
    }
    
    @Nullable
    @Override
    public MediaBrowserService.BrowserRoot onGetRoot(@NonNull String clientPackageName, int clientUid,
                                                     @Nullable Bundle rootHints) {
        if (getPackageName().equals(clientPackageName)) {
            return new MediaBrowserService.BrowserRoot("root", null);
        }
        return null;
    }


    @Override
    public void onLoadChildren(@NonNull String parentId, 
                              @NonNull Result<List<MediaBrowser.MediaItem>> result) {
        result.sendResult(null);
    }
    
    @Override
    public void onDestroy() {
        releasePlayer();
        super.onDestroy();
    }
    
    private void releasePlayer() {
        if (exoPlayer != null) {
            exoPlayer.release();
            exoPlayer = null;
        }
    }
}