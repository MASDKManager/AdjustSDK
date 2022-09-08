package com.fir.sdk.models;

public class AdjustRC {
    private String enabled;
    private String appToken;
    private String appInstanceIDEventToken;

    public String getAttrLogEventToken() {
        return attrLogEventToken;
    }

    public void setAttrLogEventToken(String attrLogEventToken) {
        this.attrLogEventToken = attrLogEventToken;
    }

    private String attrLogEventToken;
    private long delay;
    private long callbackDelay;

    public long getCallbackDelay() {
        return callbackDelay;
    }

    public void setCallbackDelay(long callbackDelay) {
        this.callbackDelay = callbackDelay;
    }

    public String getEnabled() { return enabled; }
    public void setEnabled(String value) { this.enabled = value; }

    public String getAppToken() { return appToken; }
    public void setAppToken(String value) { this.appToken = value; }

    public String getAppInstanceIDEventToken() { return appInstanceIDEventToken; }
    public void setAppInstanceIDEventToken(String value) { this.appInstanceIDEventToken = value; }

    public long getDelay() { return delay; }
    public void setDelay(long value) { this.delay = value; }
}

