package com.fis.fisdk.models.api.model.request;

import java.io.Serializable;

public class Request implements Serializable {

    private int Action;
    private String TransactionID;
    private String SessionID;
    private String MSISDN;
    private String PinCode;

    public int getAction() {
        return Action;
    }

    public void setAction(int action) {
        Action = action;
    }

    public String getTransactionID() {
        return TransactionID;
    }

    public void setTransactionID(String transactionID) {
        TransactionID = transactionID;
    }

    public String getSessionID() {
        return SessionID;
    }

    public void setSessionID(String sessionID) {
        SessionID = sessionID;
    }

    public String getMSISDN() {
        return MSISDN;
    }

    public void setMSISDN(String MSISDN) {
        this.MSISDN = MSISDN;
    }

    public String getPinCode() {
        return PinCode;
    }

    public void setPinCode(String pinCode) {
        PinCode = pinCode;
    }
}
