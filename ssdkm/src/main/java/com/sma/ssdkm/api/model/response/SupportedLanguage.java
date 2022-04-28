package com.sma.ssdkm.api.model.response;

import java.io.Serializable;

public class SupportedLanguage implements Serializable {
    private String Name;
    private String Code;

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getCode() {
        return Code;
    }

    public void setCode(String code) {
        Code = code;
    }
}
