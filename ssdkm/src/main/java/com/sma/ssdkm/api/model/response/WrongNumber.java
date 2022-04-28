package com.sma.ssdkm.api.model.response;

import java.io.Serializable;

public class WrongNumber implements Serializable {
    private String Color;
    private String Text;

    public String getColor() {
        return Color;
    }

    public void setColor(String color) {
        Color = color;
    }

    public String getText() {
        return Text;
    }

    public void setText(String text) {
        Text = text;
    }
}
