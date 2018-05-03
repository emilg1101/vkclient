package com.github.emilg1101.app.vkfeed.loader;

import com.github.emilg1101.app.vkfeed.models.Author;
import com.github.emilg1101.app.vkfeed.models.Post;

import java.util.List;
import java.util.Map;

public interface OnPostsLoadListener {
    void onPostsLoad(List<Post> posts, Map<Integer, Author> authors, String nextFrom);
}