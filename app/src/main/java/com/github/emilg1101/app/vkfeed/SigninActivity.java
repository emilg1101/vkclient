package com.github.emilg1101.app.vkfeed;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCallback;
import com.vk.sdk.VKScope;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKError;

public class SigninActivity extends AppCompatActivity {

    private String[] SCOPES = new String[] {
            VKScope.FRIENDS,
            VKScope.WALL
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_signin);
    }

    public void signin(View view) {
        VKSdk.login(this, SCOPES);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!VKSdk.onActivityResult(requestCode, resultCode, data, new VKCallback<VKAccessToken>() {
                @Override
                public void onResult(VKAccessToken res) {
                    startActivity(new Intent(SigninActivity.this, MainActivity.class));
                    finish();
                }
                @Override
                public void onError(VKError error) {

                }
        }
        )) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}