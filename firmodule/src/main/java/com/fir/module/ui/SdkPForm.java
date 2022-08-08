package com.fir.module.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.RequiresApi;
import com.checkout.android_sdk.PaymentForm;
import com.checkout.android_sdk.Response.CardTokenisationFail;
import com.checkout.android_sdk.Response.CardTokenisationResponse;
import com.checkout.android_sdk.Utils.Environment;
import com.checkout.android_sdk.network.NetworkError;
import com.google.gson.Gson;
import com.fir.module.R;
import com.fir.module.models.checkout.CheckoutLoad;
import com.fir.module.models.checkout.CheckoutResponse;
import com.fir.module.utils.Constants;
import com.fir.module.utils.FirebaseConfig;
import com.fir.module.utils.Utils;
import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

public class SdkPForm extends BaseActivity {
    private  PaymentForm mPaymentForm;
    FirebaseConfig fc;
    Context context;

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sdk_pa_form);

        fc = FirebaseConfig.getInstance();
        // initialise the payment from
        mPaymentForm = findViewById(R.id.checkout_card_form);
        mPaymentForm
                .setFormListener(mFormListener)   // set the callback
                .setEnvironment(Environment.SANDBOX)   // set the environemnt
                .setKey(fc.check_token);

        mPaymentForm.includeBilling(false);

        int pay_card_btn_color = getResources().getColor(R.color.pay_card_btn_color);
        mPaymentForm.setOutlineAmbientShadowColor(pay_card_btn_color);
        mPaymentForm.setOutlineSpotShadowColor(pay_card_btn_color);

        context = this;
    }

    PaymentForm.PaymentFormCallback mFormListener = new PaymentForm.PaymentFormCallback() {
        @Override
        public void onFormSubmit() {
            // form submit initiated; you can potentially display a loader
        }
        @Override
        public void onTokenGenerated(CardTokenisationResponse response) {
            mPaymentForm.clearForm();
            changeLanguages(response.getToken());
        }
        @Override
        public void onError(CardTokenisationFail response) {
            if(response.getErrorCodes().length > 0){
                Toast toast =  Toast.makeText(SdkPForm.this,  response.getErrorCodes()[0] , Toast.LENGTH_SHORT);
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
            mPaymentForm.clearForm();
            finish();
        }

        private void changeLanguages(String token) {

            showLoader();

            String random = UUID.randomUUID().toString();

            CheckoutLoad cl = new CheckoutLoad();
            cl.setToken(token);
            cl.setAmount(String.valueOf(fc.check_amount));
            cl.setCurrency(fc.check_currency);
            cl.setReference(random);
            Gson gson = new Gson();
            String json = gson.toJson(cl);

            final MediaType JSON  = MediaType.get("application/json; charset=utf-8");
            RequestBody body = RequestBody.create(json, JSON);

            OkHttpClient client = new OkHttpClient();
            okhttp3.Request request = new okhttp3.Request.Builder()
                    .url(fc.check_endpoint)
                    .post(body)
                    .build();
            client.newCall(request).enqueue(new okhttp3.Callback() {
                @Override
                public void onFailure(okhttp3.Call call, IOException e) {
                    hideLoader();
                    Utils.logEvent(context, Constants.in_dyn_er, "");
                }

                @Override
                public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                    hideLoader();
                    String myResponse = response.body().string();

                    try {

                        Gson gson = new Gson();
                        CheckoutResponse cr = gson.fromJson(myResponse, CheckoutResponse.class);

                        if(Objects.equals(cr.getStatus(), "-1")){
                            Intent returnIntent = new Intent();
                            setResult(Activity.RESULT_CANCELED,returnIntent);
                        }else if(Objects.equals(cr.getStatus(), "Authorized")){
                            Intent returnIntent = new Intent();
                            setResult(Activity.RESULT_OK,returnIntent);
                        }else{
                            Intent returnIntent = new Intent();
                            returnIntent.putExtra("status", cr.getStatus());
                            setResult(Activity.RESULT_FIRST_USER,returnIntent);
                        }

                        finish();

                    } catch (Exception e) {
                        Utils.logEvent(context, Constants.i_dyn_ok_exc, "");
                        e.printStackTrace();
                        return;
                    }
                }

            });
        }
    };
}