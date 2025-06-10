package com.iesmm.stelarsound.Models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

public class Song implements Parcelable {
    @SerializedName("id")
    private int id;
    @SerializedName("title")
    private String title;

    @SerializedName("artist")
    private String artist;

    @SerializedName("album")
    private String album;

    @SerializedName("cover_url")  // <-- importante
    private String cover;

    @SerializedName("audio_url")  // <-- importante
    private String audio;

    public Song() {
    }

    protected Song(Parcel in) {
        id = in.readInt();
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

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
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeString(artist);
        dest.writeString(album);
        dest.writeString(cover);
        dest.writeString(audio);
    }

    @Override
    public String toString() {
        return "Song{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", artist='" + artist + '\'' +
                ", album='" + album + '\'' +
                ", cover='" + cover + '\'' +
                ", audio='" + audio + '\'' +
                '}';
    }
}
