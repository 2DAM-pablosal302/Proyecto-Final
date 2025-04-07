package com.iesmm.stelarsound;

// PlayerController.java
public class PlayerController {
    private Context context;
    private MediaBrowserCompat mediaBrowser;
    private MediaControllerCompat mediaController;
    
    public PlayerController(Context context) {
        this.context = context;
        connectToService();
    }
    
    private void connectToService() {
        mediaBrowser = new MediaBrowserCompat(
            context,
            new ComponentName(context, MusicPlayerService.class),
            new MediaBrowserCompat.ConnectionCallback() {
                @Override
                public void onConnected() {
                    try {
                        mediaController = new MediaControllerCompat(
                            context, mediaBrowser.getSessionToken());
                        MediaControllerCompat.setMediaController(
                            (Activity) context, mediaController);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            },
            null);
        mediaBrowser.connect();
    }
    
    public void playSong(Song song) {
        if (mediaController != null) {
            Bundle extras = new Bundle();
            extras.putString("title", song.getTitle());
            extras.putString("artist", song.getArtist());
            extras.putString("coverUrl", song.getCoverUrl());
            
            mediaController.getTransportControls()
                .playFromUri(Uri.parse(song.getStreamUrl()), extras);
        }
    }
    
    public void disconnect() {
        if (mediaBrowser != null && mediaBrowser.isConnected()) {
            mediaBrowser.disconnect();
        }
    }
}