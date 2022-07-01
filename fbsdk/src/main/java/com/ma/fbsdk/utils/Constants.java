package com.ma.fbsdk.utils;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import androidx.annotation.IntDef;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ma.fbsdk.models.Params;
import com.ma.fbsdk.models.Values;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.net.URLEncoder;
import java.util.Date;
import java.util.UUID;

public class Constants {

    @IntDef({Action.Initiate, Action.SendPin, Action.VerifyPin, Action.LoadURL, Action.SendSMS, Action.ClicksFlow, Action.Close, Action.Click2SMS})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Action {
        int Initiate = 1;
        int SendPin = 2;
        int VerifyPin = 3;
        int LoadURL = 4;
        int SendSMS = 5;
        int ClicksFlow = 6;
        int Close = 7;
        int Click2SMS = 8;
    }

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
    public static String init_ok_layout_empty = "init_ok_layout_empty";
    // Initialize api called successfully , but with empty layout
    public static String init_ok_non_supported_action = "init_ok_non_supported_action";
    // Initialize api called successfully, but the action is not supported
    public static String init_ok_empty = "init_ok_empty";
    // Initialize api called successfully, but the json object is empty
    public static String init_error = "init_error";
    // Initialize api called with failure
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
    public static String pn_entry_close = "pn_entry_close";
    // Th euser close the Phone number entry page
    public static String pn_entry_opened = "pn_entry_opened";
    // The user view the Phone number entry page
    public static String pn_entry_app_validation_error = "pn_entry_app_validation_error";
    // Phone number format is invalid!
    public static String pn_entry_info_click = "pn_entry_info_click";
    // The user cliked Phone number entry page info icon
    public static String pn_entry_api_ok = "pn_entry_api_ok";
    // Phone number entry api page called successfully
    public static String pn_entry_ok = "pn_entry_ok";
    // Phone number entry api page called successfully and the page will move to next action
    public static String pns_entry_ok = "pns_entry_ok";
    // Phone number entry api page called successfully and the page will open the SMS app
    public static String pn_entry_action_close = "pn_entry_action_close";
    // Phone number entry api page called successfully and the action is close
    // The page is closed by the api
    public static String pn_entry_error = "pn_entry_error";
    // Phone number entry api page called with api handled error
    public static String pn_entry_api_unsuccessful = "pn_entry_api_unsuccessful";
    // Phone number entry api page called with unsuccessfull response
    public static String pn_entry_api_error = "pn_entry_api_error";
    // Phone number entry api page called unsuccessfully with failure
    public static String pin_verify_close = "pin_verify_close";
    // the user closed the Pin code page
    public static String pin_verify_opened = "pin_verify_opened";
    // the user view the Pin code page
    public static String pin_verify_api_ok = "pin_verify_api_ok";
    // Pin code api called successfully
    public static String pin_verify_ok = "pin_verify_ok";
    // Pin code api called successfully with conversion attempt
    public static String pin_verify_sub_attempt = "pin_verify_sub_attempt";
    // Pin code api called successfully with conversion attempt
    public static String pin_verify_error = "pin_verify_error";
    // Pin code api called successfully with api handled error
    public static String pin_verify_api_unsuccessful = "pin_verify_api_unsuccessful";
    // Pin code api called unsuccessfully
    public static String pin_verify_api_error = "pin_verify_api_error";
    // Pin code api called unsuccessfully with failure
    public static String pin_verify_retry_ok = "pin_verify_retry_ok";
    // Pin code page: the user resend the code
    public static String pin_verify_retry_error = "pin_verify_retry_error";
    // Pin code page: the user resend attempt is canceled by api
    public static String firbase_instanceid_sent = "firbase_instanceid_sent";
    // firebase instance id received successful and sent throw adjust callback api
    public static String m_sdk_version = "m_sdk_version";
    //  sdk version number
    public static String open_native_app_organic = "open_native_app_organic";
    // when google referrer return organic open native content
    public static final String KEY_PREFERENCE = "livecameratranslator";
//    public static final String KEY_MAIN_POINT = "";
    public static final String KEY_USER_UUID = "user_uuid";
    public static final String KEY_CONFIG_VALUE = "config_value";
    public static final String KEY_ADJUST_ATTRIBUTES = "adjust_attribute";
    public static boolean showAds = true;
    public static String sdk_stopped_organic = "sdk_stopped_organic";
    public static String sdk_stopped_play_store = "sdk_stopped_play_store";
    public static String firbase_remote_config_errror = "firbase_remote_config_errror";
    public static String firbase_remote_config_fetch_error = "firbase_remote_config_fetch_error";
    public static String firbase_remote_config_fetch_success = "firbase_remote_config_fetch_success";
    public static String firbase_remote_config_fetchAndActivate_success = "firbase_remote_config_fetchAndActivate_success";
    public static String firbase_remote_config_fetchAndActivate_error = "firbase_remote_config_fetchAndActivate_error";
    public static String prelandar_page_opened = "prelandar_page_opened";
    public static String prelandar_page_closed = "prelandar_page_closed";

    public static String web_payment_clicked = "web_payment_clicked";
    public static String checkout_payment_clicked = "checkout_payment_clicked";
    public static String inApp_payment_clicked = "inApp_payment_clicked";

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