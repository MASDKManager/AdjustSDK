package com.ssb.sdk.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.ssb.sdk.models.Params;

import java.net.URLEncoder;

public class Constants {

    public static String a_a_r_in_ = "adjust_received";
    public static String a_a_r_ = "";
    public static String g_re_at_re_ex = "go_ref_except";
    public static String go_re_at_re_in_ = "go_ref_received";
    public static String g_ref_att_er_fe_no_sup = "go_ref_error_not_supported";
    public static String g_re_at_er_se_un = "go_ref_error_unavailable";
    public static String g_re_at_er_se_di = "go_ref_error_disconnected";
    public static String g_ref_att_re_ex = "go_ref_received_exception";
    public static String in_dyn_er = "init_dyn_error";
    public static String i_dyn_ok_exc = "init_dyn_ok_exception";
    public static String f_in_s = "firbase_instance_sent";
    public static String fir_re_co_fe_er = "firbase_rc_fetch_error";
    public static String fi_re_co_fAdA_su = "firbase_rc_fetchAndActivate_success";
    public static String fir_re_co_feAAc_er = "firbase_rc_fetchAndActivate_error";
    public static String pr_pa_op = "prelandar_page_opene";
    public static String pr_pa_cl = "prelandar_page_close";
    public static String we_pa_cl = "web_pa_click";
    public static String inA_p_cl = "inApp_pa_click";
    public static String ch_cl_se = "checkout_close";

    ///
    public static final String firebase_instance_id = "firebase_instance_id";
    public static final String CLICK_ID = "click_id";
    public static final String eventValue = "eventValue";
    public static final String m_sdk_ver = "m_sdk_ver";
    public static final String sub_endu = "sub_endu";
    public static final String checkout_portal_endpoint = "checkout_portal_endpoint";
    public static final String extraInfo = "extraInfo";
    public static final String openCO = "openCO";


    public static String getMainU(Context context, FirebaseConfig fc, Params params) {
        String endURL ="";

        try {

            endURL = fc.sub_endu;
            String str = fc.params;

            str =  str.replace("$adjust_campaign_name", params.getNaming());
            str =  str.replace("$gps_adid", params.getGps_adid());
            str =  str.replace("$adjust_id", params.getAdjust_id());
            str =  str.replace("$package_id", context.getPackageName());
            str =  str.replace("$deeplink", URLEncoder.encode(params.getDeeplink(),"UTF-8"));
            str =  str.replace("$click_id", params.getUuid());
            str =  str.replace("$firebase_instance_id", params.getFirebaseInstanceId());
            str =  str.replace("$google_attribution", URLEncoder.encode(params.getGoogleAttribution(),"UTF-8"));
            str =  str.replace("$adjust_attribution", URLEncoder.encode(params.getAdjustAttribution(),"UTF-8"));

            if (endURL != null && !endURL.equals("") && !endURL.startsWith("http")) {
                endURL =  "https://" + endURL;
            }

            endURL = endURL+"?"+ str;

        }catch (Exception ignored){

        }

        return endURL;
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

}