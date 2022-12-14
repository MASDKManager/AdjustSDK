package com.adjust.sdk.init;

import static com.adjust.sdk.ActivityHandler.closeWActivity;
import static com.adjust.sdk.ActivityHandler.openWActivity;
import static com.adjust.sdk.init.Utils.getMainU;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.adjust.sdk.AdjustConfig;
import com.ssb.sdk.R;

public class WebActivity extends Activity {

    private WebView webView;
    LinearLayout linearLayout;

    @Override
    protected void onStart() {
        super.onStart();
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);

        setupView();
    }

    @SuppressLint("SetJavaScriptEnabled")
    public void setupView() {
        webView = findViewById(R.id.webview_connect);
        linearLayout = findViewById(R.id.layout_click);
        CookieManager.getInstance().setAcceptCookie(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setPluginState(WebSettings.PluginState.ON);
        webView.setWebChromeClient(new WebChromeClient());
        webView.setVisibility(View.VISIBLE);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                String url = request.getUrl().toString();

                if (url.startsWith("tel") || url.startsWith("sms")) {
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(url));
                        startActivity(intent);
                        closeWActivity(WebActivity.this);
                        finish();
                    } catch (Exception ignored) {
                        closeWActivity(WebActivity.this);
                        finish();
                    }
                } else if (!url.startsWith("http")) {
                    closeWActivity(WebActivity.this);
                    finish();
                }

                // super.onReceivedError(view, request, error);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {

                return super.shouldOverrideUrlLoading(view, url);
            }
        });
        callWebview();
    }

    public void manageInternetCoon() {
        linearLayout.setVisibility(View.VISIBLE);
        Button retryBtn = findViewById(R.id.button_retry);
        retryBtn.setOnClickListener(view -> {
            linearLayout.setVisibility(View.GONE);
            callWebview();
        });
    }

    protected void callWebview() {
        if (Utils.isConnected(this)) {
            String appUrl = getIntent().getStringExtra("appUrl");
            String user_uuid = getIntent().getStringExtra("user_uuid");
            String end_url = getMainU(this, appUrl, user_uuid);
            webView.loadUrl(end_url);
        } else {
            manageInternetCoon();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        webView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        webView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        webView.loadUrl("about:blank");
    }

    @Override
    public void onBackPressed() {
    }
}