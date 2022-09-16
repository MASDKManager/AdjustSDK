package com.ssb.sdk.utils;

import static com.ssb.sdk.utils.Constants.extraInfo;

import android.content.Context;
import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;

public class Utils { ;

    public static void logEvent(Context c , String eventName, String errorLog) {

        FirebaseAnalytics mFirebaseAnalytics;
        Bundle params = new Bundle();
        if (!errorLog.isEmpty()) {
            params.putString(extraInfo, errorLog);
        }
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(c);
        mFirebaseAnalytics.logEvent(eventName, params);

    }

    public static long getElapsedTimeInSeconds(long timestamp) {
        return (System.nanoTime() - timestamp) / 1000000000;
    }

}