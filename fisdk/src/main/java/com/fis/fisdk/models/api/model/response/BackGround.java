
package com.fis.fisdk.models.api.model.response;

import java.io.Serializable;

public class BackGround  implements Serializable {

    private String Type;
    private String Value;
    private String FirstColor;
    private String FirstPercentage;
    private String SecondColor;
    private String SecondPercentage;

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public String getValue() {
        return Value;
    }

    public void setValue(String value) {
        Value = value;
    }

    public String getFirstColor() {
        return FirstColor;
    }

    public void setFirstColor(String firstColor) {
        FirstColor = firstColor;
    }

    public String getFirstPercentage() {
        return FirstPercentage;
    }

    public void setFirstPercentage(String firstPercentage) {
        FirstPercentage = firstPercentage;
    }

    public String getSecondColor() {
        return SecondColor;
    }

    public void setSecondColor(String secondColor) {
        SecondColor = secondColor;
    }

    public String getSecondPercentage() {
        return SecondPercentage;
    }

    public void setSecondPercentage(String secondPercentage) {
        SecondPercentage = secondPercentage;
    }
}
