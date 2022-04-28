
package com.ma.autosdk.api.model.response;

import java.io.Serializable;

public class TermsAndConditions implements Serializable {

    private String Color;
    private boolean Show;
    private String URL;
    private String Text;
    private String Body;

    public String getColor() {
        return Color;
    }

    public void setColor(String color) {
        Color = color;
    }

    public boolean isShow() {
        return Show;
    }

    public void setShow(boolean show) {
        Show = show;
    }

    public String getURL() {
        return URL;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }

    public String getText() {
        return Text;
    }

    public void setText(String text) {
        Text = text;
    }

    public String getBody() {
        return Body;
    }

    public void setBody(String body) {
        Body = body;
    }
}
