package com.fis.fisdk.models.checkout;

public class CheckoutLoad {
    private String token;
    private String amount;
    private String currency;
    private String reference;

    public String getToken() { return token; }
    public void setToken(String value) { this.token = value; }

    public String getAmount() { return amount; }
    public void setAmount(String value) { this.amount = value; }

    public String getCurrency() { return currency; }
    public void setCurrency(String value) { this.currency = value; }

    public String getReference() { return reference; }
    public void setReference(String value) { this.reference = value; }
}
