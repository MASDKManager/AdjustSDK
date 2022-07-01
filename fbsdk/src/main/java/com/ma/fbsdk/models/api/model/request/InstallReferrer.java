
package com.ma.fbsdk.models.api.model.request;

import java.io.Serializable;

public class InstallReferrer implements Serializable {
    private String Deeplink;
    private String RefStr;

    public String getDeeplink() {
        return Deeplink;
    }

    public void setDeeplink(String deeplink) {
        Deeplink = deeplink;
    }

    public String getRefStr() {
        return RefStr;
    }

    public void setRefStr(String refStr) {
        RefStr = refStr;
    }
}
