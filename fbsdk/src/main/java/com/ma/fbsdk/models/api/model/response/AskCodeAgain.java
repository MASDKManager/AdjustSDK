package com.ma.fbsdk.models.api.model.response;

import java.io.Serializable;

public class AskCodeAgain implements Serializable {
    private String Color;
    private int CountDown;
    private String Text;

    public String getColor() {
        return Color;
    }

    public void setColor(String color) {
        Color = color;
    }

    public int getCountDown() {
        return CountDown;
    }

    public void setCountDown(int countDown) {
        CountDown = countDown;
    }

    public String getText() {
        return Text;
    }

    public void setText(String text) {
        Text = text;
    }
}
