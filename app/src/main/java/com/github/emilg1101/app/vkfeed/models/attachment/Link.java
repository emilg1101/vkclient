package com.github.emilg1101.app.vkfeed.models.attachment;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.JsonObject;

import java.io.Serializable;

public class Link implements Serializable, Parcelable {

    private String url;
    private String title;
    private String caption;
    private String description;
    private Photo photo;

    public Link(String url, String title, String caption, String description, Photo photo) {
        this.url = url;
        this.title = title;
        this.caption = caption;
        this.description = description;
        this.photo = photo;
    }

    public Link(JsonObject link) {
        parse(link);
    }

    private void parse(JsonObject link) {
        if (link.has("url")) {
            this.url = link.get("url").getAsString();
        }

        if (link.has("title")) {
            this.title = link.get("title").getAsString();
        }

        if (link.has("caption")) {
            this.caption = link.get("caption").getAsString();
        }

        if (link.has("description")) {
            this.description = link.get("description").getAsString();
        }

        if (link.has("photo")) {
            this.photo = new Photo(link.get("photo").getAsJsonObject());
        }
    }

    public String getUrl() {
        return url;
    }

    public String getTitle() {
        return title;
    }

    public String getCaption() {
        return caption;
    }

    public String getDescription() {
        return description;
    }

    public Photo getPhoto() {
        return photo;
    }

    @Override
    public String toString() {
        return "Link{" +
                "url='" + url + '\'' +
                ", title='" + title + '\'' +
                ", caption='" + caption + '\'' +
                ", description='" + description + '\'' +
                ", photo=" + photo +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Link link = (Link) o;

        if (url != null ? !url.equals(link.url) : link.url != null) return false;
        if (title != null ? !title.equals(link.title) : link.title != null) return false;
        if (caption != null ? !caption.equals(link.caption) : link.caption != null) return false;
        if (description != null ? !description.equals(link.description) : link.description != null)
            return false;
        return photo != null ? photo.equals(link.photo) : link.photo == null;
    }

    @Override
    public int hashCode() {
        int result = url != null ? url.hashCode() : 0;
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (caption != null ? caption.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (photo != null ? photo.hashCode() : 0);
        return result;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }
}
