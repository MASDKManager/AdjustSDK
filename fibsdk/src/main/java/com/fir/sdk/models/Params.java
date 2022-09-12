package com.fir.sdk.models;

import java.io.Serializable;

public class Params  implements Serializable {
    public String getGps_adid() {
        return gps_adid;
    }

    public void setGps_adid(String gps_adid) {
        this.gps_adid = gps_adid;
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


    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }



    public String getNaming() {
        return naming;
    }

    public void setNaming(String naming) {
        this.naming = naming;
    }

    public String getAdjust_id() {
        return adjust_id;
    }

    public void setAdjust_id(String adjust_id) {
        this.adjust_id = adjust_id;
    }


    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    private String uuid = "";
    private String gps_adid = "";
    private String deeplink = "";
    private String googleAttribution = "";
    private String adjustAttribution = "";
    private String firebaseInstanceId = "";
    private String naming = "";
    private String adjust_id = "";
    private String phoneNumber = "";

}
