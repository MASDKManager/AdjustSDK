package com.fis.fisdk;

import static com.fis.fisdk.utils.Utils.addHttp;
import static com.fis.fisdk.utils.Utils.getElapsedTimeInSeconds;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.adjust.sdk.Adjust;
import com.adjust.sdk.AdjustAttribution;
import com.adjust.sdk.AdjustConfig;
import com.adjust.sdk.AdjustEvent;
import com.adjust.sdk.OnAttributionChangedListener;
import com.android.installreferrer.api.InstallReferrerClient;
import com.android.installreferrer.api.InstallReferrerStateListener;
import com.android.installreferrer.api.ReferrerDetails;
import com.google.firebase.FirebaseApp;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.gson.Gson;
import com.fis.fisdk.models.Params;
import com.fis.fisdk.models.api.RetrofitClient;
import com.fis.fisdk.models.api.Services;
import com.fis.fisdk.models.api.model.request.DeviceInfo;
import com.fis.fisdk.models.api.model.request.InitPayload;
import com.fis.fisdk.models.api.model.request.InstallReferrer;
import com.fis.fisdk.models.api.model.request.Referrer;
import com.fis.fisdk.models.api.model.request.Request;
import com.fis.fisdk.models.api.model.response.ApiResponse;
import com.fis.fisdk.models.api.model.response.Layout;
import com.fis.fisdk.observer.DynURL;
import com.fis.fisdk.observer.Events;
import com.fis.fisdk.observer.URLObservable;
import com.fis.fisdk.ui.AppFileActivity;
import com.fis.fisdk.ui.BaseActivity;
import com.fis.fisdk.ui.PrelanderActivity;
import com.fis.fisdk.ui.nativeui.Action2Activity;
import com.fis.fisdk.utils.Constants;
import com.fis.fisdk.utils.FirebaseConfig;
import com.fis.fisdk.utils.Utils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MobFlow extends BaseActivity implements Application.ActivityLifecycleCallbacks {

    private static MobFlow instance;
    Params webParams = new Params();
    Long timestamp;
    static URLObservable ov;
    InstallReferrerClient referrerClient;
    MobFlowListener listener;
    Context context;
    public View upgrade_premium;
    public static String deeplink = "";
    public static String googleAdId ="";
    public static String adjustAtt ="";

    public static Layout SendPinLayout;
    public static String SendPinSessionId;

    public boolean isLaunched = false;
    FirebaseConfig fc ;

    List<Integer> actionsList = Arrays.asList(Constants.Action.SendPin, Constants.Action.Click2SMS);
    private ApiResponse apiResponse;

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

        timestamp = System.nanoTime();

        Application app = (Application) Utils.makeContextSafe(context.getApplicationContext());
        app.registerActivityLifecycleCallbacks(this);

        FirebaseApp.initializeApp(this.context);

        getGoogleInstallReferrer();
        getRemoteConfig();

        ov = new URLObservable();
        EventBus.getDefault().register(this);

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(DynURL o) {

        listener.onDataLoaded();
        runApp(true);

    }

    public void addUpgradeToPremiumButton( View upgrade_v , Button upgrade_b) {

        upgrade_premium = upgrade_v;
        upgrade_premium.setVisibility(fc.show_upgrade_to_premium_button ? View.VISIBLE : View.GONE);
        upgrade_b.setText(fc.upgrade_to_premium_button_text);
        upgrade_premium.setOnClickListener(view -> {
            runApp(false);
        });
    }

    private void getRemoteConfig(){

        fc = FirebaseConfig.getInstance();
        fc.fetchVaues((Activity) this.context, () -> {

            try {
                callURL();

                if(Objects.equals(fc.adjust_rc.getEnabled(), "true")){
                    initAdjust();
                }else{
                    ov.api_should_start(Events.ADJUST_REFERRER);
                }

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ov.api_should_start(Events.ADJUST_REFERRER);
                    }
                }, fc.adjust_rc.getDelay());


                ov.api_should_start(Events.FIREBASE_REMOTE_CONFIG);

            } catch (Exception e) {
                Utils.logEvent(this.context, Constants.firbase_remote_config_fetch_error, "");
                e.printStackTrace();
            }
        });
    }

    private void initAdjust() {
        String appToken = fc.adjust_rc.getAppToken();
        String environment = AdjustConfig.ENVIRONMENT_PRODUCTION;
        AdjustConfig config = new AdjustConfig(this.context, appToken, environment);

        config.setOnAttributionChangedListener(attribution -> {

            Utils.logEvent(this.context, Constants.adjust_attr_received_in_  , "" + getElapsedTimeInSeconds(timestamp));

            if (attribution != null) {
                webParams.setAdjustAttribution(attribution.toString());
            }

            ov.api_should_start(Events.ADJUST_REFERRER);

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

        try {
            FirebaseAnalytics.getInstance(this.context).getAppInstanceId().addOnCompleteListener(task -> {
                webParams.setFirebaseInstanceId(task.getResult());
                AdjustEvent adjustEvent = new AdjustEvent(fc.adjust_rc.getAppInstanceIDEventToken());
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

        String endURL = fc.sub_endpoint;

        if (endURL != null && !endURL.equals("")) {
            Constants.showAds = false;
            if (endURL.startsWith("http")) {
                Constants.setEndP(this.context, endURL);
            } else {
                Constants.setEndP(this.context, "https://" + endURL);
            }
        }
    }


    public void callAPI() {

        InitPayload initPayload = InitPayload.getInstance();
        DeviceInfo deviceInfo = new DeviceInfo();
        deviceInfo.setDeviceID(Utils.generateClickId(context));
        deviceInfo.setPackageName(context.getPackageName());
        deviceInfo.setOS("Android");
        deviceInfo.setModel("");
        deviceInfo.setUserAgent(System.getProperty("http.agent"));
        deviceInfo.setLangCode(Locale.getDefault().getLanguage());
        deviceInfo.setGps_adid(webParams.getGoogleAdId());

        initPayload.setDeviceInfo(deviceInfo);
        Referrer referrer = new Referrer();
        com.fis.fisdk.models.api.model.request.Adjust adjust = new com.fis.fisdk.models.api.model.request.Adjust();

        if (Adjust.getAttribution() != null) {
            webParams.setAdjustAttribution(Adjust.getAttribution().toString());
        }

        InstallReferrer installReferrer = new InstallReferrer();
        installReferrer.setRefStr(webParams.getGoogleAttribution());
        String installReferrerDeeplink = "";
        installReferrer.setDeeplink(installReferrerDeeplink);

        adjust.setDeeplink(MobFlow.deeplink);
        adjust.setRefStr(webParams.getAdjustAttribution());
        referrer.setAdjust(adjust);
        referrer.setInstallReferrer(installReferrer);
        initPayload.setReferrer(referrer);

        Request request = new Request();
        request.setAction(1);
        request.setTransactionID(UUID.randomUUID().toString());
        request.setSessionID("");
        request.setMSISDN("");
        request.setPinCode("");
        initPayload.setRequest(request);

        Gson gson = new Gson();
        String json = gson.toJson(initPayload);
        String encryptedBody = Utils.encrypt(json, fc.enc_key);

        RetrofitClient.BASE_URL = addHttp(fc.sub_endpoint);
        RetrofitClient.header = fc.auth_token;
        RetrofitClient.encKey = fc.enc_key;

        Services initiateService = RetrofitClient.getRetrofitInstance().create(Services.class);
        initiateService.initiate(RetrofitClient.BASE_URL, RetrofitClient.header, encryptedBody).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {

                if (response.isSuccessful()) {
                    String res = Utils.decrypt(response.body(), RetrofitClient.encKey);
                    Gson gson = new Gson();
                    apiResponse = gson.fromJson(res, ApiResponse.class);

                    if (apiResponse != null) {
                        if (apiResponse.getNextAction() != null && actionsList.contains(apiResponse.getNextAction().getAction())) {
                            if (apiResponse.getNextAction().getLayout() != null) {

                                Utils.logEvent(context, Constants.init_ok, "");
                                openNativeActivity();

                            } else {

                                Utils.logEvent(context, Constants.init_ok_layout_empty, "");
                                return;
                            }
                        } else if (apiResponse.getNextAction() != null) {
                            Utils.logEvent(context, Constants.init_ok_non_supported_action, apiResponse.getDescription()  + " ; " + apiResponse.getMessageToShow());
                            return;
                        }
                    } else {
                        Utils.logEvent(context, Constants.init_ok_empty, "");
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

                Utils.logEvent(context, Constants.init_error, "");
            }
        });
    }

    private void runApp(Boolean auto) {

        if(!fc.auto_open_subscription_page && auto){
            return;
        }

      //  String attribution = webParams.getGoogleAttribution();

        String attribution = "";
        try {
            attribution = Adjust.getAttribution().toString();
        } catch (Exception e) {
            attribution = webParams.getAdjustAttribution();
        }

        if (attribution != null && !attribution.isEmpty() && !attribution.toLowerCase().contains("organic") && !attribution.toLowerCase().contains("play-store")) {
            fc.direct_cb_paid_user = true;
        }

        if(fc.direct_cb_paid_user) {
            if(fc.use_native_flow){
                callAPI();
            }else {
                openAppFileActivity();
            }
        } 
        else {
            openPrelanderActivity();
        }

    }

    private void openPrelanderActivity(){
        Intent intent = new Intent(context, PrelanderActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("webParams", webParams);
        context.startActivity(intent);
    }

    private void openAppFileActivity(){
        Intent intent = new Intent(context, AppFileActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("webParams", webParams);
        context.startActivity(intent);
    }

    private void openNativeActivity(){

        Intent intent = new Intent(context, Action2Activity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("apiResponse", apiResponse);
        context.startActivity(intent);
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
