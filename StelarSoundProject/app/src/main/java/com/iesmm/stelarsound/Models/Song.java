package com.iesmm.stelarsound.Models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class Song implements Parcelable {
    private String title;
    private String artist;
    private String album;
    private String cover;
    private String audio;

    public Song() {
    }

    protected Song(Parcel in) {
        title = in.readString();
        artist = in.readString();
        album = in.readString();
        cover = in.readString();
        audio = in.readString();
    }

    public static final Creator<Song> CREATOR = new Creator<Song>() {
        @Override
        public Song createFromParcel(Parcel in) {
            return new Song(in);
        }

        @Override
        public Song[] newArray(int size) {
            return new Song[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(artist);
        dest.writeString(album);
        dest.writeString(cover);
        dest.writeString(audio);
    }
}
