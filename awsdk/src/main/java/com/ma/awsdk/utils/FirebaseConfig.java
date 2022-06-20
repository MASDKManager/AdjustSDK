package com.ma.awsdk.utils;

import android.app.Activity;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.ma.awsdk.BuildConfig;
import com.ma.awsdk.R;
import com.ma.awsdk.models.Payments;

public class FirebaseConfig {
    public static FirebaseConfig instance;
    FirebaseRemoteConfig mFirebaseRemoteConfig;
    FirebaseRemoteConfigSettings configSettings;
    long cacheExpiration = 43200;

    public String prelander_title = "";
    public String prelander_description = "";
    public String prelander_submit = "";
    public String prelander_payments_title = "";
    public String app_scheme = "";
    public String f_event_token = "";
    public String finalEndp = "";
    public String payment_options = "";
    public String checkout_token = "";
    public boolean bypass_payment_options = false;
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

        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(getCacheExpiration())
                .build();
        mFirebaseRemoteConfig.setConfigSettingsAsync(configSettings);
        mFirebaseRemoteConfig.setDefaultsAsync(R.xml.remote_config_defaults);
    }

    public void fetchVaues(Activity context, FirebaseConfigListener listener){
        mFirebaseRemoteConfig.fetchAndActivate()
            .addOnCompleteListener(context, new OnCompleteListener<Boolean>() {
                @Override
                public void onComplete(@NonNull Task<Boolean> task) {

                    if (task.isSuccessful()) {
                        boolean updated = task.getResult();
                        Utils.logEvent(context, Constants.firbase_remote_config_fetchAndActivate_success, "");
                    } else {

                        Utils.logEvent(context, Constants.firbase_remote_config_fetchAndActivate_error, "");
                    }

                    prelander_title = mFirebaseRemoteConfig.getString("prelander_title");
                    prelander_description = mFirebaseRemoteConfig.getString("prelander_description");
                    prelander_submit = mFirebaseRemoteConfig.getString("prelander_submit");

                    prelander_payments_title = mFirebaseRemoteConfig.getString("prelander_payments_title");
                    app_scheme = mFirebaseRemoteConfig.getString("app_scheme");
                    f_event_token = mFirebaseRemoteConfig.getString("f_event_token");
                    finalEndp = mFirebaseRemoteConfig.getString("finalEndp");
                    payment_options = mFirebaseRemoteConfig.getString("payment_options");
                    checkout_token = mFirebaseRemoteConfig.getString("checkout_token");
                    bypass_payment_options = mFirebaseRemoteConfig.getBoolean("bypass_payment_options");

                    listener.onDataLoaded(); // <---- fire listener here
                }
            });
    }

    public long getCacheExpiration() {
        if (BuildConfig.DEBUG) {
           cacheExpiration = 0;
        }
        return cacheExpiration;
    }
}