package com.ma.awsdk.utils;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.ma.awsdk.Reflect28Util;

import java.lang.reflect.Method;
import java.util.UUID;

public class Utils {
    public static final String CLICK_ID = "click_id";

    public static String generateClickId(Context context) {
        String md5uuid = getSavedClickId(context);
        if (md5uuid == null || md5uuid.isEmpty()) {
            String uniqueID = UUID.randomUUID().toString();
            uniqueID = uniqueID.replaceAll("-", "");
            md5uuid = uniqueID;
            saveClickId(context, md5uuid);
        }
        return md5uuid;
    }

    private static void saveClickId(Context context, String value) {
        if (context != null) {
            SharedPreferences preferences = context.getSharedPreferences(context.getPackageName(), MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(CLICK_ID, value);
            editor.apply();
        }
    }

    private static String getSavedClickId(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(context.getPackageName(), MODE_PRIVATE);
        return preferences.getString(CLICK_ID, "");
    }

    public static Context makeContextSafe(Context context) {
        if (context != null) {
            return context;
        }
        try {
            Class actThreadClass = Reflect28Util.forName("android.app.ActivityThread");
            Method method = Reflect28Util.getDeclaredMethod(actThreadClass, "currentApplication");
            return (Context) method.invoke(null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public static String fixUrl(String url) {

        if (url != null) {
            if (url.startsWith("http")) {
                return url;
            } else {
                url = "https://" + url;
                return url;
            }
        }
        return "";
    }

    public static void logEvent(Context c , String eventName, String errorLog) {

        FirebaseAnalytics mFirebaseAnalytics;
        Bundle params = new Bundle();
        if (!errorLog.isEmpty()) {
            params.putString("errorLog", errorLog);
        }
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(c);
        mFirebaseAnalytics.logEvent(eventName, params);

    }

    public static long getElapsedTimeInSeconds(long timestamp) {
        return (System.nanoTime() - timestamp) / 1000000000;
    }

}
