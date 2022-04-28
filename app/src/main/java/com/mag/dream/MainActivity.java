package com.mag.dream;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView show_ads_label = findViewById(R.id.show_ads_label);

//        show_ads_label.setText("Show Ads: " + AutoSDK.SHOW_ADS);

    }
}