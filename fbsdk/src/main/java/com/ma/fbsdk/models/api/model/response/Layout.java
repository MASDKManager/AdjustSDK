
package com.ma.fbsdk.models.api.model.response;

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

    public  SDKTexts getSDKTexts() {
        return SDKTexts;
    }

    public void setSDKTexts( SDKTexts SDKTexts) {
        this.SDKTexts = SDKTexts;
    }

    public  Disclaimers getDisclaimers() {
        return Disclaimers;
    }

    public void setDisclaimers( Disclaimers disclaimers) {
        Disclaimers = disclaimers;
    }

    public  Theme getTheme() {
        return Theme;
    }

    public void setTheme(Theme theme) {
        Theme = theme;
    }
}
