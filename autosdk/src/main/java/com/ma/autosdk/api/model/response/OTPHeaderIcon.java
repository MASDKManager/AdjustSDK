package com.ma.autosdk.api.model.response;

import java.io.Serializable;

public class OTPHeaderIcon implements Serializable {

    private String URL;
    private String Width;
    public String Class;

    public String getURL() {
        return URL;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }

    public String getWidth() {
        return Width;
    }

    public void setWidth(String width) {
        Width = width;
    }

    public void setClass(String aClass) {
        Class = aClass;
    }
}
