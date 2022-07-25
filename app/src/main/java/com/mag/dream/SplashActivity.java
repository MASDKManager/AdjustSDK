package com.mag.dream;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.fis.fisdk.MobFlow;

public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Handler handler = new Handler();
        Log.d("MagDream", "start");

        MobFlow mobFlow = MobFlow.getInstance();
        mobFlow.init(SplashActivity.this,() -> {
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);
            Log.d("MagDream", "finish");
            finish();

        });

    }
}
