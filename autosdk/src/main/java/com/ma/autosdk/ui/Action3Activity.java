package com.ma.autosdk.ui;

import static java.sql.Types.NUMERIC;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Html;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.adjust.sdk.Adjust;
import com.adjust.sdk.AdjustAttribution;
import com.chaos.view.PinView;
import com.google.gson.Gson;
import com.ma.autosdk.Bandora;
import com.ma.autosdk.R;
import com.ma.autosdk.Utils;
import com.ma.autosdk.api.RetrofitClient;
import com.ma.autosdk.api.Services;
import com.ma.autosdk.api.model.request.InitPayload;
import com.ma.autosdk.api.model.request.Request;
import com.ma.autosdk.api.model.response.ApiResponse;
import com.ma.autosdk.api.model.response.AskCodeAgain;
import com.ma.autosdk.api.model.response.BackGround;
import com.ma.autosdk.api.model.response.Disclaimers;
import com.ma.autosdk.api.model.response.Layout;
import com.ma.autosdk.api.model.response.OTPHeaderIcon;
import com.ma.autosdk.api.model.response.Theme;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Action3Activity extends BaseActivity {
    TextView error_field;
    TextView countdown;
    TextView ask_code_again;
    String sessionId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_action_3);
        PinView otp_view = findViewById(R.id.otp_view);
        Button verify = findViewById(R.id.verify);
        TextView headerInfo = findViewById(R.id.headerInfo);
        TextView wrong_numb = findViewById(R.id.wrong_numb);
        ask_code_again = findViewById(R.id.ask_code_again);
        countdown = findViewById(R.id.countdown);
        error_field = findViewById(R.id.error_field);

        ImageView close = findViewById(R.id.close);

        close.setOnClickListener(view -> {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
            firebaseLog("Verify phone screen closed", "");
            finish();
        });

        if (getIntent() != null) {
            Layout layout = (Layout) getIntent().getSerializableExtra("layout");
            sessionId = getIntent().getStringExtra("id");


            int action = getIntent().getIntExtra("action", 3);
            if (layout != null) {
                ConstraintLayout main_bg = findViewById(R.id.main_bg);
                ImageView logo = findViewById(R.id.logo);
                TextView footerInfo = findViewById(R.id.footerInfo);
                TextView middleInfo = findViewById(R.id.middleInfo);

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

                if (layout.getTheme() != null) {
                    Theme theme = layout.getTheme();

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

                    if (theme.getOTPHeaderIcon() != null) {
                        OTPHeaderIcon otpHeaderIcon = theme.getOTPHeaderIcon();
                        if (otpHeaderIcon.getURL() != null && !otpHeaderIcon.getURL().isEmpty()) {
                            String url = Utils.fixUrl(otpHeaderIcon.getURL());
                            Picasso.get().load(url).into(logo);
                        }
                    }

                    if (theme.getCheckBox() != null) {

                    } else {
                        //checkbox.setVisibility(View.Gone);
                    }

                    if (theme.getButton() != null) {
                        verify.setText(theme.getButton().getText());
                        try {
                            if (theme.getButton().getBackground() != null) {
                                verify.setBackgroundColor(Color.parseColor(theme.getButton().getBackground()));
                            }
                            verify.setTextColor(Color.parseColor(theme.getButton().getColor()));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    if (theme.getTextBox() != null) {
                        otp_view.setVisibility(View.VISIBLE);
                        otp_view.setHint(theme.getTextBox().getText());
                        if (theme.getTextBox().getPlaceHolderColor() != null) {
                            otp_view.setHintTextColor(ColorStateList.valueOf(Color.parseColor(theme.getTextBox().getPlaceHolderColor())));
                            otp_view.setLineColor(ColorStateList.valueOf(Color.parseColor(theme.getTextBox().getPlaceHolderColor())));
                        }
                        otp_view.setItemCount(theme.getTextBox().getLength());

                        if (theme.getTextBox().getIsNumeric() == 1) {
                            otp_view.setInputType(NUMERIC);
                        }
                        otp_view.requestFocus();
                    } else {
                        otp_view.setVisibility(View.GONE);
                    }

                    if (theme.getWrongNumber() != null) {
                        wrong_numb.setVisibility(View.VISIBLE);
                        wrong_numb.setText(theme.getWrongNumber().getText());
                        wrong_numb.setTextColor(Color.parseColor(theme.getWrongNumber().getColor()));
                    } else {
                        wrong_numb.setVisibility(View.GONE);
                    }

                    wrong_numb.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            wrongNumber();
                        }
                    });

                    if (theme.getAskCodeAgain() != null) {
                        defineResendPin(theme.getAskCodeAgain());
                    }

                    ask_code_again.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            callSendPinAgain();
                        }
                    });

                    verify.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            showLoader();
                            InitPayload initPayload = InitPayload.getInstance();

                            if (initPayload.getDeviceInfo().getGps_adid() == null || initPayload.getDeviceInfo().getGps_adid().isEmpty()) {
                                initPayload.getDeviceInfo().setGps_adid(Bandora.googleAdId);
                            }

                            AdjustAttribution attribution = Adjust.getAttribution();

                            if (initPayload.getReferrer().getAdjust() != null && attribution != null) {
                                initPayload.getReferrer().getAdjust().setRefStr(attribution.toString());
                            }

                            Request request = initPayload.getRequest();
                            request.setAction(action);
                            request.setTransactionID(UUID.randomUUID().toString());
                            request.setSessionID(sessionId);
                            request.setPinCode(otp_view.getText().toString());

                            Gson gson = new Gson();
                            String json = gson.toJson(initPayload);
                            String encryptedBody = Utils.encrypt(json, RetrofitClient.encKey);
                            Services initiateService = RetrofitClient.getRetrofitInstance().create(Services.class);
                            initiateService.initiate(RetrofitClient.BASE_URL, RetrofitClient.header, encryptedBody).enqueue(new Callback<String>() {
                                @Override
                                public void onResponse(Call<String> call, Response<String> response) {
                                    hideLoader();
                                    firebaseLog("Verify phone clicked", "");
                                    if (response.isSuccessful()) {
                                        String res = Utils.decrypt(response.body(), RetrofitClient.encKey);
                                        Gson gson = new Gson();
                                        ApiResponse apiResponse = gson.fromJson(res, ApiResponse.class);
                                        if (apiResponse.getError() == 0 && apiResponse.getNextAction() != null && apiResponse.getNextAction().getAction() == 7) {
                                            finish();
                                        } else if (apiResponse.getError() > 0) {
                                            error_field.setVisibility(View.VISIBLE);
                                            error_field.setText(apiResponse.getMessageToShow());
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<String> call, Throwable t) {
                                    hideLoader();
                                    Toast.makeText(Action3Activity.this, "error occured", Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    });
                }
            }
        }
    }

    private void wrongNumber() {
        Intent intent = new Intent(Action3Activity.this, Action2Activity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("layout", Bandora.SendPinLayout);
        intent.putExtra("id", Bandora.SendPinSessionId);
        int action = getIntent().getIntExtra("action", 3);
        action = action - 1;
        intent.putExtra("action", action);
        startActivity(intent);
        finish();
    }

    private void callSendPinAgain() {
        error_field.setVisibility(View.GONE);
        showLoader();
        InitPayload initPayload = InitPayload.getInstance();
        if (initPayload.getDeviceInfo().getGps_adid() == null || initPayload.getDeviceInfo().getGps_adid().isEmpty()) {
            initPayload.getDeviceInfo().setGps_adid(Bandora.googleAdId);
        }

        AdjustAttribution attribution = Adjust.getAttribution();

        if (initPayload.getReferrer().getAdjust() != null && attribution != null) {
            initPayload.getReferrer().getAdjust().setRefStr(attribution.toString());
        }

        Request request = initPayload.getRequest();
        request.setTransactionID(UUID.randomUUID().toString());
        Gson gson = new Gson();
        String json = gson.toJson(initPayload);
        String encryptedBody = Utils.encrypt(json, RetrofitClient.encKey);
        Services initiateService = RetrofitClient.getRetrofitInstance().create(Services.class);
        initiateService.initiate(RetrofitClient.BASE_URL, RetrofitClient.header, encryptedBody).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                hideLoader();
                if (response.isSuccessful()) {

                    firebaseLog("Verify phone again clicked", "");
                    String res = Utils.decrypt(response.body(), RetrofitClient.encKey);
                    Gson gson = new Gson();
                    ApiResponse apiResponse = gson.fromJson(res, ApiResponse.class);
                    if (apiResponse.getError() == 0 && apiResponse.getNextAction() != null && apiResponse.getNextAction().getAction() == 3) {
                        defineResendPin(apiResponse.getNextAction().getLayout().getTheme().getAskCodeAgain());
                        sessionId = apiResponse.getSessionID();
                    } else if (apiResponse.getError() > 0) {
                        try {
                            error_field.setVisibility(View.VISIBLE);
                            error_field.setText(apiResponse.getMessageToShow());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    if (apiResponse.getNextAction() != null && apiResponse.getNextAction().getAction() == 7) {
                        finish();
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                hideLoader();
                Toast.makeText(Action3Activity.this, "error occured", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void defineResendPin(AskCodeAgain askCodeAgain) {
        if (askCodeAgain != null) {
            ask_code_again.setEnabled(false);
            ask_code_again.setVisibility(View.VISIBLE);
            countdown.setVisibility(View.VISIBLE);
            ask_code_again.setText(askCodeAgain.getText());
            ask_code_again.setTextColor(Color.parseColor(askCodeAgain.getColor()));
            NumberFormat f = new DecimalFormat("00");
            long min = ((askCodeAgain.getCountDown() * 1000) / 60000) % 60;
            long sec = ((askCodeAgain.getCountDown() * 1000) / 1000) % 60;
            countdown.setText(f.format(min) + ":" + f.format(sec));
            ask_code_again.setEnabled(false);
            new CountDownTimer(askCodeAgain.getCountDown() * 1000L, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    // Used for formatting digit to be in 2 digits only
                    NumberFormat f = new DecimalFormat("00");
                    long min = (millisUntilFinished / 60000) % 60;
                    long sec = (millisUntilFinished / 1000) % 60;
                    countdown.setText(f.format(min) + ":" + f.format(sec));
                }

                @Override
                public void onFinish() {
                    ask_code_again.setEnabled(true);
                    countdown.setText("00:00");
                }
            }.start();
        } else {
            ask_code_again.setVisibility(View.GONE);
        }
    }
}