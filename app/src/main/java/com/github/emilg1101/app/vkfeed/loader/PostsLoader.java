package com.github.emilg1101.app.vkfeed.loader;

import com.github.emilg1101.app.vkfeed.models.Author;
import com.github.emilg1101.app.vkfeed.models.Post;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PostsLoader {

    private OnPostsLoadListener onPostsLoadListener;

    public PostsLoader() {

    }

    public void load(String startFrom) {
        VKRequest vkRequest = new VKRequest("newsfeed.get", VKParameters.from(VKApiConst.FILTERS, "post", VKApiConst.COUNT, 10, "start_from", startFrom));
        vkRequest.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                List<Post> posts = new ArrayList<>();
                Map<Integer, Author> sourceMap = new HashMap<>();
                JsonParser parser = new JsonParser();
                JsonObject responseData = parser.parse(response.responseString).getAsJsonObject().get("response").getAsJsonObject();
                JsonArray postsData = responseData.get("items").getAsJsonArray();
                for (JsonElement element : postsData) {
                    JsonObject object = element.getAsJsonObject();
                    posts.add(new Post(object));
                }
                sourceMap.putAll(toMap(responseData.get("profiles").getAsJsonArray()));
                sourceMap.putAll(toMap(responseData.get("groups").getAsJsonArray()));
                String nextFrom = responseData.get("next_from").getAsString();
                if (onPostsLoadListener != null) {
                    onPostsLoadListener.onPostsLoad(posts, sourceMap, nextFrom);
                }
            }

            @Override
            public void onError(VKError error) {
                if (onPostsLoadListener != null) {
                    //onPostsLoadListener.onError(error.errorMessage);
                }
            }
        });
    }

    private Map<Integer, Author> toMap(JsonArray array) {
        Map<Integer, Author> sourceMap = new HashMap<>();
        for (JsonElement element : array) {
            JsonObject object = element.getAsJsonObject();
            int id = object.get("id").getAsInt();
            String name = "";
            if (object.has("name")) {
                name = object.get("name").getAsString();
            } else {
                name = object.get("first_name").getAsString() + object.get("last_name").getAsString();
            }
            String photo = object.get("photo_100").getAsString();
            sourceMap.put(id, new Author(name, photo));
        }
        return sourceMap;
    }

    public void setOnPostsLoadListener(OnPostsLoadListener onPostsLoadListener) {
        this.onPostsLoadListener = onPostsLoadListener;
    }
}