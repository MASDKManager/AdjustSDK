package com.ssb.sdk.models;

public class AdjustRC {
    private Boolean enabled;
    private String appToken;
    private String appInstanceIDEventToken;
    private String attrLogEventToken;
    private long callbackDelay;

    public String getAttrLogEventToken() {
        return attrLogEventToken;
    }

    public void setAttrLogEventToken(String attrLogEventToken) {
        this.attrLogEventToken = attrLogEventToken;
    }

    public long getCallbackDelay() {
        return callbackDelay;
    }

    public void setCallbackDelay(long callbackDelay) {
        this.callbackDelay = callbackDelay;
    }

    public Boolean getEnabled() { return enabled; }
    public void setEnabled(Boolean value) { this.enabled = value; }

    public String getAppToken() { return appToken; }
    public void setAppToken(String value) { this.appToken = value; }

    public String getAppInstanceIDEventToken() { return appInstanceIDEventToken; }
    public void setAppInstanceIDEventToken(String value) { this.appInstanceIDEventToken = value; }

}

