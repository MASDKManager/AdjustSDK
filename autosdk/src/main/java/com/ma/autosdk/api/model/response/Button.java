package com.ma.autosdk.api.model.response;


import java.io.Serializable;

public class Button  implements Serializable {
    private String Type;
    private String Background;
    private String Color;
    private String Text;

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public String getBackground() {
        return Background;
    }

    public void setBackground(String background) {
        Background = background;
    }

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
