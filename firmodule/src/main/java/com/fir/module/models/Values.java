package com.fir.module.models;

public class Values {

    public String getClick_id() {
        return click_id;
    }

    public void setClick_id(String click_id) {
        this.click_id = click_id;
    }

    public String getPackage_id() {
        return package_id;
    }

    public void setPackage_id(String package_id) {
        this.package_id = package_id;
    }

    public String getFirebase_instance_id() {
        return firebase_instance_id;
    }

    public void setFirebase_instance_id(String firebase_instance_id) {
        this.firebase_instance_id = firebase_instance_id;
    }

    public String getAdjust_attribution() {
        return adjust_attribution;
    }

    public void setAdjust_attribution(String adjust_attribution) {
        this.adjust_attribution = adjust_attribution;
    }

    public String getGps_adid() {
        return gps_adid;
    }

    public void setGps_adid(String gps_adid) {
        this.gps_adid = gps_adid;
    }

    public String getGoogle_attribution() {
        return google_attribution;
    }

    public void setGoogle_attribution(String google_attribution) {
        this.google_attribution = google_attribution;
    }

    String click_id = "";
    String package_id = "";
    String firebase_instance_id = "";
    String adjust_attribution = "";
    String gps_adid = "";
    String google_attribution = "";

}
