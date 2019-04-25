package com.wellav.dolphin.mmedia.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.wellav.dolphin.application.DolphinApp;


public class NetworkUtils {
	
    public static boolean isNetConnect(Context context) {

        ConnectivityManager manager = (ConnectivityManager) DolphinApp
        .getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);

        if (manager == null) {
            return false;
        }

        NetworkInfo networkinfo = manager.getActiveNetworkInfo();

        if (networkinfo == null || !networkinfo.isAvailable()) {
            return false;
        }
        if (networkinfo != null && networkinfo.isConnected()) {
            // 判断当前网络是否已经连接
            if (networkinfo.getState() == NetworkInfo.State.CONNECTED) {
                return true;
            }else{
                return false;
            }
        }
        return false;
    }

}
