package com.ssb.sdk.models.checkout;

public class CheckoutResponse {
    private String responseCode;
    private String status;
    private String responseSummary;
    private String approved;

    public String getResponseCode() { return responseCode; }
    public void setResponseCode(String value) { this.responseCode = value; }

    public String getStatus() { return status; }
    public void setStatus(String value) { this.status = value; }

    public String getResponseSummary() { return responseSummary; }
    public void setResponseSummary(String value) { this.responseSummary = value; }

    public String getApproved() { return approved; }
    public void setApproved(String value) { this.approved = value; }
}
