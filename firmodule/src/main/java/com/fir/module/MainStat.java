package com.fir.module;

import static com.fir.module.utils.Constants.eventValue;
import static com.fir.module.utils.Constants.m_sdk_ver;
import static com.fir.module.utils.Constants.sub_endu;
import static com.fir.module.utils.Constants.user_uuid;
import static com.fir.module.utils.Constants.wParams;
import static com.fir.module.utils.Utils.getElapsedTimeInSeconds;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.browser.customtabs.CustomTabsIntent;

import com.adjust.sdk.Adjust;
import com.adjust.sdk.AdjustConfig;
import com.adjust.sdk.AdjustEvent;
import com.android.installreferrer.api.InstallReferrerClient;
import com.android.installreferrer.api.InstallReferrerStateListener;
import com.android.installreferrer.api.ReferrerDetails;
import com.google.firebase.FirebaseApp;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.fir.module.models.Params;
import com.fir.module.observer.DynU;
import com.fir.module.observer.Events;
import com.fir.module.observer.URLObservable;
import com.fir.module.ui.WVCActivity;
import com.fir.module.ui.BaseActivity;
import com.fir.module.ui.PrelanderActivity;
import com.fir.module.utils.Constants;
import com.fir.module.utils.FirebaseConfig;
import com.fir.module.utils.Utils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;


import java.util.Objects;


public class MainStat extends BaseActivity implements Application.ActivityLifecycleCallbacks {

    private static MainStat instance;
    Params webParams = new Params();
    Long timestamp;
    static URLObservable ov;
    InstallReferrerClient referrerClient;
    MobFlowListener listener;
    Context context;
    public TextView u_p;

    FirebaseConfig fc;

    public interface MobFlowListener {
        public void onDataLoaded();
    }

    public static MainStat getInstance() {

        if (instance == null) {
            instance = new MainStat();
        }

        return instance;
    }

    public void init(Activity activity, MobFlowListener listener) {

        this.listener = listener;
        this.context = activity;

        timestamp = System.nanoTime();

        Application app = (Application) context.getApplicationContext();
        app.registerActivityLifecycleCallbacks(this);

        FirebaseApp.initializeApp(this.context);

        getConfig();

        ov = new URLObservable();

        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

    }

    private void getConfig(){
        getGoogleInstallReferrer();
        getRemoteConfig();
        initFacebook();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(DynU o) {

        Activity activity = (Activity) context;

        if (!activity.isFinishing() && !activity.isDestroyed()) {
            listener.onDataLoaded();
            runApp(true);
        }
    }

    public void startStat(TextView upgrade) {

        u_p = upgrade;

        u_p.setVisibility(fc.show_upgrade_button ? View.VISIBLE : View.GONE);
        u_p.setText(fc.upgrade_button_text);
        u_p.setOnClickListener(view -> {
            runApp(false);
        });
    }
    private void initFacebook() {
//        FacebookSdk.setApplicationId("");
//        FacebookSdk.setClientToken("");
    }

    private void getRemoteConfig() {

        fc = FirebaseConfig.getInstance();
        fc.fetchVaues((Activity) this.context, () -> {

            try {

                if (fc.adjust_rc != null) {
                    if (Objects.equals(fc.adjust_rc.getEnabled(), "true")) {
                        initAdjust();

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                ov.ads_start(Events.A_R);
                            }
                        }, fc.adjust_rc.getDelay());

                    } else {
                        ov.ads_start(Events.A_R);
                    }
                } else {
                    ov.ads_start(Events.A_R);
                }


                ov.ads_start(Events.F_R_C);

            } catch (Exception e) {
                Utils.logEvent(this.context, Constants.fir_re_co_fe_er, "");
                e.printStackTrace();
            }
        });
    }

    private void initAdjust() {
        String appToken = fc.adjust_rc.getAppToken();
        String environment = AdjustConfig.ENVIRONMENT_PRODUCTION;
        AdjustConfig config = new AdjustConfig(this.context, appToken, environment);

        config.setOnAttributionChangedListener(attribution -> {

            Utils.logEvent(this.context, Constants.a_a_r_in_, "" + getElapsedTimeInSeconds(timestamp));

            if (attribution != null) {
                webParams.setAdjustAttribution(attribution.toString());
            }

            ov.ads_start(Events.A_R);

        });

        config.setOnDeeplinkResponseListener(deeplink -> {

            webParams.setDeeplink(deeplink.toString());
            return false;

        });

        Adjust.getGoogleAdId(this.context, googleAdId -> webParams.setGoogleAdId(googleAdId));
        config.setDelayStart(3);
        Adjust.onCreate(config);

        String versionCode = BuildConfig.VERSION;
        Adjust.addSessionCallbackParameter(m_sdk_ver, versionCode);
        Utils.logEvent(this.context, m_sdk_ver + versionCode, "");

        try {
            FirebaseAnalytics.getInstance(this.context).getAppInstanceId().addOnCompleteListener(task -> {
                webParams.setFirebaseInstanceId(task.getResult());
                AdjustEvent adjustEvent = new AdjustEvent(fc.adjust_rc.getAppInstanceIDEventToken());
                adjustEvent.addCallbackParameter(eventValue, task.getResult());
                adjustEvent.addCallbackParameter(user_uuid, task.getResult());
                Adjust.trackEvent(adjustEvent);

                Adjust.addSessionCallbackParameter(user_uuid, task.getResult());
                Adjust.sendFirstPackages();

                ov.ads_start(Events.F_I_ID);

                Utils.logEvent(this.context, Constants.f_in_s, "");
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
                            Utils.logEvent(context, Constants.g_re_at_re_ex, "");
                        }
                        break;
                    case InstallReferrerClient.InstallReferrerResponse.FEATURE_NOT_SUPPORTED:
                        // API not available on the current Play Store app.
                        Utils.logEvent(context, Constants.g_ref_att_er_fe_no_sup, "");
                        break;
                    case InstallReferrerClient.InstallReferrerResponse.SERVICE_UNAVAILABLE:
                        // Connection couldn't be established.
                        Utils.logEvent(context, Constants.g_re_at_er_se_un, "");
                        break;
                }

                ov.ads_start(Events.G_R);
            }

            @Override
            public void onInstallReferrerServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
                Utils.logEvent(context, Constants.g_re_at_er_se_di, "");
            }
        });
    }

    private void generateInstallReferrer() throws RemoteException {
        try {
            Utils.logEvent(this.context, Constants.go_re_at_re_in_, "" + getElapsedTimeInSeconds(timestamp));
            ReferrerDetails response = this.referrerClient.getInstallReferrer();
            webParams.setGoogleAttribution(response.getInstallReferrer());

        } catch (Exception e) {
            e.printStackTrace();
            Utils.logEvent(this.context, Constants.g_ref_att_re_ex, "");
        }
    }

    private void runApp(Boolean auto) {

        if (!fc.auto_open_sub_page && auto) {
            return;
        }

        String attribution = "";

        if (fc.preventAttList.getUseAdjustAttribution()) {
            try {
                attribution = Adjust.getAttribution().toString();
            } catch (Exception e) {
                attribution = webParams.getAdjustAttribution();
            }
        } else if (fc.preventAttList.getUseGoogleAttribution()) {
            attribution = webParams.getGoogleAttribution();
        }

        if (fc.preventAttList.getUseAdjustAttribution() || fc.preventAttList.getUseGoogleAttribution()) {
            if (attribution != null && !attribution.isEmpty()) {
                for (String preventAttribution : fc.preventAttList.getAttBlackList()) {
                    if (attribution.contains(preventAttribution)) {
                        fc.direct_cb_user = false;
                        break;
                    }
                }
            }
        }

        if (fc.direct_cb_user) {
            openWActivity();
        } else {
            openPrelanderActivity();
        }

    }

    private void openPrelanderActivity() {
        Intent intent = new Intent(context, PrelanderActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(wParams, webParams);
        context.startActivity(intent);
    }

    private void openWActivity() {

        if (fc.show_customt){
            String ur = Constants.generateMainU(context, fc.sub_endu,  webParams );
            new CustomTabsIntent.Builder().build().launchUrl(context, Uri.parse(ur));
        }else{
            Intent intent = new Intent(context, WVCActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(sub_endu, fc.sub_endu);
            intent.putExtra(wParams, webParams);
            context.startActivity(intent);
        }

    }

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle bundle) {
    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {
    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {
        Adjust.onResume();
        getConfig();

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
