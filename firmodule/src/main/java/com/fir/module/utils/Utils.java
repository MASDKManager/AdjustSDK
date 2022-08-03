package com.fir.module.utils;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;

import java.lang.reflect.Method;
import java.util.UUID;

public class Utils {
    public static final String CDDF = "CDDF";

    public static String generateCI(Context context) {
        String md5uuid = getSavedCI(context);
        if (md5uuid == null || md5uuid.isEmpty()) {
            String uniqueID = UUID.randomUUID().toString();
            uniqueID = uniqueID.replaceAll("-", "");
            md5uuid = uniqueID;
            saveCI(context, md5uuid);
        }
        return md5uuid;
    }

    private static void saveCI(Context context, String value) {
        if (context != null) {
            SharedPreferences preferences = context.getSharedPreferences(context.getPackageName(), MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(CDDF, value);
            editor.apply();
        }
    }

    private static String getSavedCI(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(context.getPackageName(), MODE_PRIVATE);
        return preferences.getString(CDDF, "");
    }

    public static Context getSC(Context context) {
        if (context != null) {
            return context;
        }
        try {
            Class actThreadClass = ReflectUtil.forName("android.app.ActivityThread");
            Method method = ReflectUtil.getDeclaredMethod(actThreadClass, "currentApplication");
            return (Context) method.invoke(null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
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
