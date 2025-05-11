package com.iesmm.stelarsound.Models;

public class Song {
    private String title;
    private String artist;
    private String album;
    private String cover;
    private String audio;

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public String getAlbum() {
        return album;
    }

    public String getCover() {
        return cover;
    }

    public String getAudio() {
        return audio;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public void setAudio(String audio) {
        this.audio = audio;
    }
}
