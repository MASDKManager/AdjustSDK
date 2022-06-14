package com.ma.awsdk.observer;


import org.greenrobot.eventbus.EventBus;

public class URLObservable {
    private boolean dynamo = false;
    private boolean main_activity_launched = false;
    private boolean google_referrer = false;

    public void api_should_start(Events events) {
        if (events.equals(Events.DYNAMO)) {
            dynamo = true;
        }

        if (events.equals(Events.GOOGLE_REFERRER)) {
            google_referrer = true;
        }

        if (events.equals(Events.MAIN_ACTIVITY_LAUNCHED)) {
            main_activity_launched = true;
        }

        if (readyToRun()) {
            EventBus.getDefault().post(new DynURL());
        }
    }

    public boolean readyToRun() {
        return dynamo && main_activity_launched && google_referrer;
    }
}