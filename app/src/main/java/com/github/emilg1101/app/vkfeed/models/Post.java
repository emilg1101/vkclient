package com.github.emilg1101.app.vkfeed.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.github.emilg1101.app.vkfeed.models.attachment.Photo;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Post implements Serializable, Parcelable {

    private int sourceId;
    private long date;
    private int postId;
    private String text;
    private int likesCount;
    private boolean userLikes;

    private List<Photo> photos;

    public Post(JsonObject post) {
        parse(post);
    }

    public Post(Parcel in) {
        sourceId = in.readInt();
        date = in.readLong();
        postId = in.readInt();
        text = in.readString();
        likesCount = in.readInt();
        userLikes = in.readByte() != 0x00;
        if (in.readByte() == 0x01) {
            photos = new ArrayList<Photo>();
            in.readList(photos, Photo.class.getClassLoader());
        } else {
            photos = null;
        }
    }

    private void parse(JsonObject post) {
        this.sourceId = post.get("source_id").getAsInt();
        this.date = post.get("date").getAsLong();
        this.postId = post.get("post_id").getAsInt();
        this.text = post.get("text").getAsString();
        this.photos = new ArrayList<>();
        this.likesCount = post.get("likes").getAsJsonObject().get("count").getAsInt();
        this.userLikes = post.get("likes").getAsJsonObject().get("user_likes").getAsInt() != 0;

        if (post.has("attachments")) {
            JsonArray attachments = post.get("attachments").getAsJsonArray();
            for (JsonElement attachment : attachments) {
                JsonObject object = attachment.getAsJsonObject();
                String type = object.get("type").getAsString();
                if (type.equals("photo")) {
                    photos.add(new Photo(object.get("photo").getAsJsonObject()));
                }
            }
        }
    }

    public int getSourceId() {
        return sourceId;
    }

    public long getDate() {
        return date;
    }

    public int getPostId() {
        return postId;
    }

    public String getText() {
        return text;
    }

    public int getLikesCount() {
        return likesCount;
    }

    public boolean isUserLikes() {
        return userLikes;
    }

    public List<Photo> getPhotos() {
        return photos;
    }

    public void setUserLikes(boolean userLikes) {
        this.userLikes = userLikes;
    }

    @Override
    public String toString() {
        return "Post{" +
                "sourceId=" + sourceId +
                ", date=" + date +
                ", postId=" + postId +
                ", text='" + text + '\'' +
                ", likesCount=" + likesCount +
                ", userLikes=" + userLikes +
                ", photos=" + photos +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(sourceId);
        dest.writeLong(date);
        dest.writeInt(postId);
        dest.writeString(text);
        dest.writeInt(likesCount);
        dest.writeByte((byte) (userLikes ? 0x01 : 0x00));
        if (photos == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(photos);
        }
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Post> CREATOR = new Parcelable.Creator<Post>() {
        @Override
        public Post createFromParcel(Parcel in) {
            return new Post(in);
        }

        @Override
        public Post[] newArray(int size) {
            return new Post[size];
        }
    };
}