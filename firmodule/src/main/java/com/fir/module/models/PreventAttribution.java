package com.fir.module.models;

public class PreventAttribution {
    private boolean useAdjustAttribution;
    private boolean useGoogleAttribution;
    private String[] attBlackList;

    public boolean getUseAdjustAttribution() { return useAdjustAttribution; }
    public void setUseAdjustAttribution(boolean value) { this.useAdjustAttribution = value; }

    public boolean getUseGoogleAttribution() { return useGoogleAttribution; }
    public void setUseGoogleAttribution(boolean value) { this.useGoogleAttribution = value; }

    public String[] getAttBlackList() { return attBlackList; }
    public void setAttBlackList(String[] value) { this.attBlackList = value; }
}
