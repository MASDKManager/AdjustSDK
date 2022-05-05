package com.ma.autosdk.Util;


import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Base64;


import java.net.URLEncoder;
import java.util.Date;
import java.util.UUID;

public class Constants {

    public static final String KEY_PREFERENCE = "livecameratranslator";
//    public static final String KEY_MAIN_POINT = "";
    public static final String KEY_USER_UUID = "user_uuid";
    public static final String KEY_CONFIG_VALUE = "config_value";
    public static final String KEY_ADJUST_ATTRIBUTES = "adjust_attribute";
    public static boolean showAds = true;

    public static String generateUserUUID(Context context) {
        String md5uuid = getUserUUID(context);
        if (md5uuid == null || md5uuid.isEmpty()) {
            String guid = "";
            final String uniqueID = UUID.randomUUID().toString();
            Date date = new Date();
            long timeMilli = date.getTime();
            guid = uniqueID + timeMilli;
           // md5uuid = md5(guid);
            setUserUUID(context, guid);
        }
        return md5uuid;
    }

    public static String generateMainLink(Context context) {
        String MainUrl ="";
        try {
            String pkgurl =  context.getPackageName()+"-"+generateUserUUID(context);
            String base64 = Base64.encodeToString(pkgurl.getBytes("UTF-8"), Base64.DEFAULT);
            MainUrl = getEndp(context)+"?"+base64+";2;";
            MainUrl = MainUrl + URLEncoder.encode(getReceivedAttribution(context), "utf-8");
        }catch (Exception ignored){
        }
        return MainUrl;
    }


    public static void setUserUUID(Context context, String value) {
        if (context != null) {
            SharedPreferences preferences = context.getSharedPreferences(KEY_PREFERENCE, MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(KEY_USER_UUID, value);
            editor.apply();
        }
    }

    public static String getUserUUID(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(KEY_PREFERENCE, MODE_PRIVATE);
        return preferences.getString(KEY_USER_UUID, "");
    }

    public static void setEndP(Context context, String value) {
        if (context != null) {
            SharedPreferences preferences = context.getSharedPreferences(KEY_PREFERENCE, MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(KEY_CONFIG_VALUE, value);
            editor.apply();
        }
    }

    public static String getEndp(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(KEY_PREFERENCE, MODE_PRIVATE);
        return preferences.getString(KEY_CONFIG_VALUE, "");
    }

    public static boolean isConnectedToInternet(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            for (NetworkInfo networkInfo : info)
                if (networkInfo.getState() == NetworkInfo.State.CONNECTED) {
                    return true;
                }
        }
        return false;
    }
    public static void setReceivedAttribution(Context context, String value) {
        if (context != null) {
            SharedPreferences preferences = context.getSharedPreferences(KEY_PREFERENCE, MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(KEY_ADJUST_ATTRIBUTES, value);
            editor.apply();
        }
    }

    public static String getReceivedAttribution(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(KEY_PREFERENCE, MODE_PRIVATE);
        return preferences.getString(KEY_ADJUST_ATTRIBUTES, "");
    }


}
