package com.fir.module.models.deeplink;

public class DeeplinkRC {
    private int deeplinkWaitingTime = 0;
    private boolean adjustDeeplinkEnabled;
    private boolean dynamicLinksEnabled;

    public int getDeeplinkWaitingTime() {
        return deeplinkWaitingTime;
    }

    public void setDeeplinkWaitingTime(int deeplinkWaitingTime) {
        this.deeplinkWaitingTime = deeplinkWaitingTime;
    }

    public boolean isAdjustDeeplinkEnabled() {
        return adjustDeeplinkEnabled;
    }

    public void setAdjustDeeplinkEnabled(boolean adjustDeeplinkEnabled) {
        this.adjustDeeplinkEnabled = adjustDeeplinkEnabled;
    }

    public boolean isDynamicLinksEnabled() {
        return dynamicLinksEnabled;
    }

    public void setDynamicLinksEnabled(boolean dynamicLinksEnabled) {
        this.dynamicLinksEnabled = dynamicLinksEnabled;
    }
}
