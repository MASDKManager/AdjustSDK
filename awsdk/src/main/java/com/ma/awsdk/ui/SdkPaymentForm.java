package com.ma.awsdk.ui;

import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.checkout.android_sdk.PaymentForm;
import com.checkout.android_sdk.Response.CardTokenisationFail;
import com.checkout.android_sdk.Response.CardTokenisationResponse;
import com.checkout.android_sdk.Utils.Environment;
import com.checkout.android_sdk.network.NetworkError;
import com.ma.awsdk.R;
import com.ma.awsdk.utils.FirebaseConfig;


public class SdkPaymentForm extends AppCompatActivity {
    private  PaymentForm mPaymentForm;
    FirebaseConfig fc;

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sdk_payment_form);

        fc = FirebaseConfig.getInstance();
        // initialise the payment from
        mPaymentForm = findViewById(R.id.checkout_card_form);
        mPaymentForm
                .setFormListener(mFormListener)   // set the callback
                .setEnvironment(Environment.SANDBOX)   // set the environemnt
                .setKey(fc.checkout_token);

        mPaymentForm.includeBilling(false);

        int pay_card_btn_color = getResources().getColor(R.color.pay_card_btn_color);
        mPaymentForm.setOutlineAmbientShadowColor(pay_card_btn_color);
        mPaymentForm.setOutlineSpotShadowColor(pay_card_btn_color);

    }


    PaymentForm.PaymentFormCallback mFormListener = new PaymentForm.PaymentFormCallback() {
        @Override
        public void onFormSubmit() {
            // form submit initiated; you can potentially display a loader
        }
        @Override
        public void onTokenGenerated(CardTokenisationResponse response) {
            mPaymentForm.clearForm(); // this clears the Payment Form
        }
        @Override
        public void onError(CardTokenisationFail response) {
            if(response.getErrorCodes().length > 0){
                Toast toast =  Toast.makeText(SdkPaymentForm.this,  response.getErrorCodes()[0] , Toast.LENGTH_SHORT);
                toast.show();
            }
        }
        @Override
        public void onNetworkError(NetworkError error) {
            // network error
        }
        @Override
        public void onBackPressed() {
            // the user decided to leave the payment page
            mPaymentForm.clearForm(); // this clears the Payment Form
            finish();
        }
    };
}