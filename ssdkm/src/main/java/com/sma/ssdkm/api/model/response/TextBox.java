package com.sma.ssdkm.api.model.response;

import java.io.Serializable;

public class TextBox implements Serializable {
    private String Color;
    private String PlaceHolderColor;
    private String Text;
    private String ErrorMSG;
    private int Length;
    private int Prefix;
    private int isNumeric;
    private int MaxLength;
    private int MinLength;

    public String getColor() {
        return Color;
    }

    public void setColor(String color) {
        Color = color;
    }

    public String getPlaceHolderColor() {
        return PlaceHolderColor;
    }

    public void setPlaceHolderColor(String placeHolderColor) {
        PlaceHolderColor = placeHolderColor;
    }

    public String getErrorMSG() {
        return ErrorMSG;
    }

    public void setErrorMSG(String errorMSG) {
        ErrorMSG = errorMSG;
    }

    public String getText() {
        return Text;
    }

    public void setText(String text) {
        Text = text;
    }

    public int getLength() {
        return Length;
    }

    public void setLength(int length) {
        Length = length;
    }

    public int getIsNumeric() {
        return isNumeric;
    }

    public void setIsNumeric(int isNumeric) {
        this.isNumeric = isNumeric;
    }

    public int getMaxLength() {
        return MaxLength;
    }

    public void setMaxLength(int maxLength) {
        MaxLength = maxLength;
    }

    public int getMinLength() {
        return MinLength;
    }

    public void setMinLength(int minLength) {
        MinLength = minLength;
    }

    public int getPrefix() {
        return Prefix;
    }

    public void setPrefix(int prefix) {
        Prefix = prefix;
    }
}
