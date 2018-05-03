package com.github.emilg1101.app.vkfeed.models.attachment;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.JsonObject;

import java.io.Serializable;

public class Photo implements Serializable, Parcelable {

    private String photo;

    public Photo(String photo) {
        this.photo = photo;
    }

    public Photo(Parcel in) {
        photo = in.readString();
    }

    public Photo(JsonObject photo) {
        parse(photo);
    }

    public String getPhoto() {
        return photo;
    }

    private void parse(JsonObject photo) {
        if (photo.has("photo_1280")) {
            this.photo = photo.get("photo_1280").getAsString();
        } else if (photo.has("photo_807")) {
            this.photo = photo.get("photo_807").getAsString();
        } else if (photo.has("photo_604")) {
            this.photo = photo.get("photo_604").getAsString();
        } else if (photo.has("photo_130")) {
            this.photo = photo.get("photo_130").getAsString();
        } else {
            this.photo = photo.get("photo_75").getAsString();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Photo photo1 = (Photo) o;
        return this.photo.equals(photo1.photo);
    }

    @Override
    public int hashCode() {
        return photo != null ? photo.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Photo{" +
                "photo='" + photo + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(photo);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Photo> CREATOR = new Parcelable.Creator<Photo>() {
        @Override
        public Photo createFromParcel(Parcel in) {
            return new Photo(in);
        }

        @Override
        public Photo[] newArray(int size) {
            return new Photo[size];
        }
    };
}