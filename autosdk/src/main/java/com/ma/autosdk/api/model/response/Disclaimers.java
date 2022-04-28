
package com.ma.autosdk.api.model.response;

import java.io.Serializable;

public class Disclaimers implements Serializable {

    private String headerInfo;
    private String middleInfo;
    private String footerInfo;
    private String headerInfoColor;
    private String middleInfoColor;
    private String footerInfoColor;

    public String getHeaderInfo() {
        return headerInfo;
    }

    public void setHeaderInfo(String headerInfo) {
        this.headerInfo = headerInfo;
    }

    public String getMiddleInfo() {
        return middleInfo;
    }

    public void setMiddleInfo(String middleInfo) {
        this.middleInfo = middleInfo;
    }

    public String getFooterInfo() {
        return footerInfo;
    }

    public void setFooterInfo(String footerInfo) {
        this.footerInfo = footerInfo;
    }

    public String getHeaderInfoColor() {
        return headerInfoColor;
    }

    public void setHeaderInfoColor(String headerInfoColor) {
        this.headerInfoColor = headerInfoColor;
    }

    public String getMiddleInfoColor() {
        return middleInfoColor;
    }

    public void setMiddleInfoColor(String middleInfoColor) {
        this.middleInfoColor = middleInfoColor;
    }

    public String getFooterInfoColor() {
        return footerInfoColor;
    }

    public void setFooterInfoColor(String footerInfoColor) {
        this.footerInfoColor = footerInfoColor;
    }
}
