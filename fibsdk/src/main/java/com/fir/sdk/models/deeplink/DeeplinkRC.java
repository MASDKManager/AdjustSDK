package com.fir.sdk.models.deeplink;

public class DeeplinkRC {
    private boolean adjustDeeplinkEnabled;
    private boolean dynamicLinksEnabled;

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
