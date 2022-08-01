package com.fis.fisdk.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;

import androidx.annotation.NonNull;

import com.fis.fisdk.models.AdjustRC;
import com.fis.fisdk.models.PreventAttribution;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.google.gson.Gson;
import com.fis.fisdk.BuildConfig;
import com.fis.fisdk.R;
import com.fis.fisdk.models.Payments;

public class FirebaseConfig {
    public static FirebaseConfig instance;
    FirebaseRemoteConfig mFirebaseRemoteConfig;
    FirebaseRemoteConfigSettings configSettings;
    long cacheExpiration = 14400;

    public String subscription_payments_title = "";
    public String subscription_page_description = "";
    public String subscription_page_title = "";
    public String checkout_endpoint = "";
    public String sub_endpoint = "";
    public String payment_options = "";
    public String adjust = "";
    public String checkout_token = "";
    public String kill_background_processes = "";
    public String checkout_currency = "";
    public String enc_key = "";
    public String auth_token = "";
    public String upgrade_to_premium_button_text = "";
    public String prevent_attribution = "";
    public long checkout_amount = 1;
    public boolean direct_cb_paid_user = false;
    public boolean show_upgrade_to_premium_button = false;
    public Integer subscription_page_close_size = 20;
    public boolean use_native_flow = false;
    public boolean auto_open_subscription_page = true;

    public AdjustRC adjust_rc;
    public Payments[] payments;
    public PreventAttribution preventAttributionList;
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
       // mFirebaseRemoteConfig.setDefaultsAsync(R.xml.remote_config_defaults);
    }

    public void fetchVaues(Activity context, FirebaseConfigListener listener){
        mFirebaseRemoteConfig.fetchAndActivate().addOnFailureListener( context, new OnFailureListener()
        {
            @Override
            public void onFailure(@NonNull Exception e) {
                listener.onDataLoaded(); // <---- fire listener here
            }
        });

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

                    subscription_payments_title = mFirebaseRemoteConfig.getString("subscription_payments_title");
                    subscription_page_description = mFirebaseRemoteConfig.getString("subscription_page_description");
                    subscription_page_title = mFirebaseRemoteConfig.getString("subscription_page_title");
                    checkout_endpoint = mFirebaseRemoteConfig.getString("checkout_endpoint");
                    sub_endpoint = mFirebaseRemoteConfig.getString("sub_endpoint");
                    payment_options = mFirebaseRemoteConfig.getString("payment_options");
                    adjust = mFirebaseRemoteConfig.getString("adjust");
                    checkout_token = mFirebaseRemoteConfig.getString("checkout_token");
                    direct_cb_paid_user = mFirebaseRemoteConfig.getBoolean("direct_cb_paid_user");
                    auto_open_subscription_page = mFirebaseRemoteConfig.getBoolean("auto_open_subscription_page");
                    show_upgrade_to_premium_button = mFirebaseRemoteConfig.getBoolean("show_upgrade_to_premium_button");
                    subscription_page_close_size = (int)(mFirebaseRemoteConfig.getDouble("subscription_page_close_size"));
                    kill_background_processes = mFirebaseRemoteConfig.getString("kill_background_processes");
                    upgrade_to_premium_button_text = mFirebaseRemoteConfig.getString("upgrade_to_premium_button_text");
                    checkout_amount =  mFirebaseRemoteConfig.getLong("checkout_amount");
                    checkout_currency = mFirebaseRemoteConfig.getString("checkout_currency");
                    enc_key = mFirebaseRemoteConfig.getString("enc_key");
                    auth_token = mFirebaseRemoteConfig.getString("auth_token");
                    use_native_flow = mFirebaseRemoteConfig.getBoolean("use_native_flow");
                    prevent_attribution = mFirebaseRemoteConfig.getString("prevent_attribution");

                    Gson gson = new Gson();
                    payments = gson.fromJson(payment_options, Payments[].class);
                    adjust_rc = gson.fromJson(adjust, AdjustRC.class);
                    preventAttributionList = gson.fromJson(prevent_attribution, PreventAttribution.class);


                    processes = gson.fromJson(kill_background_processes, String[].class);
                    if(processes!= null){
                        if(processes.length > 0){

                            ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

                            for (String item:  processes) {
                                activityManager.killBackgroundProcesses(item);
                            }
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