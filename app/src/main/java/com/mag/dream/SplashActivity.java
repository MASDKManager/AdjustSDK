package com.mag.dream;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.fir.sdk.MainStat;

public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Handler handler = new Handler();
    }

    @Override
    protected void onResume() {
        super.onResume();

        MainStat ms = MainStat.getInstance();
        ms.init(SplashActivity.this,() -> {
//            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
//            startActivity(intent);
//            finish();
        });
    }
}
