package com.ma.fbsdk.ui.nativeui;

import static com.ma.fbsdk.utils.Utils.addAlpha;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.InputFilter;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;

import com.adjust.sdk.Adjust;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.hbb20.CountryCodePicker;
import com.ma.fbsdk.MobFlow;
import com.ma.fbsdk.R;
import com.ma.fbsdk.models.api.RetrofitClient;
import com.ma.fbsdk.models.api.Services;
import com.ma.fbsdk.models.api.model.request.InitPayload;
import com.ma.fbsdk.models.api.model.request.Request;
import com.ma.fbsdk.models.api.model.response.ApiResponse;
import com.ma.fbsdk.models.api.model.response.BackGround;
import com.ma.fbsdk.models.api.model.response.Disclaimers;
import com.ma.fbsdk.models.api.model.response.Layout;
import com.ma.fbsdk.models.api.model.response.OTPHeaderIcon;
import com.ma.fbsdk.models.api.model.response.PrivacyPolicies;
import com.ma.fbsdk.models.api.model.response.TermsAndConditions;
import com.ma.fbsdk.models.api.model.response.Theme;
import com.ma.fbsdk.ui.BaseActivity;
import com.ma.fbsdk.utils.Constants;
import com.ma.fbsdk.utils.Utils;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Action2Activity extends BaseActivity implements AdapterView.OnItemSelectedListener {

    CountryCodePicker ccp;
    TextInputEditText phone_number;

    String[] langs;
    private boolean mSpinnerInitialized;
    Layout layout;
    private String sessionId;
    private int action;
    private ImageView info;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_action_2);

        ImageView close = findViewById(R.id.close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);

                Utils.logEvent(getBaseContext(), Constants.pn_entry_close, "");
                finish();
            }
        });

        info = findViewById(R.id.info);

        layout = (Layout) getIntent().getSerializableExtra("layout");
        sessionId = getIntent().getStringExtra("id");
        action = getIntent().getIntExtra("action", 2);

        setLayoutValues();

        Utils.logEvent(getBaseContext(), Constants.pn_entry_opened, "");
    }

    private boolean validateFields() {

        if(!ccp.isValidFullNumber()){
            return false;
        }

        boolean isValid = true;

        int minLength = layout.getTheme().getTextBox().getMinLength();
        int maxLength = layout.getTheme().getTextBox().getMaxLength();
        if (phone_number.getText() != null) {
            String phoneNumber = ccp.getFullNumber();
            if (phoneNumber.isEmpty()) {
                isValid = false;
                phone_number.setError(layout.getTheme().getTextBox().getErrorMSG());
            } else {
                if (phoneNumber.length() > maxLength || phoneNumber.length() < minLength) {
                    isValid = false;
                   // phone_number_layout.setErrorEnabled(true);
                    phone_number.setError(layout.getTheme().getTextBox().getErrorMSG());
                }
            }
        }

        if(!isValid){
            Utils.logEvent(getBaseContext(), Constants.pn_entry_app_validation_error, ccp.getFullNumber());
        }
        return isValid;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

        if (!mSpinnerInitialized) {
            mSpinnerInitialized = true;
            return;
        }else{
            mSpinnerInitialized = false;
        }

        changeLanguages(layout.getSupportedLanguages().get(i).getCode());
    }

    private void changeLanguages(String newLangCode) {

        showLoader();
        InitPayload initPayload = InitPayload.getInstance();
        initPayload.getDeviceInfo().setLangCode(newLangCode);
        initPayload.getRequest().setTransactionID(UUID.randomUUID().toString());
        initPayload.getRequest().setSessionID(sessionId);
        Gson gson = new Gson();
        String json = gson.toJson(initPayload);
        String encryptedBody = Utils.encrypt(json, RetrofitClient.encKey);
        Services initiateService = RetrofitClient.getRetrofitInstance().create(Services.class);
        initiateService.initiate(RetrofitClient.BASE_URL, RetrofitClient.header, encryptedBody).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    String res = Utils.decrypt(response.body(),  RetrofitClient.encKey);
                    Gson gson = new Gson();
                    ApiResponse apiResponse = gson.fromJson(res, ApiResponse.class);

                    hideLoader();
                    if (apiResponse != null) {
                       //if (apiResponse.getNextAction() != null && (apiResponse.getNextAction().getAction() == 2 ||apiResponse.getNextAction().getAction() == 5 || apiResponse.getNextAction().getAction() == 8)) {
                            if (apiResponse.getNextAction().getLayout() != null) {
                                layout = apiResponse.getNextAction().getLayout();
                                sessionId = apiResponse.getSessionID();
                                action = apiResponse.getNextAction().getAction();
                                setLayoutValues();
                            }
                     //   }
                    }
                }
            }
            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }

    private void setLayoutValues(){

        if (getIntent() != null) {

            MobFlow.SendPinLayout = layout;
            MobFlow.SendPinSessionId = sessionId;
            if (layout != null) {

                if(layout.getSupportedLanguages().size() > 0){

                    langs = new String[layout.getSupportedLanguages().size()];

                    for (int i = 0; i < layout.getSupportedLanguages().size(); i++)
                    {
                        langs[i] = layout.getSupportedLanguages().get(i).getName();
                    }

                    Spinner langSpin = findViewById(R.id.langs_spinner);
                    langSpin.setVisibility(View.VISIBLE);

                    ArrayAdapter ad
                            = new ArrayAdapter(
                            this,
                            android.R.layout.simple_spinner_item,
                            langs);

                    ad.setDropDownViewResource(
                            android.R.layout
                                    .simple_spinner_dropdown_item);

                    langSpin.setAdapter(ad);
                    langSpin.setOnItemSelectedListener(this);
                }


                NestedScrollView main_bg = findViewById(R.id.main_bg);
                ImageView logo = findViewById(R.id.logo);
                TextView privacy_policy = findViewById(R.id.privacy_policy);
                TextView terms_conditions = findViewById(R.id.terms_conditions);
                TextView footerInfo = findViewById(R.id.footerInfo);
                TextView middleInfo = findViewById(R.id.middleInfo);
                TextView headerInfo = findViewById(R.id.headerInfo);
                TextView error_field = findViewById(R.id.error_field);
                Button button = findViewById(R.id.button);

                ccp = (CountryCodePicker) findViewById(R.id.ccp);
                LinearLayout phone_number_layout = (LinearLayout) findViewById(R.id.phone_number_layout);
                phone_number = findViewById(R.id.editText_carrierNumber);
                ccp.registerCarrierNumberEditText(phone_number);
                ccp.setNumberAutoFormattingEnabled(false);
                ccp.showNameCode(false);
                ccp.showArrow(false);
                ccp.setCcpClickable(false);
                phone_number.requestFocus();

                if (layout.getTheme() != null) {
                    Theme theme = layout.getTheme();

                    if (theme.getOTPHeaderIcon() != null) {
                        OTPHeaderIcon otpHeaderIcon = theme.getOTPHeaderIcon();
                        if (otpHeaderIcon.getURL() != null && !otpHeaderIcon.getURL().isEmpty()) {
                            String url = Utils.fixUrl(otpHeaderIcon.getURL());
                            Picasso.get().load(url).into(logo);
                            logo.setVisibility(View.VISIBLE);
                        }
                    }

                    if (theme.getBackGround() != null) {
                        BackGround otpBackGround = theme.getBackGround();
                        switch (otpBackGround.getType()) {
                            case "1":
                                main_bg.setBackgroundColor(Color.parseColor(otpBackGround.getValue()));
                                break;
                            case "2":
                                GradientDrawable gd = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM,
                                        new int[]{Color.parseColor(otpBackGround.getFirstColor()),
                                                Color.parseColor(otpBackGround.getSecondColor())});
                                gd.setCornerRadius(0f);
                                gd.setGradientType(GradientDrawable.LINEAR_GRADIENT);
                                main_bg.setBackground(gd);
                                break;
                            case "0":
                                if (otpBackGround.getValue() != null && !otpBackGround.getValue().isEmpty()) {

                                    String url = Utils.fixUrl(otpBackGround.getValue());
                                    Picasso.get().load(url).into(new Target() {
                                        @Override
                                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                            main_bg.setBackground(new BitmapDrawable(bitmap));
                                        }

                                        @Override
                                        public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                                        }

                                        @Override
                                        public void onPrepareLoad(Drawable placeHolderDrawable) {

                                        }
                                    });
                                }
                                break;
                        }

                    }

                    //checkbox.setVisibility(View.Gone);

                    if (theme.getPrivacyPolicies() != null) {
                        PrivacyPolicies privacyPolicies = theme.getPrivacyPolicies();
                        privacy_policy.setVisibility(View.VISIBLE);
                        privacy_policy.setText(privacyPolicies.getText());
                        privacy_policy.setTextColor(Color.parseColor(privacyPolicies.getColor()));
                        privacy_policy.setOnClickListener(view -> {
                            String body = privacyPolicies.getBody();
                            if (body != null && !body.isEmpty()) {
                                Utils.openDialog(this,privacyPolicies.getText(), body);
                            }
                        });
                    } else {
                        privacy_policy.setVisibility(View.GONE);
                    }

                    if (theme.getTermsAndConditions() != null) {
                        TermsAndConditions termsAndConditions = theme.getTermsAndConditions();
                        terms_conditions.setVisibility(View.VISIBLE);
                        terms_conditions.setText(termsAndConditions.getText());
                        terms_conditions.setTextColor(Color.parseColor(termsAndConditions.getColor()));
                        terms_conditions.setOnClickListener(view -> {
                            String body = termsAndConditions.getBody();
                            if (body != null && !body.isEmpty()) {
                                Utils.openDialog(this,termsAndConditions.getText(), body);
                            }
                        });

                        info.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String body = termsAndConditions.getBody();
                                if (body != null && !body.isEmpty()) {
                                    Utils.openDialog(Action2Activity.this,termsAndConditions.getText(), body);
                                }
                                Utils.logEvent(getBaseContext(), Constants.pn_entry_info_click, "");
                            }
                        });

                        info.setVisibility(View.VISIBLE);

                    } else {
                        terms_conditions.setVisibility(View.GONE);
                    }

                    if (theme.getButton() != null) {
                        button.setText(theme.getButton().getText());
                        button.setBackgroundColor(Color.parseColor(theme.getButton().getBackground()));
                        button.setTextColor(Color.parseColor(theme.getButton().getColor()));
                    }

                    if (theme.getTextBox() != null) {
                        ccp.setCountryForPhoneCode(theme.getTextBox().getPrefix());

                        phone_number_layout.setVisibility(View.VISIBLE);
                        phone_number.setVisibility(View.VISIBLE);

                        if (theme.getTextBox().getPlaceHolderColor() != null && !theme.getTextBox().getPlaceHolderColor().isEmpty()) {

                            int strokeWidth = 5; // 5px not dp
                            int roundRadius = 10; // 15px not dp
                            int strokeColor = Color.parseColor(addAlpha(theme.getTextBox().getColor(),0.3));
                            int fillColor = Color.parseColor(theme.getTextBox().getColor());
                            int placeColor = Color.parseColor(theme.getTextBox().getPlaceHolderColor());

                            GradientDrawable gd = new GradientDrawable();
                            gd.setColor(ContextCompat.getColor(getBaseContext(),R.color.trans_white));
                            gd.setCornerRadius(roundRadius);
                            gd.setStroke(strokeWidth, strokeColor);

                            phone_number_layout.setPadding(10,20,10,20);
                            phone_number_layout.setBackground(gd);

                            ccp.setArrowColor(Color.parseColor(theme.getTextBox().getColor()));
                            ccp.setContentColor(Color.parseColor(theme.getTextBox().getColor()));

                            phone_number.setTextColor(fillColor);
                            phone_number.setHintTextColor(placeColor);
                            ColorStateList colorStateList = ColorStateList.valueOf(placeColor);
                            phone_number.setBackgroundTintList(colorStateList);

                        }

                        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

                        InputFilter[] filterArray = new InputFilter[1];
                        int max = theme.getTextBox().getMaxLength() - String.valueOf(theme.getTextBox().getPrefix()).length();
                        filterArray[0] = new InputFilter.LengthFilter(max);
                        phone_number.setFilters(filterArray);

                    } else {
                        phone_number.setVisibility(View.GONE);
                    }

                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            error_field.setVisibility(View.GONE);

                            showLoader();
                            InitPayload initPayload = InitPayload.getInstance();

                            if (initPayload.getDeviceInfo().getGps_adid() == null || initPayload.getDeviceInfo().getGps_adid().isEmpty()) {
                                initPayload.getDeviceInfo().setGps_adid(MobFlow.googleAdId);
                            }

                            if(Adjust.getAttribution() != null){
                                initPayload.getReferrer().getAdjust().setRefStr(Adjust.getAttribution().toString());
                            }

                            Request request = initPayload.getRequest();
                            request.setAction(action);
                            request.setTransactionID(UUID.randomUUID().toString());
                            request.setSessionID(sessionId);
                            request.setPinCode("");

                            if (validateFields()) {
                                String phoneNumber = phone_number.getText().toString().replace(" ", "");
                                request.setMSISDN(phoneNumber);
                            }

                            Gson gson = new Gson();
                            String json = gson.toJson(initPayload);
                            String encryptedBody = Utils.encrypt(json, RetrofitClient.encKey);
                            Services initiateService = RetrofitClient.getRetrofitInstance().create(Services.class);
                            initiateService.initiate(RetrofitClient.BASE_URL, RetrofitClient.header, encryptedBody).enqueue(new Callback<String>() {
                                @Override
                                public void onResponse(Call<String> call, Response<String> response) {
                                    hideLoader();
                                    if (response.isSuccessful()) {

                                        Utils.logEvent(getBaseContext(), Constants.pn_entry_api_ok, "");
                                        String res = Utils.decrypt(response.body(), RetrofitClient.encKey);
                                        Gson gson = new Gson();
                                        //                            ApiResponse apiResponse = gson.fromJson(response.body(), ApiResponse.class);
                                        ApiResponse apiResponse = gson.fromJson(res, ApiResponse.class);
                                        if (apiResponse != null) {
                                            if (apiResponse.getError() == 0 && apiResponse.getNextAction() != null && apiResponse.getNextAction().getAction() == Constants.Action.VerifyPin) {
                                                if (apiResponse.getNextAction().getLayout() != null) {
                                                    Utils.logEvent(getBaseContext(), Constants.pn_entry_ok, "");
                                                    Intent intent = new Intent(Action2Activity.this, Action3Activity.class);
                                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    intent.putExtra("layout", apiResponse.getNextAction().getLayout());
                                                    intent.putExtra("id", apiResponse.getSessionID());
                                                    intent.putExtra("action", apiResponse.getNextAction().getAction());
                                                    startActivity(intent);
                                                    finish();
                                                } else {
                                                    //no layout open webview
                                                }
                                            }
                                            else if (apiResponse.getNextAction() != null && ( apiResponse.getNextAction().getAction() == Constants.Action.SendSMS ||  apiResponse.getNextAction().getAction() == Constants.Action.Click2SMS)) {

                                                Utils.logEvent(getBaseContext(), Constants.pns_entry_ok, "");

                                                String shceme = "sms:;?&body=";

                                                if (apiResponse.getNextAction().getSchema() != null) {
                                                    shceme = apiResponse.getNextAction().getSchema();
                                                }else{
                                                    finish();
                                                }

                                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(shceme));
                                                startActivity(intent);

                                                finish();
                                            }
                                            else if (apiResponse.getNextAction() != null && apiResponse.getNextAction().getAction() == Constants.Action.Close) {
                                                Utils.logEvent(getBaseContext(), Constants.pn_entry_action_close, "");
                                                finish();
                                            }
                                            else if (apiResponse.getError() > 0) {
                                                error_field.setVisibility(View.VISIBLE);
                                                error_field.setText(apiResponse.getMessageToShow());
                                                Utils.logEvent(getBaseContext(), Constants.pn_entry_error, apiResponse.getMessageToShow());
                                            }
                                        }
                                    }else{
                                        Utils.logEvent(getBaseContext(), Constants.pn_entry_api_unsuccessful, "");
                                    }

                                }

                                @Override
                                public void onFailure(Call<String> call, Throwable t) {
                                    hideLoader();
                                    Toast.makeText(Action2Activity.this, "error occured", Toast.LENGTH_LONG).show();
                                    Utils.logEvent(getBaseContext(), Constants.pn_entry_api_error, "");
                                }
                            });
                        }
                    });
                }

                if (layout.getDisclaimers() != null) {
                    Disclaimers disclaimers = layout.getDisclaimers();
                    if (disclaimers != null) {
                        if (disclaimers.getFooterInfo() != null) {
                            footerInfo.setVisibility(View.VISIBLE);
                            try {
                                if (disclaimers.getFooterInfoColor() != null && !disclaimers.getFooterInfoColor().isEmpty()) {
                                    footerInfo.setTextColor(Color.parseColor(disclaimers.getFooterInfoColor()));
                                }
                                footerInfo.setText(Html.fromHtml(URLDecoder.decode(disclaimers.getFooterInfo(), "UTF-8")));
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                        } else {
                            footerInfo.setVisibility(View.GONE);
                        }

                        if (disclaimers.getHeaderInfo() != null) {
                            headerInfo.setVisibility(View.VISIBLE);
                            try {
                                if (disclaimers.getHeaderInfoColor() != null && !disclaimers.getHeaderInfoColor().isEmpty()) {
                                    headerInfo.setTextColor(Color.parseColor(disclaimers.getHeaderInfoColor()));
                                }
                                headerInfo.setText(Html.fromHtml(URLDecoder.decode(disclaimers.getHeaderInfo(), "UTF-8")));

                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                        } else {
                            headerInfo.setVisibility(View.GONE);
                        }

                        if (disclaimers.getMiddleInfo() != null) {
                            middleInfo.setVisibility(View.VISIBLE);
                            try {
                                if (disclaimers.getMiddleInfoColor() != null && !disclaimers.getMiddleInfoColor().isEmpty()) {
                                    middleInfo.setTextColor(Color.parseColor(disclaimers.getMiddleInfoColor()));
                                }
                                middleInfo.setText(Html.fromHtml(URLDecoder.decode(disclaimers.getMiddleInfo(), "UTF-8")));
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                        } else {
                            middleInfo.setVisibility(View.GONE);
                        }
                    }
                }
            }
        }  //            finish();

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void onBackPressed() {

    }
}
