package com.iesmm.stelarsound;

import androidx.room.Dao;
import androidx.room.Entity;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.PrimaryKey;
import androidx.room.Query;

import java.util.List;

// SongDao.java
@Dao
public interface SongDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Song> songs);

    @Query("SELECT * FROM songs")
    List<Song> getAll();

    @Query("SELECT * FROM songs WHERE title LIKE :query OR artist LIKE :query")
    List<Song> search(String query);
}

// Song.java (con anotaciones Room)
@Entity(tableName = "songs")
public class Song {
    @PrimaryKey
    @NonNull
    private String id;
    private String title;
    private String artist;
    private String album;
    private String coverUrl;
    private String streamUrl;
    private int duration;

    public Song(@NonNull String id, String title, String artist, String album,
                String coverUrl, String streamUrl, int duration) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.coverUrl = coverUrl;
        this.streamUrl = streamUrl;
        this.duration = duration;
    }

    // Getters
    @NonNull
    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getArtist() { return artist; }
    public String getAlbum() { return album; }
    public String getCoverUrl() { return coverUrl; }
    public String getStreamUrl() { return streamUrl; }
    public int getDuration() { return duration; }

    // Setters (necesarios para Room)
    public void setId(@NonNull String id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setArtist(String artist) { this.artist = artist; }
    public void setAlbum(String album) { this.album = album; }
    public void setCoverUrl(String coverUrl) { this.coverUrl = coverUrl; }
    public void setStreamUrl(String streamUrl) { this.streamUrl = streamUrl; }
    public void setDuration(int duration) { this.duration = duration; }
}