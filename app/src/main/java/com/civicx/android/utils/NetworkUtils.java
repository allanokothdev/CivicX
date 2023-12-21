package com.civicx.android.utils;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;

public class NetworkUtils extends Application {

    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

}
