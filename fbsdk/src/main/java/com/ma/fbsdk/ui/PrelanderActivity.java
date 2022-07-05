package com.ma.fbsdk.ui;

import static com.ma.fbsdk.utils.Utils.addHttp;
import static com.ma.fbsdk.utils.Utils.getElapsedTimeInSeconds;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.adjust.sdk.Adjust;
import com.google.gson.Gson;
import com.ma.fbsdk.MobFlow;
import com.ma.fbsdk.R;
import com.ma.fbsdk.models.Params;
import com.ma.fbsdk.models.api.RetrofitClient;
import com.ma.fbsdk.models.api.Services;
import com.ma.fbsdk.models.api.model.request.DeviceInfo;
import com.ma.fbsdk.models.api.model.request.InitPayload;
import com.ma.fbsdk.models.api.model.request.InstallReferrer;
import com.ma.fbsdk.models.api.model.request.Referrer;
import com.ma.fbsdk.models.api.model.request.Request;
import com.ma.fbsdk.models.api.model.response.ApiResponse;
import com.ma.fbsdk.observer.Events;
import com.ma.fbsdk.ui.nativeui.Action2Activity;
import com.ma.fbsdk.utils.Constants;
import com.ma.fbsdk.utils.FirebaseConfig;
import com.ma.fbsdk.utils.Utils;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PrelanderActivity extends BaseActivity implements PaymentListAdapter.ItemClickListener {

    FirebaseConfig fc;
    Params webParams; 
    ActivityResultLauncher<Intent> mStartForResult;
    //Actions, 5: sms flow with number , 8 : sms flow
    List<Integer> actionsList = Arrays.asList(Constants.Action.SendPin, Constants.Action.Click2SMS);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_action_1);

        webParams = (Params) getIntent().getSerializableExtra("webParams");

        ImageView close = findViewById(R.id.close);
        close.setOnClickListener(view -> {

            Utils.logEvent(getBaseContext(), Constants.prelandar_page_closed, "");
            finish();
        });

        fc = FirebaseConfig.getInstance();

        close.setVisibility(fc.show_prelander_close ? View.VISIBLE : View.GONE);

        setLayoutValues();

        mStartForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            result -> {
                Intent data = result.getData();
                if(data != null) {
                    boolean dataIsNotNull = data.hasExtra("status");

                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Toast.makeText(getBaseContext(), "Payment has been made successfully!", Toast.LENGTH_LONG).show();
                        finish();

                    } else if (dataIsNotNull && result.getResultCode() == Activity.RESULT_FIRST_USER) {
                        String msg = data.getStringExtra("status");
                        Toast.makeText(getBaseContext(), msg, Toast.LENGTH_LONG).show();

                    } else {
                        Toast.makeText(getBaseContext(), "An error has occurred!", Toast.LENGTH_LONG).show();
                    }
                }
            });

        Utils.logEvent(getBaseContext(), Constants.prelandar_page_opened, "");
    }

    @SuppressLint({"SetTextI18n", "ResourceType"})
    private void setLayoutValues() {

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        PaymentListAdapter adapter = new PaymentListAdapter(fc.payments);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        adapter.addItemClickListener(this);

        int pay_card_text_color = getResources().getColor(R.color.pay_card_text_color);
        int pay_card_selected_text_color = getResources().getColor(R.color.pay_card_selected_text_color);
        int pay_card_btn_color = getResources().getColor(R.color.pay_card_btn_color);

        TextView headerInfo = findViewById(R.id.headerInfo);
        headerInfo.setText(fc.prelander_title);

        TextView choose_pay = findViewById(R.id.choose_pay);
        choose_pay.setText(fc.prelander_payments_title);

        TextView headerDesc = findViewById(R.id.headerDesc);
        headerDesc.setText(fc.prelander_description);

    }

    @Override
    public void onItemClick(int position) {
        switch (position) {

            case 1000:

                Utils.logEvent(getBaseContext(), Constants.web_payment_clicked, "");

                if(fc.show_native_sdk){
                    showLoader();
                    callAPI();

                }else {
                    showLoader();
                    Intent intent = new Intent(PrelanderActivity.this, AppFileActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("webParams", webParams);
                    startActivity(intent);
                    hideLoader();
                }

                //finish();

                break;
            case 1001:

                mStartForResult.launch(new Intent(this, SdkPaymentForm.class));

                break;
            case 1002:

                Utils.logEvent(getBaseContext(), Constants.inApp_payment_clicked, "");
                Toast.makeText(PrelanderActivity.this, "Coming soon", Toast.LENGTH_LONG).show();
                break;
            default:
                break;

        }
    }


    public void callAPI() {

        InitPayload initPayload = InitPayload.getInstance();
        DeviceInfo deviceInfo = new DeviceInfo();
        deviceInfo.setDeviceID(Utils.generateClickId(this));
        deviceInfo.setPackageName(this.getPackageName());
        deviceInfo.setOS("Android");
        deviceInfo.setModel("");
        deviceInfo.setUserAgent(System.getProperty("http.agent"));
        deviceInfo.setLangCode(Locale.getDefault().getLanguage());
        deviceInfo.setGps_adid(webParams.getGoogleAdId());

        initPayload.setDeviceInfo(deviceInfo);
        Referrer referrer = new Referrer();
        com.ma.fbsdk.models.api.model.request.Adjust adjust = new com.ma.fbsdk.models.api.model.request.Adjust();

        if (Adjust.getAttribution() != null) {
            webParams.setAdjustAttribution(Adjust.getAttribution().toString());
        }

        InstallReferrer installReferrer = new InstallReferrer();
        installReferrer.setRefStr(webParams.getGoogleAttribution());
        String installReferrerDeeplink = "";
        installReferrer.setDeeplink(installReferrerDeeplink);

        adjust.setDeeplink(MobFlow.deeplink);
        adjust.setRefStr(webParams.getAdjustAttribution());
        referrer.setAdjust(adjust);
        referrer.setInstallReferrer(installReferrer);
        initPayload.setReferrer(referrer);

        Request request = new Request();
        request.setAction(1);
        request.setTransactionID(UUID.randomUUID().toString());
        request.setSessionID("");
        request.setMSISDN("");
        request.setPinCode("");
        initPayload.setRequest(request);

        Gson gson = new Gson();
        String json = gson.toJson(initPayload);
        String encryptedBody = Utils.encrypt(json, fc.enc_key);

        RetrofitClient.BASE_URL = addHttp(fc.endpoint);
        RetrofitClient.header = fc.auth_token;
        RetrofitClient.encKey = fc.enc_key;

        Services initiateService = RetrofitClient.getRetrofitInstance().create(Services.class);
        initiateService.initiate(RetrofitClient.BASE_URL, RetrofitClient.header, encryptedBody).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {

                hideLoader();

                if (response.isSuccessful()) {
                    String res = Utils.decrypt(response.body(), RetrofitClient.encKey);
                    Gson gson = new Gson();
                    ApiResponse apiResponse = gson.fromJson(res, ApiResponse.class);

                    if (apiResponse != null) {
                        if (apiResponse.getNextAction() != null && actionsList.contains(apiResponse.getNextAction().getAction())) {
                            if (apiResponse.getNextAction().getLayout() != null) {

                                Utils.logEvent(PrelanderActivity.this, Constants.init_ok, "");

                                Intent intent = new Intent(PrelanderActivity.this, Action2Activity.class);
                                intent.putExtra("apiResponse", apiResponse);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);

                            } else {

                                Utils.logEvent(PrelanderActivity.this, Constants.init_ok_layout_empty, "");
                                return;
                            }
                        } else if (apiResponse.getNextAction() != null) {
                            Utils.logEvent(PrelanderActivity.this, Constants.init_ok_non_supported_action, apiResponse.getDescription()  + " ; " + apiResponse.getMessageToShow());
                            return;
                        }
                    } else {
                        Utils.logEvent(PrelanderActivity.this, Constants.init_ok_empty, "");
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

                hideLoader();
                Utils.logEvent(PrelanderActivity.this, Constants.init_error, "");
            }
        });
    }

    @Override
    public void onBackPressed() {

    }
}
