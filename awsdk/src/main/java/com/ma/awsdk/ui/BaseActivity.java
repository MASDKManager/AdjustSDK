package com.ma.awsdk.ui;

import android.app.Dialog;
import android.os.Bundle;
import android.view.Window;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.ma.awsdk.R;

import java.util.Objects;

public class BaseActivity extends AppCompatActivity {

    private Dialog progressDialog;
    public FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            progressDialog = new Dialog(this);
            progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            Objects.requireNonNull(progressDialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
         } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void showLoader() {
        if (!isFinishing()) {
            if (progressDialog != null) {

                try {
                    if (!progressDialog.isShowing()) {
                        progressDialog.show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void hideLoader() {
        if (!isFinishing()) {
            progressDialog.dismiss();
        }
    }

    public void  firebaseLog(String eventName, String errorLog){
        Bundle params = new Bundle();
        if (!errorLog.isEmpty() && errorLog != null ){
            params.putString("errorLog", errorLog);
        }
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        mFirebaseAnalytics.logEvent(eventName, params);
    }

}
