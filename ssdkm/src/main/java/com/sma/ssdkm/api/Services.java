package com.sma.ssdkm.api;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Url;

public interface Services {
    @POST()
    Call<String> initiate(@Url String url, @Header("AccessToken") String header, @Body String post);
}
