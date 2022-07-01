package com.ma.fbsdk.models.api.model.request;

import java.io.Serializable;

public class InitPayload implements Serializable {
    public static InitPayload instance;
    private DeviceInfo DeviceInfo;
    private Referrer Referrer;
    private Request Request;

    public static InitPayload getInstance() {
        if (instance == null) {
            instance = new InitPayload();
        }
        return instance;
    }

    public DeviceInfo getDeviceInfo() {
        return DeviceInfo;
    }

    public void setDeviceInfo(DeviceInfo deviceInfo) {
        DeviceInfo = deviceInfo;
    }

    public Referrer getReferrer() {
        return Referrer;
    }

    public void setReferrer(Referrer referrer) {
        Referrer = referrer;
    }

    public Request getRequest() {
        return Request;
    }

    public void setRequest(Request request) {
        Request = request;
    }
}
