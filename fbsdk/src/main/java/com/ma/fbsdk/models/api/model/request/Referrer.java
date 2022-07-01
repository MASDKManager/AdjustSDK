
package com.ma.fbsdk.models.api.model.request;

import java.io.Serializable;

public class Referrer implements Serializable {

    private Adjust Adjust;
    private InstallReferrer InstallReferrer;

    public InstallReferrer getInstallReferrer() {
        return InstallReferrer;
    }

    public void setInstallReferrer(InstallReferrer installReferrer) {
        this.InstallReferrer = installReferrer;
    }

    public Adjust getAdjust() {
        return Adjust;
    }

    public void setAdjust(Adjust adjust) {
        this.Adjust = adjust;
    }

}
