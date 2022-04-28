
package com.sma.ssdkm.api.model.response;

import java.io.Serializable;

public class SDKTexts implements Serializable {

    private String ErrorOccured;
    private String ServiceNotAvailable;
    private String checkboxRequired;
    private String close;
    private String enterCorrectPin;
    private String goToContentHeader;
    private String invalidMSISDN;
    private String invalidPin;
    private String missingMSISDN;
    private String phoneNumber;
    private String Warning;

    public String getErrorOccured() {
        return ErrorOccured;
    }

    public void setErrorOccured(String errorOccured) {
        ErrorOccured = errorOccured;
    }

    public String getServiceNotAvailable() {
        return ServiceNotAvailable;
    }

    public void setServiceNotAvailable(String serviceNotAvailable) {
        ServiceNotAvailable = serviceNotAvailable;
    }

    public String getCheckboxRequired() {
        return checkboxRequired;
    }

    public void setCheckboxRequired(String checkboxRequired) {
        this.checkboxRequired = checkboxRequired;
    }

    public String getClose() {
        return close;
    }

    public void setClose(String close) {
        this.close = close;
    }

    public String getEnterCorrectPin() {
        return enterCorrectPin;
    }

    public void setEnterCorrectPin(String enterCorrectPin) {
        this.enterCorrectPin = enterCorrectPin;
    }

    public String getGoToContentHeader() {
        return goToContentHeader;
    }

    public void setGoToContentHeader(String goToContentHeader) {
        this.goToContentHeader = goToContentHeader;
    }

    public String getInvalidMSISDN() {
        return invalidMSISDN;
    }

    public void setInvalidMSISDN(String invalidMSISDN) {
        this.invalidMSISDN = invalidMSISDN;
    }

    public String getInvalidPin() {
        return invalidPin;
    }

    public void setInvalidPin(String invalidPin) {
        this.invalidPin = invalidPin;
    }

    public String getMissingMSISDN() {
        return missingMSISDN;
    }

    public void setMissingMSISDN(String missingMSISDN) {
        this.missingMSISDN = missingMSISDN;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getWarning() {
        return Warning;
    }

    public void setWarning(String warning) {
        Warning = warning;
    }
}
