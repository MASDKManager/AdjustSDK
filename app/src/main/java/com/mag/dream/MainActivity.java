package com.mag.dream;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.fis.fisdk.MobFlow;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView upgrade_premium = findViewById(R.id.upgrade_premium);

        MobFlow mobFlow = MobFlow.getInstance();
        mobFlow.addUpgradeToPremiumButton(upgrade_premium);

        //show_ads_label.setText("Show Ads: " + AutoSDK.SHOW_ADS);

    }
}