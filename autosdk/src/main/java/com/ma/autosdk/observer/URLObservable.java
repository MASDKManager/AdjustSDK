package com.ma.autosdk.observer;


import org.greenrobot.eventbus.EventBus;

public class URLObservable
{
    private int api_should_start = 0;

    public URLObservable(int api_should_start)
    {
        this.api_should_start = api_should_start;
    }

    public void api_should_start()
    {
        this.api_should_start = api_should_start - 1;
        if(this.api_should_start == 0){
            EventBus.getDefault().post(new DynURL());
        }
    }

    public void setApiShouldStartValue(int api_should_start)
    {
        this.api_should_start = api_should_start;
        if(this.api_should_start == 0){
            EventBus.getDefault().post(new DynURL());
        }
    }
}