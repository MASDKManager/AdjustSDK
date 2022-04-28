package com.ma.autosdk.ui;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.Html;
import android.text.InputFilter;
import android.view.View;
import android.view.Window;
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
import androidx.core.widget.NestedScrollView;

import com.adjust.sdk.Adjust;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.hbb20.CountryCodePicker;
import com.ma.autosdk.Bandora;
import com.ma.autosdk.CustomDialogFragment;
import com.ma.autosdk.R;
import com.ma.autosdk.Utils;
import com.ma.autosdk.api.RetrofitClient;
import com.ma.autosdk.api.Services;
import com.ma.autosdk.api.model.request.InitPayload;
import com.ma.autosdk.api.model.request.Request;
import com.ma.autosdk.api.model.response.ApiResponse;
import com.ma.autosdk.api.model.response.BackGround;
import com.ma.autosdk.api.model.response.Disclaimers;
import com.ma.autosdk.api.model.response.Layout;
import com.ma.autosdk.api.model.response.OTPHeaderIcon;
import com.ma.autosdk.api.model.response.PrivacyPolicies;
import com.ma.autosdk.api.model.response.TermsAndConditions;
import com.ma.autosdk.api.model.response.Theme;
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
    private LinearLayout phone_number_layout;

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
                firebaseLog("Button screen closed", "");
                finish();
            }
        });

        layout = (Layout) getIntent().getSerializableExtra("layout");
        sessionId = getIntent().getStringExtra("id");
        action = getIntent().getIntExtra("action", 2);

        setLayoutValues();

        firebaseLog("Button screen opened", "");
    }

    private void openDialog(String title, String body) {
        CustomDialogFragment cdf = new CustomDialogFragment(this, title, body);
        cdf.requestWindowFeature(Window.FEATURE_NO_TITLE);
        cdf.show();
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

                    if (apiResponse != null) {
                        if (apiResponse.getNextAction() != null && (apiResponse.getNextAction().getAction() == 5 || apiResponse.getNextAction().getAction() == 8)) {
                            if (apiResponse.getNextAction().getLayout() != null) {
                                layout = apiResponse.getNextAction().getLayout();
                                sessionId = apiResponse.getSessionID();
                                action = apiResponse.getNextAction().getAction();
                                setLayoutValues();
                            }
                        }
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

            Bandora.SendPinLayout = layout;
            Bandora.SendPinSessionId = sessionId;
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
                phone_number_layout = (LinearLayout) findViewById(R.id.phone_number_layout);
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
                        if(otpBackGround.getType().equals("1")){
                            main_bg.setBackgroundColor(Color.parseColor(otpBackGround.getValue()));
                        }else if  (otpBackGround.getType().equals("2")){
                            GradientDrawable gd = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM,
                                    new int[]{Color.parseColor(otpBackGround.getFirstColor()),
                                            Color.parseColor(otpBackGround.getSecondColor())});
                            gd.setCornerRadius(0f);
                            gd.setGradientType(GradientDrawable.LINEAR_GRADIENT);
                            main_bg.setBackgroundDrawable(gd);
                        }else if (otpBackGround.getType().equals("0")){
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
                        }

                    }

                    if (theme.getCheckBox() != null) {

                    } else {
                        //checkbox.setVisibility(View.Gone);
                    }

                    if (theme.getPrivacyPolicies() != null) {
                        PrivacyPolicies privacyPolicies = theme.getPrivacyPolicies();
                        privacy_policy.setVisibility(View.VISIBLE);
                        privacy_policy.setText(privacyPolicies.getText());
                        privacy_policy.setTextColor(Color.parseColor(privacyPolicies.getColor()));
                        privacy_policy.setOnClickListener(view -> {
                            String body = privacyPolicies.getBody();
                            if (body != null && !body.isEmpty()) {
                                openDialog(privacyPolicies.getText(), body);
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
                                openDialog(termsAndConditions.getText(), body);
                            }
                        });
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
                            phone_number.setHintTextColor(ColorStateList.valueOf(Color.parseColor(theme.getTextBox().getPlaceHolderColor())));
                        }

                        phone_number.setTextColor(ColorStateList.valueOf(Color.parseColor(theme.getTextBox().getColor())));
                        phone_number.requestFocus();

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
                                initPayload.getDeviceInfo().setGps_adid(Bandora.googleAdId);
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

                                        firebaseLog("Button clicked success", "");
                                        String res = Utils.decrypt(response.body(), RetrofitClient.encKey);
                                        Gson gson = new Gson();
                                        //                            ApiResponse apiResponse = gson.fromJson(response.body(), ApiResponse.class);
                                        ApiResponse apiResponse = gson.fromJson(res, ApiResponse.class);
                                        if (apiResponse != null) {
                                            if (apiResponse.getError() == 0 && apiResponse.getNextAction() != null && apiResponse.getNextAction().getAction() == 3) {
                                                if (apiResponse.getNextAction().getLayout() != null) {
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
                                            } else if (apiResponse.getError() > 0) {
                                                error_field.setVisibility(View.VISIBLE);
                                                error_field.setText(apiResponse.getMessageToShow());
                                                firebaseLog("Button clicked failed", apiResponse.getMessageToShow());
                                            }
                                            else  if (apiResponse.getNextAction() != null && apiResponse.getNextAction().getAction() == 7) {
                                                finish();
                                            }
                                        }else{
                                            firebaseLog("Button parsing error", "");
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<String> call, Throwable t) {
                                    hideLoader();
                                    Toast.makeText(Action2Activity.this, "error occured", Toast.LENGTH_LONG).show();
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
                                    phone_number.setTextColor(Color.parseColor(disclaimers.getHeaderInfoColor()));

                                    ccp.setArrowColor(Color.parseColor(disclaimers.getHeaderInfoColor()));
                                    ccp.setContentColor(Color.parseColor(disclaimers.getHeaderInfoColor()));

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
        } else {
//            finish();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }


}
