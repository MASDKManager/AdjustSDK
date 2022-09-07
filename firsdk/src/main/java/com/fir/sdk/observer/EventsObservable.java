package com.fir.sdk.observer;

import org.greenrobot.eventbus.EventBus;

public class EventsObservable {
    private boolean firebase_Instnce_ID = false;
    private boolean firebase_Received = false;
    private boolean google_Referrer = false;

    public void ads_start(Events events) {
        if (events.equals(Events.Firebase_Received)) {
            firebase_Received = true;
        }

        if (events.equals(Events.Google_Referrer)) {
            google_Referrer = true;
        }

        if (events.equals(Events.Firebase_Instnce_ID)) {
            firebase_Instnce_ID = true;
        }

        if (readyToRun()) {
            EventBus.getDefault().post(new StartEvent());
        }
    }

    public boolean readyToRun() {
        //TODO add check in case of deeplink or waiting time has passed
        return firebase_Received && firebase_Instnce_ID && google_Referrer ;

    }
}