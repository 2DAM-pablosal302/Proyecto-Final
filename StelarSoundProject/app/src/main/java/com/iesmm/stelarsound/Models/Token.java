package com.iesmm.stelarsound.Models;

import android.os.Parcel;
import android.os.Parcelable;

public class Token implements Parcelable {
    private String body;

    // Constructor vacío
    public Token() {
    }

    // Constructor completo (opcional)
    public Token(String body) {
        this.body = body;
    }

    // Constructor desde Parcel
    protected Token(Parcel in) {
        body = in.readString();
    }

    public static final Creator<Token> CREATOR = new Creator<Token>() {
        @Override
        public Token createFromParcel(Parcel in) {
            return new Token(in);
        }

        @Override
        public Token[] newArray(int size) {
            return new Token[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(body);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    // Getters y Setters
    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
