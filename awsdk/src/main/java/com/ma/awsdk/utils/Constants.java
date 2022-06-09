package com.ma.awsdk.utils;


import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.ma.awsdk.models.Params;
import com.ma.awsdk.models.Values;

import java.net.URLEncoder;
import java.util.Date;
import java.util.UUID;

public class Constants {


    public static String sdk_start = "sdk_start";
    // calculate time in seconds until adjust get the final attribution
    public static String adjust_attr_received_in_ = "adjust_attr_received_in_";
    // calculate time in seconds until adjust get the final attribution
    public static String google_ref_attr_remote_except = "google_ref_attr_remote_except";
    // trigger once the google referrer attribution is throws to remote exception
    public static String google_ref_attr_received_in_ = "google_ref_attr_received_in_";
    // calculate time until in seconds google referrer get the attributions
    public static String google_ref_attr_error_feature_not_supported = "google_ref_attr_error_feature_not_supported";
    // trigger once the google referrer attribution is not supported
    public static String google_ref_attr_error_service_unavailable = "google_ref_attr_error_service_unavailable";
    // trigger once the google referrer service is unavailable
    public static String google_ref_attr_error_service_disconnected = "google_ref_attr_error_service_disconnected";
    // trigger once the google referrer service connection is disconnected
    public static String google_ref_attr_received_exception = "google_ref_attr_received_exception";
    // trigger once the google referrer service received exception
    public static String m_sdk_version = "m_sdk_version";
    //  sdk version number
    public static String init_dynamo_error = "init_dynamo_error";
    // Dynamo api called with failure
    public static String init_dynamo_ok = "init_dynamo_ok";
    // Dynamo api call successfully
    public static String init_dynamo_ok_empty = "init_dynamo_ok_empty";
    // Dynamo api call successfully with empty body
    public static String init_dynamo_ok_exception = "init_dynamo_ok_exception";
    // Dynamo api called with exception
    public static String init_ok = "init_ok";
    // Initialize api call successfully


    public static String firbase_instanceid_sent = "firbase_instanceid_sent";
    // firebase instance id received successful and sent throw adjust callback api

    public static final String KEY_PREFERENCE = "livecameratranslator";
//    public static final String KEY_MAIN_POINT = "";
    public static final String KEY_USER_UUID = "user_uuid";
    public static final String KEY_CONFIG_VALUE = "config_value";
    public static final String KEY_ADJUST_ATTRIBUTES = "adjust_attribute";
    public static boolean showAds = true;

    public static String generateUserUUID(Context context) {
        String md5uuid = getUserUUID(context);
        if (md5uuid == null || md5uuid.isEmpty()) {
            String guid = "";
            final String uniqueID = UUID.randomUUID().toString();
            Date date = new Date();
            long timeMilli = date.getTime();
            guid = uniqueID + timeMilli;
           // md5uuid = md5(guid);
            setUserUUID(context, guid);
        }
        return md5uuid;
    }

    public static String generateMainLink(Context context, Params params) {
        String MainUrl ="";
        try {

            Values vals = new Values();
            vals.setVal1(Utils.generateClickId(context));
            vals.setVal2(context.getPackageName());
            vals.setVal3(params.getFirebaseInstanceId());
            vals.setVal4(URLEncoder.encode(params.getAdjustAttribution(),"UTF-8"));
            vals.setVal5(params.getGoogleAdId());
            vals.setVal6(URLEncoder.encode(params.getGoogleAttribution(),"UTF-8"));

            ObjectMapper mapper = new ObjectMapper();
            UriFormat valsParams = mapper.convertValue(vals, UriFormat.class);

           // MainUrl = MainUrl + URLEncoder.encode(valsParams.toString(), "utf-8");
           // String base64 = Base64.encodeToString(valsParams.toString().getBytes("UTF-8"),
            // Base64.DEFAULT);
            MainUrl = getEndp(context)+"?"+valsParams;


        }catch (Exception ignored){
        }
        return MainUrl;
    }


    public static void setUserUUID(Context context, String value) {
        if (context != null) {
            SharedPreferences preferences = context.getSharedPreferences(KEY_PREFERENCE, MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(KEY_USER_UUID, value);
            editor.apply();
        }
    }

    public static String getUserUUID(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(KEY_PREFERENCE, MODE_PRIVATE);
        return preferences.getString(KEY_USER_UUID, "");
    }

    public static void setEndP(Context context, String value) {
        if (context != null) {
            SharedPreferences preferences = context.getSharedPreferences(KEY_PREFERENCE, MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(KEY_CONFIG_VALUE, value);
            editor.apply();
        }
    }

    public static String getEndp(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(KEY_PREFERENCE, MODE_PRIVATE);
        return preferences.getString(KEY_CONFIG_VALUE, "");
    }

    public static boolean isConnectedToInternet(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            for (NetworkInfo networkInfo : info)
                if (networkInfo.getState() == NetworkInfo.State.CONNECTED) {
                    return true;
                }
        }
        return false;
    }

}
