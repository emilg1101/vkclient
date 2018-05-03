package com.github.emilg1101.app.vkfeed.liker;

import com.google.gson.JsonParser;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;

public class Liker {

    public void like(boolean isLiked, int ownerId, int itemId, final LikeListener likeListener) {
        VKRequest vkRequest = new VKRequest(isLiked?"likes.delete":"likes.add",
                VKParameters.from("type", "post", "owner_id", ownerId, "item_id", itemId));
        vkRequest.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                JsonParser parser = new JsonParser();
                int count = parser.parse(response.responseString).getAsJsonObject()
                        .get("response").getAsJsonObject()
                        .get("likes").getAsInt();
                if (likeListener != null) {
                    likeListener.like(count);
                }
            }
        });
    }

    public interface LikeListener {
        void like(int count);
    }
}