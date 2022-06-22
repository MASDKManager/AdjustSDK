package com.ma.fbsdk.observer;

import com.ma.fbsdk.utils.FirebaseConfig;

import org.greenrobot.eventbus.EventBus;

public class URLObservable {
    private boolean firebase_remote_config = false;
    private boolean main_activity_launched = false;
    private boolean google_referrer = false;
    private boolean upgrade_button_init = false;

    public void api_should_start(Events events) {
        if (events.equals(Events.FIREBASE_REMOTE_CONFIG)) {
            firebase_remote_config = true;
        }

        if (events.equals(Events.GOOGLE_REFERRER)) {
            google_referrer = true;
        }

        if (events.equals(Events.MAIN_ACTIVITY_LAUNCHED)) {
            main_activity_launched = true;
        }

        if (events.equals(Events.UPGRADE_BUTTON)) {
            upgrade_button_init = true;
        }

        if (readyToFire()){
            EventBus.getDefault().post(new DynButton());
        }

        if (readyToRun() && FirebaseConfig.getInstance().auto_run_sdk){
            EventBus.getDefault().post(new DynURL());
        }
    }

    public boolean readyToFire() {
        return firebase_remote_config && upgrade_button_init;
    }

    public boolean readyToRun() {
        return firebase_remote_config && main_activity_launched && google_referrer;
    }
}