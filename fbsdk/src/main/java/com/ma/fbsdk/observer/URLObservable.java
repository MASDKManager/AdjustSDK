package com.ma.fbsdk.observer;

import com.ma.fbsdk.utils.FirebaseConfig;

import org.greenrobot.eventbus.EventBus;

public class URLObservable {
    private boolean firebase_remote_config = false;
    private boolean google_referrer = false;

    public void api_should_start(Events events) {
        if (events.equals(Events.FIREBASE_REMOTE_CONFIG)) {
            firebase_remote_config = true;
        }

        if (events.equals(Events.GOOGLE_REFERRER)) {
            google_referrer = true;
        }

        if (readyToRun()){
            EventBus.getDefault().post(new DynURL());
        }
    }

    public boolean readyToRun() {
        return firebase_remote_config && google_referrer;
    }
}