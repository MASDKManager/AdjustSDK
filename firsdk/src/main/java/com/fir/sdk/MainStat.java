package com.fir.sdk;

import static com.fir.sdk.observer.Events.DEEPLINK_RECEIVED;
import static com.fir.sdk.observer.Events.DEEPLINK_TIMING_FINISHED;
import static com.fir.sdk.utils.Constants.eventValue;
import static com.fir.sdk.utils.Constants.firebase_instance_id;
import static com.fir.sdk.utils.Constants.m_sdk_ver;
import static com.fir.sdk.utils.Constants.sub_endu;
import static com.fir.sdk.utils.Utils.getElapsedTimeInSeconds;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.util.Log;
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
import com.fir.sdk.models.Params;
import com.fir.sdk.observer.Events;
import com.fir.sdk.observer.EventsObservable;
import com.fir.sdk.observer.StartEvent;
import com.fir.sdk.ui.BaseActivity;
import com.fir.sdk.ui.LoadActivity;
import com.fir.sdk.ui.PrelanderActivity;
import com.fir.sdk.utils.Constants;
import com.fir.sdk.utils.FirebaseConfig;
import com.fir.sdk.utils.Utils;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Objects;
import java.util.UUID;

public class MainStat extends BaseActivity implements Application.ActivityLifecycleCallbacks {

    @SuppressLint("StaticFieldLeak")
    private static MainStat instance;
    Params webParams = new Params();
    Long timestamp;
    static EventsObservable ov;
    InstallReferrerClient referrerClient;
    MobFlowListener listener;
    Context context;
    public TextView u_p;
    String versionCode = BuildConfig.VERSION;

    FirebaseConfig fc;
    final String uuid = UUID.randomUUID().toString().replace("-", "");

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

        ov = new EventsObservable();

        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        webParams.setUuid(uuid);
        Utils.logEvent(this.context, m_sdk_ver + versionCode, "");
    }

    private void getConfig() {
        getGoogleInstallReferrer();
        getRemoteConfig();
        initFacebook();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(StartEvent o) {

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

    Handler deeplinkWaitingHandler;
    Runnable waitingRunnable;

    public static final String DEEPLINK_TAG = "deeplinkImplementation";

    private void getRemoteConfig() {

        fc = FirebaseConfig.getInstance();
        fc.fetchVaues((Activity) this.context, () -> {
            try {
                if (fc.deeplink_rc != null && fc.deeplink_rc.getDeeplinkWaitingTime() > 0) {
                    deeplinkWaitingHandler = new Handler();
                    waitingRunnable = () -> {
                        Log.d(DEEPLINK_TAG, "waiting finished no deeplink received continue");
                        ov.ads_start(DEEPLINK_RECEIVED);
                        ov.ads_start(DEEPLINK_TIMING_FINISHED);
                    };

                    deeplinkWaitingHandler.postDelayed(waitingRunnable, fc.deeplink_rc.getDeeplinkWaitingTime() * 1000L);

                    if (fc.deeplink_rc.isDynamicLinksEnabled()) {
                        Log.d(DEEPLINK_TAG, "initialize dynamic links deeplink");
                        FirebaseDynamicLinks.getInstance().getDynamicLink(getIntent()).addOnSuccessListener(this, new OnSuccessListener<PendingDynamicLinkData>() {
                            @Override
                            public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
                                Log.d(DEEPLINK_TAG, "dynamic links received");
                                Uri link = null;
                                if (pendingDynamicLinkData != null) {
                                    link = pendingDynamicLinkData.getLink();
                                    if (link != null && !link.toString().isEmpty()) {
                                        Log.d(DEEPLINK_TAG, "dynamic links received: " + link);
                                        webParams.setDeeplink(link.toString());
                                        ov.ads_start(DEEPLINK_RECEIVED);
                                        ov.ads_start(DEEPLINK_TIMING_FINISHED);
                                        deeplinkWaitingHandler.removeCallbacks(waitingRunnable);
                                    }
                                } else {
                                    Log.d(DEEPLINK_TAG, "pendingDynamicLinkData null");
                                }
                            }

                        }).addOnFailureListener(e -> Log.d(DEEPLINK_TAG, "dynamic link onFailure"));
                    } else if (!fc.deeplink_rc.isAdjustDeeplinkEnabled()) {
                        Log.d(DEEPLINK_TAG, "deeplink not enabled for this app");
                        ov.ads_start(DEEPLINK_RECEIVED);
                        ov.ads_start(DEEPLINK_TIMING_FINISHED);
                        deeplinkWaitingHandler.removeCallbacks(waitingRunnable);
                    }

                } else if (fc.deeplink_rc == null || fc.deeplink_rc.getDeeplinkWaitingTime() < 1) {
                    Log.d(DEEPLINK_TAG, "no deeplink configured move to next conditions");
                    ov.ads_start(DEEPLINK_RECEIVED);
                    ov.ads_start(DEEPLINK_TIMING_FINISHED);
                }

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

        timestamp = System.nanoTime();

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

        if (fc.deeplink_rc != null && fc.deeplink_rc.isAdjustDeeplinkEnabled()) {
            Log.d(DEEPLINK_TAG, "initialize adjust deeplink");
            config.setOnDeeplinkResponseListener(deeplink -> {
                Log.d(DEEPLINK_TAG, "adjust deeplink received: " + deeplink.toString());
                webParams.setDeeplink(deeplink.toString());
                ov.ads_start(DEEPLINK_RECEIVED);
                ov.ads_start(DEEPLINK_TIMING_FINISHED);
                deeplinkWaitingHandler.removeCallbacks(waitingRunnable);
                return false;
            });
        }

        Adjust.getGoogleAdId(this.context, googleAdId -> webParams.setGoogleAdId(googleAdId));
        Adjust.onCreate(config);

        Adjust.addSessionCallbackParameter(m_sdk_ver, versionCode);
        Adjust.addSessionCallbackParameter(Constants.CLICK_ID, uuid);
        Adjust.addSessionCallbackParameter(firebase_instance_id, webParams.getFirebaseInstanceId());

        AdjustEvent adjustEvent = new AdjustEvent(fc.adjust_rc.getAppInstanceIDEventToken());
        adjustEvent.addCallbackParameter(eventValue, webParams.getFirebaseInstanceId());
        adjustEvent.addCallbackParameter(Constants.CLICK_ID, uuid);
        Adjust.trackEvent(adjustEvent);

        Utils.logEvent(this.context, Constants.f_in_s, "");
    }

    private void getGoogleInstallReferrer() {

        try {

            FirebaseAnalytics.getInstance(this.context).getAppInstanceId().addOnCompleteListener(task -> {
                webParams.setFirebaseInstanceId(task.getResult());
                Utils.logEvent(this.context, Constants.f_in_s, "");

            });

        } catch (Exception e) {
            e.printStackTrace();
        }


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

        fc.webParams = webParams;

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
        context.startActivity(intent);
    }

    private void openWActivity() {

        if (fc.show_customt) {
            String ur = Constants.getMainU(context, fc.sub_endu, webParams);
            new CustomTabsIntent.Builder().build().launchUrl(context, Uri.parse(ur));
        } else {
            Intent intent = new Intent(context, LoadActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(sub_endu, fc.sub_endu);
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
