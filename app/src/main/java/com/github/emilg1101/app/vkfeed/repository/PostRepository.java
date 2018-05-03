package com.github.emilg1101.app.vkfeed.repository;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.github.emilg1101.app.vkfeed.models.Post;
import com.github.emilg1101.app.vkfeed.utils.Serializer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PostRepository implements Iterable<Post>, Serializable, Parcelable {
    private static PostRepository instance = new PostRepository();

    private List<Post> posts;
    private String startFrom;

    public static PostRepository getInstance() {
        return instance;
    }

    private PostRepository() {
        posts = new ArrayList<>();
        startFrom = "";
    }

    public static void load(Context context) {
        instance = Serializer.deserialize(context, instance);
    }

    public static void save(Context context) {
        Serializer.serialize(context, instance);
    }

    public void add(Post post) {
        posts.add(post);
    }

    public void addAll(List<Post> posts) {
        this.posts.addAll(posts);
    }

    public Post get(int index) {
        return posts.get(index);
    }

    public void remove(int index) {
        posts.remove(index);
    }

    public void clear() {
        posts.clear();
    }

    public int size() {
        return posts.size();
    }

    public String getStartFrom() {
        return startFrom;
    }

    public void setStartFrom(String startFrom) {
        this.startFrom = startFrom;
    }

    @NonNull
    @Override
    public Iterator<Post> iterator() {
        return posts.iterator();
    }

    protected PostRepository(Parcel in) {
        if (in.readByte() == 0x01) {
            posts = new ArrayList<Post>();
            in.readList(posts, Post.class.getClassLoader());
        } else {
            posts = null;
        }
        startFrom = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (posts == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(posts);
        }
        dest.writeString(startFrom);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<PostRepository> CREATOR = new Parcelable.Creator<PostRepository>() {
        @Override
        public PostRepository createFromParcel(Parcel in) {
            return new PostRepository(in);
        }

        @Override
        public PostRepository[] newArray(int size) {
            return new PostRepository[size];
        }
    };
}