package com.mag.dream;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.ma.fbsdk.MobFlow;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button upgrade_premium = findViewById(R.id.upgrade_premium);

        MobFlow mobFlow = MobFlow.getInstance();
        mobFlow.addUpgradeToPremiumButton(upgrade_premium);

        //show_ads_label.setText("Show Ads: " + AutoSDK.SHOW_ADS);

    }
}