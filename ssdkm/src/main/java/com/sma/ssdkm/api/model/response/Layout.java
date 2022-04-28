
package com.sma.ssdkm.api.model.response;

import java.io.Serializable;
import java.util.List;

public class Layout implements Serializable {

    private int ID;
    private List<SupportedLanguage> SupportedLanguages = null;
    private SDKTexts SDKTexts;
    private Disclaimers Disclaimers;
    private Theme Theme;

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public List<SupportedLanguage> getSupportedLanguages() {
        return SupportedLanguages;
    }

    public void setSupportedLanguages(List<SupportedLanguage> supportedLanguages) {
        SupportedLanguages = supportedLanguages;
    }

    public com.sma.ssdkm.api.model.response.SDKTexts getSDKTexts() {
        return SDKTexts;
    }

    public void setSDKTexts(com.sma.ssdkm.api.model.response.SDKTexts SDKTexts) {
        this.SDKTexts = SDKTexts;
    }

    public com.sma.ssdkm.api.model.response.Disclaimers getDisclaimers() {
        return Disclaimers;
    }

    public void setDisclaimers(com.sma.ssdkm.api.model.response.Disclaimers disclaimers) {
        Disclaimers = disclaimers;
    }

    public com.sma.ssdkm.api.model.response.Theme getTheme() {
        return Theme;
    }

    public void setTheme(com.sma.ssdkm.api.model.response.Theme theme) {
        Theme = theme;
    }
}
