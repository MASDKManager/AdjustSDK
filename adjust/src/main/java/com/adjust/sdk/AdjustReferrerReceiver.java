package com.adjust.sdk;

import static com.adjust.sdk.Constants.REFERRER;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

// support multiple BroadcastReceivers for the INSTALL_REFERRER:
// https://appington.wordpress.com/2012/08/01/giving-credit-for-android-app-installs/

public class AdjustReferrerReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String rawReferrer = intent.getStringExtra(REFERRER);

        if (null == rawReferrer) {
            return;
        }

        Adjust.getDefaultInstance().sendReferrer(rawReferrer, context);
    }
}
