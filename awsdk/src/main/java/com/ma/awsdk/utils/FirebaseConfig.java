package com.ma.awsdk.utils;

import android.app.Activity;
import android.content.Context;

import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.google.gson.Gson;
import com.ma.awsdk.BuildConfig;
import com.ma.awsdk.R;
import com.ma.awsdk.models.Payments;

public class FirebaseConfig {
    public static FirebaseConfig instance;
    FirebaseRemoteConfig mFirebaseRemoteConfig;
    FirebaseRemoteConfigSettings configSettings;
    long cacheExpiration = 43200;

    private FirebaseConfigListener listener;

    public String prelander_title = "";
    public String prelander_description = "";
    public String prelander_submit = "";
    public String adjust_token = "";
    public String app_scheme = "";
    public String f_event_token = "";
    public String finalEndp = "";
    public String payment_options = "";
    public String checkout_token = "";
    public Payments[] payments;

    public interface FirebaseConfigListener {
        public void onDataLoaded();
    }

    public static FirebaseConfig getInstance() {

        if (instance == null) {
            instance = new FirebaseConfig();
        }

        return instance;
    }

    public FirebaseConfig() {

        this.listener = null;

        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(getCacheExpiration())
                .build();
        mFirebaseRemoteConfig.setConfigSettingsAsync(configSettings);
        mFirebaseRemoteConfig.setDefaultsAsync(R.xml.remote_config_defaults);
    }

    public String fetchVaues(Context context,FirebaseConfigListener listener){
        mFirebaseRemoteConfig.fetch(getCacheExpiration())
                .addOnCompleteListener((Activity) context, task -> {

                    if (task.isSuccessful()) {
                        mFirebaseRemoteConfig.fetchAndActivate();
                    } else {
                    //    Utils.logEvent(context, Constants.firbase_remote_config_errror, "");
                    }

                    prelander_title = mFirebaseRemoteConfig.getString("prelander_title");
                    prelander_description = mFirebaseRemoteConfig.getString("prelander_description");
                    prelander_submit = mFirebaseRemoteConfig.getString("prelander_submit");

                    adjust_token = mFirebaseRemoteConfig.getString("adjust_token");
                    app_scheme = mFirebaseRemoteConfig.getString("app_scheme");
                    f_event_token = mFirebaseRemoteConfig.getString("f_event_token");
                    finalEndp = mFirebaseRemoteConfig.getString("finalEndp");
                    payment_options = mFirebaseRemoteConfig.getString("payment_options");
                    checkout_token = mFirebaseRemoteConfig.getString("checkout_token");

                    this.listener = listener;
                    if (listener != null)
                        listener.onDataLoaded(); // <---- fire listener here
                });
        return "";
    }

    public long getCacheExpiration() {
        if (BuildConfig.DEBUG) {
            cacheExpiration = 0;
        }
        return cacheExpiration;
    }
}