package com.ma.fbsdk.models.api.model.response;

import java.io.Serializable;

public class CheckBox  implements Serializable {
    private boolean Show;
    private boolean Checked;
    private String RelatedURL;
    private String Color;
    private String Text;

    public boolean isShow() {
        return Show;
    }

    public void setShow(boolean show) {
        Show = show;
    }

    public boolean isChecked() {
        return Checked;
    }

    public void setChecked(boolean checked) {
        Checked = checked;
    }

    public String getRelatedURL() {
        return RelatedURL;
    }

    public void setRelatedURL(String relatedURL) {
        RelatedURL = relatedURL;
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
