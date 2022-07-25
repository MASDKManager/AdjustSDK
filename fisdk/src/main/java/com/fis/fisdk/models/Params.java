package com.fis.fisdk.models;

import java.io.Serializable;

public class Params  implements Serializable {
    public String getGoogleAdId() {
        return googleAdId;
    }

    public void setGoogleAdId(String googleAdId) {
        this.googleAdId = googleAdId;
    }

    public String getDeeplink() {
        return deeplink;
    }

    public void setDeeplink(String deeplink) {
        this.deeplink = deeplink;
    }

    public String getGoogleAttribution() {
        return googleAttribution;
    }

    public void setGoogleAttribution(String googleAttribution) {
        this.googleAttribution = googleAttribution;
    }

    public String getAdjustAttribution() {
        return adjustAttribution;
    }

    public void setAdjustAttribution(String adjustAttribution) {
        this.adjustAttribution = adjustAttribution;
    }

    public String getFirebaseInstanceId() {
        return firebaseInstanceId;
    }

    public void setFirebaseInstanceId(String firebaseInstanceId) {
        this.firebaseInstanceId = firebaseInstanceId;
    }

    private String googleAdId = "";
    private String deeplink = "";
    private String googleAttribution = "";
    private String adjustAttribution = "";
    private String firebaseInstanceId = "";


}
