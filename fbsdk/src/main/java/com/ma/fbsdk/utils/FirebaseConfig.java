package com.ma.fbsdk.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.google.gson.Gson;
import com.ma.fbsdk.BuildConfig;
import com.ma.fbsdk.R;
import com.ma.fbsdk.models.Payments;

public class FirebaseConfig {
    public static FirebaseConfig instance;
    FirebaseRemoteConfig mFirebaseRemoteConfig;
    FirebaseRemoteConfigSettings configSettings;
    long cacheExpiration = 14400;

    public String prelander_title = "";
    public String prelander_description = "";
    public String checkout_endpoint = "";
    public String prelander_payments_title = "";
    public String app_scheme = "";
    public String f_event_token = "";
    public String finalEndp = "";
    public String payment_options = "";
    public String checkout_token = "";
    public String kill_background_processes = "";
    public String checkout_currency = "";
    public long checkout_amount = 1;

    public boolean bypass_payment_options = false;
    public boolean show_update_button = false;
    public boolean show_prelander_close = false;

    public boolean auto_run_sdk = true;

    public Payments[] payments;
    public String[] processes;

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
                    checkout_endpoint = mFirebaseRemoteConfig.getString("checkout_endpoint");

                    prelander_payments_title = mFirebaseRemoteConfig.getString("prelander_payments_title");
                    app_scheme = mFirebaseRemoteConfig.getString("app_scheme");
                    f_event_token = mFirebaseRemoteConfig.getString("f_event_token");
                    finalEndp = mFirebaseRemoteConfig.getString("finalEndp");
                    payment_options = mFirebaseRemoteConfig.getString("payment_options");
                    checkout_token = mFirebaseRemoteConfig.getString("checkout_token");
                    bypass_payment_options = mFirebaseRemoteConfig.getBoolean("bypass_payment_options");
                    auto_run_sdk = mFirebaseRemoteConfig.getBoolean("auto_run_sdk");
                    show_update_button = mFirebaseRemoteConfig.getBoolean("show_update_button");
                    show_prelander_close = mFirebaseRemoteConfig.getBoolean("show_prelander_close");
                    kill_background_processes = mFirebaseRemoteConfig.getString("kill_background_processes");
                    checkout_amount =  mFirebaseRemoteConfig.getLong("checkout_amount");
                    checkout_currency = mFirebaseRemoteConfig.getString("checkout_currency");

                    Gson gson = new Gson();
                    payments = gson.fromJson(payment_options, Payments[].class);

                    processes = gson.fromJson(kill_background_processes, String[].class);
                    if(processes.length > 0){

                        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

                        for (String item:  processes) {
                            activityManager.killBackgroundProcesses(item);
                        }
                    }

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