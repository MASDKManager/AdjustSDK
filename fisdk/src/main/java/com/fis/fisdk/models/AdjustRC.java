package com.fis.fisdk.models;

public class AdjustRC {
    private Boolean enabled;
    private String appToken;
    private String appInstanceIDEventToken;

    public Boolean getEnabled() { return enabled; }
    public void setEnabled(Boolean value) { this.enabled = value; }

    public String getAppToken() { return appToken; }
    public void setAppToken(String value) { this.appToken = value; }

    public String getAppInstanceIDEventToken() { return appInstanceIDEventToken; }
    public void setAppInstanceIDEventToken(String value) { this.appInstanceIDEventToken = value; }
}

