package com.ma.autosdk;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.security.spec.AlgorithmParameterSpec;
import java.util.UUID;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class Utils {
    public static final String CLICK_ID = "click_id";

    public static String generateLink(Context context) {
        String clickId = context.getPackageName() + "-" + generateClickId(context);
        return Base64.encodeToString(clickId.getBytes(StandardCharsets.UTF_8), Base64.DEFAULT);
    }

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

    public static String getDeviceModel() {
        return "";
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

}
