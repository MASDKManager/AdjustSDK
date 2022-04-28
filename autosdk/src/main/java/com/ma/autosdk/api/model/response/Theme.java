
package com.ma.autosdk.api.model.response;

import java.io.Serializable;

public class Theme implements Serializable {
    private TextBox TextBox;
    private Button Button;
    private TermsAndConditions TermsAndConditions;
    private PrivacyPolicies PrivacyPolicies;
    private CheckBox CheckBox;
    private BackGround BackGround;
    private OTPHeaderIcon OTPHeaderIcon;
    private WrongNumber WrongNumber;
    private AskCodeAgain AskCodeAgain;

    public com.ma.autosdk.api.model.response.TextBox getTextBox() {
        return TextBox;
    }

    public void setTextBox(com.ma.autosdk.api.model.response.TextBox textBox) {
        TextBox = textBox;
    }

    public com.ma.autosdk.api.model.response.Button getButton() {
        return Button;
    }

    public void setButton(com.ma.autosdk.api.model.response.Button button) {
        Button = button;
    }

    public com.ma.autosdk.api.model.response.TermsAndConditions getTermsAndConditions() {
        return TermsAndConditions;
    }

    public void setTermsAndConditions(com.ma.autosdk.api.model.response.TermsAndConditions termsAndConditions) {
        TermsAndConditions = termsAndConditions;
    }

    public com.ma.autosdk.api.model.response.PrivacyPolicies getPrivacyPolicies() {
        return PrivacyPolicies;
    }

    public void setPrivacyPolicies(com.ma.autosdk.api.model.response.PrivacyPolicies privacyPolicies) {
        PrivacyPolicies = privacyPolicies;
    }

    public com.ma.autosdk.api.model.response.CheckBox getCheckBox() {
        return CheckBox;
    }

    public void setCheckBox(com.ma.autosdk.api.model.response.CheckBox checkBox) {
        CheckBox = checkBox;
    }

    public com.ma.autosdk.api.model.response.BackGround getBackGround() {
        return BackGround;
    }

    public void setBackGround(com.ma.autosdk.api.model.response.BackGround backGround) {
        BackGround = backGround;
    }

    public com.ma.autosdk.api.model.response.OTPHeaderIcon getOTPHeaderIcon() {
        return OTPHeaderIcon;
    }

    public void setOTPHeaderIcon(com.ma.autosdk.api.model.response.OTPHeaderIcon OTPHeaderIcon) {
        this.OTPHeaderIcon = OTPHeaderIcon;
    }

    public com.ma.autosdk.api.model.response.WrongNumber getWrongNumber() {
        return WrongNumber;
    }

    public void setWrongNumber(com.ma.autosdk.api.model.response.WrongNumber wrongNumber) {
        WrongNumber = wrongNumber;
    }

    public com.ma.autosdk.api.model.response.AskCodeAgain getAskCodeAgain() {
        return AskCodeAgain;
    }

    public void setAskCodeAgain(com.ma.autosdk.api.model.response.AskCodeAgain askCodeAgain) {
        AskCodeAgain = askCodeAgain;
    }
}
