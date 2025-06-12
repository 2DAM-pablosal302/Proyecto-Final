package com.iesmm.stelarsound.Models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class Playlist implements Parcelable {
    private int id;
    private String name;
    private String creator;
    private String coverUrl;
    private int songCount;
    private List<Song> songs;

    // Constructores
    public Playlist() {
        this.songs = new ArrayList<>();
    }

    public Playlist(int id, String name, String creator, String coverUrl, int songCount) {
        this.id = id;
        this.name = name;
        this.creator = creator;
        this.coverUrl = coverUrl;
        this.songCount = songCount;
        this.songs = new ArrayList<>();
    }



    public Playlist(int id, String name,  String coverUrl, int songCount) {
        this.id = id;
        this.name = name;
        this.coverUrl = coverUrl;
        this.songCount = songCount;
        this.songs = new ArrayList<>();
    }

    protected Playlist(Parcel in) {
        id = in.readInt();
        name = in.readString();
        creator = in.readString();
        coverUrl = in.readString();
        songCount = in.readInt();
        songs = in.createTypedArrayList(Song.CREATOR);
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public int getSongCount() {
        return songCount;
    }

    public void setSongCount(int songCount) {
        this.songCount = songCount;
    }

    public List<Song> getSongs() {
        return songs;
    }

    public void setSongs(List<Song> songs) {
        this.songs = songs;
    }

    // Métodos para manejo de canciones
    public void addSong(Song song) {
        this.songs.add(song);
        this.songCount++;
    }

    public void removeSong(Song song) {
        this.songs.remove(song);
        this.songCount--;
    }

    // Parcelable implementation
    public static final Creator<Playlist> CREATOR = new Creator<Playlist>() {
        @Override
        public Playlist createFromParcel(Parcel in) {
            return new Playlist(in);
        }

        @Override
        public Playlist[] newArray(int size) {
            return new Playlist[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(creator);
        dest.writeString(coverUrl);
        dest.writeInt(songCount);
        dest.writeTypedList(songs);
    }

    @Override
    public String toString() {
        return "Playlist{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", creator='" + creator + '\'' +
                ", songCount=" + songCount +
                '}';
    }
}