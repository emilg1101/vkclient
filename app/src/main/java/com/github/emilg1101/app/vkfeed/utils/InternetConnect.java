package com.github.emilg1101.app.vkfeed.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class InternetConnect extends BroadcastReceiver {

    private ConnectivityManager connectivityManager;
    private OnConnectListener onConnectListener;
    private Context context;

    public InternetConnect(Context context) {
        this.context = context;
        connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    public void setOnConnectListener(OnConnectListener onConnectListener) {
        this.onConnectListener = onConnectListener;
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        context.registerReceiver(this, filter);
    }

    public void removeOnConnectListener() {
        context.unregisterReceiver(this);
    }

    public boolean hasConnection() {
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        return activeNetwork != null;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (onConnectListener != null) {
            NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
            onConnectListener.onConnected(activeNetwork != null);
        }
    }

    public interface OnConnectListener {
        void onConnected(boolean connected);
    }
}
