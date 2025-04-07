package com.iesmm.stelarsound;

public class SongResponse {
    private String id;
    private String title;
    private String artist;
    // otros campos...
    
    public Song toDomain() {
        return new Song(id, title, artist);
    }
}
