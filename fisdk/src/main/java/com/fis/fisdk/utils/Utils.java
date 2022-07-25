package com.fis.fisdk.utils;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Base64;
import android.view.Window;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.fis.fisdk.Reflect28Util;

import java.lang.reflect.Method;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Random;
import java.util.UUID;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class Utils {
    public static final String CLICK_ID = "click_id";

    public static String generateClickId(Context context) {
        String md5uuid = getSavedClickId(context);
        if (md5uuid == null || md5uuid.isEmpty()) {
            String uniqueID = UUID.randomUUID().toString();
            uniqueID = uniqueID.replaceAll("-", "");
            md5uuid = uniqueID;
            saveClickId(context, md5uuid);
        }
        return md5uuid;
    }

    private static void saveClickId(Context context, String value) {
        if (context != null) {
            SharedPreferences preferences = context.getSharedPreferences(context.getPackageName(), MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(CLICK_ID, value);
            editor.apply();
        }
    }

    private static String getSavedClickId(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(context.getPackageName(), MODE_PRIVATE);
        return preferences.getString(CLICK_ID, "");
    }

    public static Context makeContextSafe(Context context) {
        if (context != null) {
            return context;
        }
        try {
            Class actThreadClass = Reflect28Util.forName("android.app.ActivityThread");
            Method method = Reflect28Util.getDeclaredMethod(actThreadClass, "currentApplication");
            return (Context) method.invoke(null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String addHttp(String url) {

        if (url != null) {
            String setEndP = "";
            if (url.startsWith("http")) {
                setEndP = url + "/Actions/";
            } else {
                setEndP = "https://" + url + "/Actions/";
            }
            return setEndP;
        }
        return "";
    }

    public static String fixUrl(String url) {

        if (url != null) {
            if (url.startsWith("http")) {
                return url;
            } else {
                url = "https://" + url;
                return url;
            }
        }
        return "";
    }

    public static void logEvent(Context c , String eventName, String errorLog) {

        FirebaseAnalytics mFirebaseAnalytics;
        Bundle params = new Bundle();
        if (!errorLog.isEmpty()) {
            params.putString("errorLog", errorLog);
        }
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(c);
        mFirebaseAnalytics.logEvent(eventName, params);

    }

    public static long getElapsedTimeInSeconds(long timestamp) {
        return (System.nanoTime() - timestamp) / 1000000000;
    }

    public static String generatingRandomString() {

        int leftLimit = 97; // letter 'a'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 10;
        Random random = new Random();
        StringBuilder buffer = new StringBuilder(targetStringLength);
        for (int i = 0; i < targetStringLength; i++) {
            int randomLimitedInt = leftLimit + (int)
                    (random.nextFloat() * (rightLimit - leftLimit + 1));
            buffer.append((char) randomLimitedInt);
        }
        String generatedString = buffer.toString();

        return generatedString;
    }

    public static void openDialog(Context c ,String title, String body) {
        CustomDialogFragment cdf = new CustomDialogFragment(c, title, body);
        cdf.requestWindowFeature(Window.FEATURE_NO_TITLE);
        cdf.show();
    }

    /**
     * @param originalColor color, without alpha
     * @param alpha         from 0.0 to 1.0
     * @return
     */
    public static String addAlpha(String originalColor, double alpha) {
        long alphaFixed = Math.round(alpha * 255);
        String alphaHex = Long.toHexString(alphaFixed);
        if (alphaHex.length() == 1) {
            alphaHex = "0" + alphaHex;
        }
        originalColor = originalColor.replace("#", "#" + alphaHex);


        return originalColor;
    }

    public static String encrypt(String value, String key) {
        try {
            SecretKey secretKey = new SecretKeySpec(key.getBytes(), "UTF-8");
            String iv = key.substring(0, Math.min(key.length(), 16));
            AlgorithmParameterSpec ivSpec = new IvParameterSpec(iv.getBytes());
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);
            return new String(Base64.encode(cipher.doFinal(value.getBytes("UTF-8")), Base64.NO_WRAP));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String decrypt(String value, String key) {
        try {
            SecretKey secretKey = new SecretKeySpec(key.getBytes(), "UTF-8");
            String iv = key.substring(0, Math.min(key.length(), 16));
            AlgorithmParameterSpec ivSpec = new IvParameterSpec(iv.getBytes());
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec);
            byte[] decryptedData = cipher.doFinal(Base64.decode(value.getBytes("UTF-8"), Base64.NO_WRAP));
            String decryptedText = new String(decryptedData, "UTF-8");
            return decryptedText;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
