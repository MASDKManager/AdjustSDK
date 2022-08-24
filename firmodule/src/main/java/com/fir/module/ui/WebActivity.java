package com.fir.module.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;

import com.fir.module.R;
import com.fir.module.utils.Constants;

public class WebActivity extends BaseActivity {
    private WebView webView;
    LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);

        hideLoader();
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
        webView.setWebChromeClient(new WebChromeClient());
        webView.setVisibility(View.VISIBLE);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
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
        if (Constants.isConnected(this)) {
            String sub_endu = getIntent().getStringExtra(Constants.sub_endu);
            webView.loadUrl( sub_endu );
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