package com.mag.dream;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.fir.module.MainStat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView up = findViewById(R.id.u_p);
        MainStat ms = MainStat.getInstance();
        ms.startStat(up);

        //show_ads_label.setText("Show Ads: " + AutoSDK.SHOW_ADS);

    }
}