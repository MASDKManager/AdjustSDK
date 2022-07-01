
package com.ma.fbsdk.models.api.model.response;

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

    public TextBox getTextBox() {
        return TextBox;
    }

    public void setTextBox(TextBox textBox) {
        TextBox = textBox;
    }

    public Button getButton() {
        return Button;
    }

    public void setButton(Button button) {
        Button = button;
    }

    public TermsAndConditions getTermsAndConditions() {
        return TermsAndConditions;
    }

    public void setTermsAndConditions(TermsAndConditions termsAndConditions) {
        TermsAndConditions = termsAndConditions;
    }

    public PrivacyPolicies getPrivacyPolicies() {
        return PrivacyPolicies;
    }

    public void setPrivacyPolicies(PrivacyPolicies privacyPolicies) {
        PrivacyPolicies = privacyPolicies;
    }

    public CheckBox getCheckBox() {
        return CheckBox;
    }

    public void setCheckBox(CheckBox checkBox) {
        CheckBox = checkBox;
    }

    public BackGround getBackGround() {
        return BackGround;
    }

    public void setBackGround(BackGround backGround) {
        BackGround = backGround;
    }

    public OTPHeaderIcon getOTPHeaderIcon() {
        return OTPHeaderIcon;
    }

    public void setOTPHeaderIcon(OTPHeaderIcon OTPHeaderIcon) {
        this.OTPHeaderIcon = OTPHeaderIcon;
    }

    public WrongNumber getWrongNumber() {
        return WrongNumber;
    }

    public void setWrongNumber(WrongNumber wrongNumber) {
        WrongNumber = wrongNumber;
    }

    public AskCodeAgain getAskCodeAgain() {
        return AskCodeAgain;
    }

    public void setAskCodeAgain(AskCodeAgain askCodeAgain) {
        AskCodeAgain = askCodeAgain;
    }
}
