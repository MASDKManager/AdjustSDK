package com.fir.module.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fir.module.models.Params;
import com.fir.module.models.Values;

import java.net.URLEncoder;

public class Constants {

    public static String a_a_r_in_ = "adjust_received";
    public static String g_re_at_re_ex = "google_ref_except";
    public static String go_re_at_re_in_ = "google_ref_received";
    public static String g_ref_att_er_fe_no_sup = "google_ref_error_not_supported";
    public static String g_re_at_er_se_un = "google_ref_error_unavailable";
    public static String g_re_at_er_se_di = "google_ref_error_disconnected";
    public static String g_ref_att_re_ex = "google_ref_received_exception";
    public static String in_dyn_er = "init_dyn_error";
    public static String i_dyn_ok_exc = "init_dyn_ok_exception";
    public static String f_in_s = "firbase_instance_sent";
    public static String fir_re_co_fe_er = "firbase_rc_fetch_error";
    public static String fi_re_co_fAdA_su = "firbase_rc_fetchAndActivate_success";
    public static String fir_re_co_feAAc_er = "firbase_rc_fetchAndActivate_error";
    public static String pr_pa_op = "prelandar_page_opene";
    public static String pr_pa_cl = "prelandar_page_close";
    public static String we_pa_cl = "web_payment_clicke";
    public static String inA_p_cl = "inApp_payment_clicke";

    ///
    public static final String firebase_instance_id = "firebase_instance_id";
    public static final String CLICK_ID = "click_id";
    public static final String eventValue = "eventValue";
    public static final String m_sdk_ver = "m_sdk_ver";
    public static final String wParams = "wParams";
    public static final String sub_endu = "sub_endu";
    public static final String extraInfo = "extraInfo";


    public static String getMainU(Context context, String endURL, Params params) {
        try {

            Values vals = new Values();
            vals.setClick_id(params.getUuid());
            vals.setPackage_id(context.getPackageName());
            vals.setFirebase_instance_id(params.getFirebaseInstanceId());
            vals.setAdjust_attribution(URLEncoder.encode(params.getAdjustAttribution(),"UTF-8"));
            vals.setGps_adid(params.getGoogleAdId());
            vals.setGoogle_attribution(URLEncoder.encode(params.getGoogleAttribution(),"UTF-8"));

            ObjectMapper mapper = new ObjectMapper();
            UriFormat valsParams = mapper.convertValue(vals, UriFormat.class);

            if (endURL != null && !endURL.equals("") && !endURL.startsWith("http")) {
                endURL =  "https://" + endURL;
            }

            endURL = endURL+"?"+valsParams;

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