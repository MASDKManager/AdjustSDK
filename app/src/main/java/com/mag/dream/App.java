package com.mag.dream;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.adjust.sdk.Adjust;
import com.adjust.sdk.AdjustConfig;
import com.adjust.sdk.LogLevel;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.UUID;

public class App extends Application {

    public static final String App_PREF = "magApp";
    String appEvent = "";
    public AdjustConfig config;

    @Override
    public void onCreate() {
        super.onCreate();

        String appToken = "wjuc18qnii9s";
        String end_url = "dkg2c924lz99y.cloudfront.net";

        String environment = AdjustConfig.ENVIRONMENT_SANDBOX;
        config = new AdjustConfig(this, appToken, environment, end_url, generateUserUUID(getApplicationContext()));
        config.setLogLevel(LogLevel.VERBOSE);

        Adjust.onCreate(config);
        registerActivityLifecycleCallbacks(new AdjustLifecycleCallbacks());

        Adjust.addSessionCallbackParameter("user_uuid", generateUserUUID(getApplicationContext()));

        ProcessLifecycleOwner.get().getLifecycle().addObserver(new LifecycleEventObserver() {
            @Override
            public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
                Log.e("TAG", "onStateChanged: " + event.toString());

                if (appEvent.equals(("ON_STOP")) && event.toString().equals("ON_START")) {
                    com.adjust.sdk.init.Utils.startCounter(getApplicationContext());
                }

                appEvent = event.toString();
            }
        });
    }

    private static final class AdjustLifecycleCallbacks implements ActivityLifecycleCallbacks {

        @Override
        public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {

        }

        @Override
        public void onActivityStarted(@NonNull Activity activity) {

        }

        @Override
        public void onActivityResumed(Activity activity) {
            Adjust.onResume();
        }

        @Override
        public void onActivityPaused(Activity activity) {
            Adjust.onPause();
        }

        @Override
        public void onActivityStopped(@NonNull Activity activity) {

        }

        @Override
        public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

        }

        @Override
        public void onActivityDestroyed(@NonNull Activity activity) {

        }

    }

    public static String generateUserUUID(Context context) {
        String md5uuid = getAppUserUUID(context);
        if (md5uuid == null || md5uuid.isEmpty()) {
            String guid = "";
            final String uniqueID = UUID.randomUUID().toString();
            Date date = new Date();
            long timeMilli = date.getTime();
            guid = uniqueID + timeMilli;
            md5uuid = md5(guid);
            setAppUserUUID(context, md5uuid);
        }
        return md5uuid;
    }

    private static String md5(String s) {
        try {
            // Create MD5 Hash
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(s.getBytes());
            byte[] messageDigest = digest.digest();

            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < messageDigest.length; i++)
                hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";

    }

    public static void setAppUserUUID(Context context, String value) {
        if (context != null) {
            SharedPreferences preferences = context.getSharedPreferences(App_PREF,
                    MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("User_UUID", value);
            editor.apply();
        }
    }

    public static String getAppUserUUID(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(App_PREF,
                MODE_PRIVATE);
        return preferences.getString("User_UUID", "");
    }

}