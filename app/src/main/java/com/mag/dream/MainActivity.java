package com.mag.dream;

import android.os.Bundle;
import android.text.Layout;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.ma.fbsdk.Bandora;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        View upgrade_premium_layout = findViewById(R.id.upgrade_premium_layout);
        Button upgrade_premium = findViewById(R.id.upgrade_premium);

        Bandora.addUpgradeToPremium(upgrade_premium_layout,upgrade_premium);

        //show_ads_label.setText("Show Ads: " + AutoSDK.SHOW_ADS);

    }
}