package com.github.emilg1101.app.vkfeed.repository;

import android.content.Context;

import com.github.emilg1101.app.vkfeed.models.Author;
import com.github.emilg1101.app.vkfeed.utils.Serializer;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class AuthorsRepository implements Serializable {

    private static AuthorsRepository instance = new AuthorsRepository();

    private Map<Integer, Author> sourceMap;

    public static AuthorsRepository getInstance() {
        return instance;
    }

    private AuthorsRepository() {
        sourceMap = new HashMap<>();
    }

    public void addAll(Map<Integer, Author> sourceMap) {
        this.sourceMap.putAll(sourceMap);
    }

    public Author get(int id) {
        return sourceMap.get(Math.abs(id));
    }

    public int size() {
        return sourceMap.size();
    }

    public static void load(Context context) {
        instance = Serializer.deserialize(context, instance);
    }

    public static void save(Context context) {
        Serializer.serialize(context, instance);
    }
}