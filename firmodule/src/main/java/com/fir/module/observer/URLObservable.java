package com.fir.module.observer;

import org.greenrobot.eventbus.EventBus;

public class URLObservable {
    private boolean f_i_i_d = false;
    private boolean f_r_c = false;
    private boolean g_r = false;
    private boolean a_r = false;

    public void ads_start(Events events) {
        if (events.equals(Events.F_R_C)) {
            f_r_c = true;
        }

        if (events.equals(Events.G_R)) {
            g_r = true;
        }

        if (events.equals(Events.A_R)) {
            a_r = true;
        }

        if (events.equals(Events.F_I_ID)) {
            f_i_i_d = true;
        }

        if (readyToRun()){
            EventBus.getDefault().post(new DynU());
        }
    }

    public boolean readyToRun() {
        return f_r_c && g_r && a_r ;
    }
}