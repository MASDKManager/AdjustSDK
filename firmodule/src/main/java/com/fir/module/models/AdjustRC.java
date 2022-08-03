package com.fir.module.models;

public class AdjustRC {
    private String enabled;
    private String appToken;
    private String appInstanceIDEventToken;
    private long delay;

    public String getEnabled() { return enabled; }
    public void setEnabled(String value) { this.enabled = value; }

    public String getAppToken() { return appToken; }
    public void setAppToken(String value) { this.appToken = value; }

    public String getAppInstanceIDEventToken() { return appInstanceIDEventToken; }
    public void setAppInstanceIDEventToken(String value) { this.appInstanceIDEventToken = value; }

    public long getDelay() { return delay; }
    public void setDelay(long value) { this.delay = value; }
}

