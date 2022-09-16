package com.ssb.sdk.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.Toolbar;

import com.braintreepayments.cardform.view.CardForm;
import com.checkout.android_sdk.CheckoutAPIClient;
import com.checkout.android_sdk.Request.CardTokenisationRequest;
import com.checkout.android_sdk.Response.CardTokenisationFail;
import com.checkout.android_sdk.Response.CardTokenisationResponse;
import com.checkout.android_sdk.Utils.Environment;
import com.checkout.android_sdk.network.NetworkError;
import com.google.gson.Gson;
import com.ssb.sdk.R;
import com.ssb.sdk.models.checkout.CheckoutLoad;
import com.ssb.sdk.models.checkout.CheckoutResponse;
import com.ssb.sdk.utils.Constants;
import com.ssb.sdk.utils.FirebaseConfig;
import com.ssb.sdk.utils.Utils;
import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

public class SdkPForm extends BaseActivity {

    private CheckoutAPIClient mCheckoutAPIClient;
    CardForm cardForm ;
    FirebaseConfig fc;
    Context context;

    @SuppressLint("SetTextI18n")
    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sdk_pa_form);

        fc = FirebaseConfig.getInstance();

        cardForm = (CardForm) findViewById(R.id.card_form);
        cardForm.cardRequired(true)
                .expirationRequired(true)
                .cvvRequired(true)
                .cardholderName(CardForm.FIELD_REQUIRED)
                .mobileNumberRequired(true)
                .setup(this);

        context = this;

        mCheckoutAPIClient = new CheckoutAPIClient(
                this,                // context
                fc.check_token,          // your public key
                Environment.LIVE  // the environment
        );

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setTitle(fc.sub_p_header);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);

                Utils.logEvent(getBaseContext(), Constants.ch_cl_se, "");
                finish();
            }
        });

        Button button = findViewById(R.id.button);

        int pay_card_btn_color = this.getResources().getColor(R.color.pay_card_btn_color);
        button.setBackgroundColor(pay_card_btn_color);
        button.setText("Pay " + (fc.check_amount / 100) + " " + fc.check_currency);

        button.setOnClickListener( view -> {

            if (cardForm.isValid()) {
                mCheckoutAPIClient.setTokenListener(mTokenListener); // pass the callback
                // Pass the payload and generate the token
                mCheckoutAPIClient.generateToken(
                        new CardTokenisationRequest(
                                cardForm.getCardNumber(),
                                cardForm.getCardholderName(),
                                cardForm.getExpirationMonth(),
                                cardForm.getExpirationYear(),
                                cardForm.getCvv()
                        )
                );
            }else{
                cardForm.validate();
            }
        });
    }

    CheckoutAPIClient.OnTokenGenerated mTokenListener = new CheckoutAPIClient.OnTokenGenerated() {
        @Override
        public void onTokenGenerated(CardTokenisationResponse token) {
            callAPI(token.getToken());
            fc.webParams.setPhoneNumber(cardForm.getCountryCode() + cardForm.getMobileNumber());
        }
        @Override
        public void onError(CardTokenisationFail error) {
            if(error.getErrorCodes().length > 0){
                Toast toast =  Toast.makeText(SdkPForm.this,  error.getErrorCodes()[0] , Toast.LENGTH_SHORT);
                toast.show();
            }
        }
        @Override
        public void onNetworkError(NetworkError error) {
            // your network error
        }
    };

    private String genURL(){
        String server = fc.check_endpoint + "?firebase_instance_id=" + fc.webParams.getFirebaseInstanceId() + "&phone_number=" + cardForm.getCountryCode() + cardForm.getMobileNumber();
        return server;
    }

    private void callAPI(String token) {

        showLoader();

        String random = UUID.randomUUID().toString();
        String url =  genURL();

        CheckoutLoad cl = new CheckoutLoad();
        cl.setToken(token);
        cl.setAmount(String.valueOf(fc.check_amount));
        cl.setCurrency(fc.check_currency);
        cl.setReference(random);
        cl.setTest(false);
        Gson gson = new Gson();
        String json = gson.toJson(cl);

        final MediaType JSON  = MediaType.get("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(json, JSON);

        OkHttpClient client = new OkHttpClient();
        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(url)
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
}