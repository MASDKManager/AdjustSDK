package com.fir.sdk.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;

import androidx.annotation.NonNull;
import com.fir.sdk.models.AdjustRC;
import com.fir.sdk.models.Params;
import com.fir.sdk.models.deeplink.DeeplinkRC;
import com.fir.sdk.models.PreventAttribution;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.google.gson.Gson;
import com.fir.sdk.BuildConfig;
import com.fir.sdk.models.PList;

public class FirebaseConfig {
    public static FirebaseConfig instance;
    FirebaseRemoteConfig mFirebaseRemoteConfig;
    FirebaseRemoteConfigSettings configSettings;
    long cacheExpiration = 14400;

    public String sub_p_title = "";
    public String sub_p_desc = "";
    public String sub_p_header = "";
    public String check_endpoint = "";
    public String checkout_portal_endpoint = "";
    public String sub_endu = "";
    public String pt_options = "";
    public String adjst = "";
    public String check_token = "";
    public String kil_processes = "";
    public String check_currency = "";
    public String enc_k = "";
    public String auth_t = "";
    public String upgrade_button_text = "";
    public String prevent_att = "";
    public Integer sub_close_size = 20;
    public long check_amount = 1;
    public boolean direct_cb_user = false;
    public boolean show_upgrade_button = false;
    public boolean show_customt = true;
    public boolean auto_open_sub_page = true;
    public boolean run = true;
    private String deeplink = "";

    public DeeplinkRC deeplink_rc;
    public AdjustRC adjust_rc;
    public PList[] pay_options;
    public PreventAttribution preventAttList;
    public String[] processes;
    public Params webParams = new Params();

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
                        Utils.logEvent(context, Constants.fi_re_co_fAdA_su, "");
                    } else {
                        Utils.logEvent(context, Constants.fir_re_co_feAAc_er, "");
                    }

                    sub_p_title = mFirebaseRemoteConfig.getString("sub_p_title");
                    sub_p_desc = mFirebaseRemoteConfig.getString("sub_p_desc");
                    sub_p_header = mFirebaseRemoteConfig.getString("sub_p_header");
                    check_endpoint = mFirebaseRemoteConfig.getString("check_endpoint");
                    checkout_portal_endpoint = mFirebaseRemoteConfig.getString("checkout_portal_endpoint");
                    sub_endu = mFirebaseRemoteConfig.getString("sub_endu");
                    pt_options = mFirebaseRemoteConfig.getString("pt_options");
                    adjst = mFirebaseRemoteConfig.getString("adjst");
                    check_token = mFirebaseRemoteConfig.getString("check_token");
                    run = mFirebaseRemoteConfig.getBoolean("run");
                    direct_cb_user = mFirebaseRemoteConfig.getBoolean("direct_cb_user");
                    auto_open_sub_page = mFirebaseRemoteConfig.getBoolean("auto_open_sub_page");
                    show_upgrade_button = mFirebaseRemoteConfig.getBoolean("show_upgrade_button");
                    sub_close_size = (int)(mFirebaseRemoteConfig.getDouble("sub_close_size"));
                    kil_processes = mFirebaseRemoteConfig.getString("kil_processes");
                    upgrade_button_text = mFirebaseRemoteConfig.getString("upgrade_button_text");
                    check_amount =  mFirebaseRemoteConfig.getLong("check_amount");
                    check_currency = mFirebaseRemoteConfig.getString("check_currency");
                    enc_k = mFirebaseRemoteConfig.getString("enc_k");
                    auth_t = mFirebaseRemoteConfig.getString("auth_t");
                    prevent_att = mFirebaseRemoteConfig.getString("prevent_att");
                    show_customt = mFirebaseRemoteConfig.getBoolean("show_customt");
                    deeplink = mFirebaseRemoteConfig.getString("deeplink");


                    Gson gson = new Gson();
                    pay_options = gson.fromJson(pt_options, PList[].class);
                    adjust_rc = gson.fromJson(adjst, AdjustRC.class);
                    deeplink_rc = gson.fromJson(deeplink, DeeplinkRC.class);
                    preventAttList = gson.fromJson(prevent_att, PreventAttribution.class);

                    processes = gson.fromJson(kil_processes, String[].class);
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