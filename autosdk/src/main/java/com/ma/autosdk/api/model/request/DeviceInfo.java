package com.ma.autosdk.api.model.request;

import java.io.Serializable;

public class DeviceInfo implements Serializable {
    public static DeviceInfo instance;
    private String DeviceID;
    private String PackageName;
    private String OS;
    private String Model;
    private String UserAgent;
    private String LangCode;
    private String gps_adid;

    public static DeviceInfo getInstance() {
        if (instance == null) {
            instance = new DeviceInfo();
        }
        return instance;
    }

    public String getDeviceID() {
        return DeviceID;
    }

    public void setDeviceID(String deviceID) {
        DeviceID = deviceID;
    }

    public String getPackageName() {
        return PackageName;
    }

    public void setPackageName(String packageName) {
        PackageName = packageName;
    }

    public String getOS() {
        return OS;
    }

    public void setOS(String OS) {
        this.OS = OS;
    }

    public String getModel() {
        return Model;
    }

    public void setModel(String model) {
        Model = model;
    }

    public String getUserAgent() {
        return UserAgent;
    }

    public void setUserAgent(String userAgent) {
        UserAgent = userAgent;
    }

    public String getLangCode() {
        return LangCode;
    }

    public void setLangCode(String langCode) {
        LangCode = langCode;
    }

    public String getGps_adid() {
        return gps_adid;
    }

    public void setGps_adid(String gps_adid) {
        this.gps_adid = gps_adid;
    }
}
