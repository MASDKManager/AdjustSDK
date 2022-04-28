package com.sma.ssdkm.api.model.response;

import java.io.Serializable;

public class NextAction implements Serializable {
    private int Action;
    private String Name;
    private Layout Layout;
    private String URL;
    private String Schema;


    public int getAction() {
        return Action;
    }

    public void setAction(int action) {
        Action = action;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public com.sma.ssdkm.api.model.response.Layout getLayout() {
        return Layout;
    }

    public void setLayout(com.sma.ssdkm.api.model.response.Layout layout) {
        Layout = layout;
    }

    public String getURL() {
        return URL;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }

    public String getSchema() {
        return Schema;
    }

    public void setSchema(String schema) {
        Schema = schema;
    }
}
