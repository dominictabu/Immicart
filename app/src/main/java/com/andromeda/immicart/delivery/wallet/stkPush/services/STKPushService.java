package com.andromeda.immicart.delivery.wallet.stkPush.services;

import com.andromeda.immicart.delivery.wallet.stkPush.model.AccessToken;
import com.andromeda.immicart.delivery.wallet.stkPush.model.STKPush;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * Created  on 5/28/2017.
 */

public interface STKPushService {
    @POST("mpesa/stkpush/v1/processrequest")
    Call<STKPush> sendPush(@Body STKPush stkPush);

    @GET("jobs/pending")
    Call<STKPush> getTasks();

    @GET("oauth/v1/generate?grant_type=client_credentials")
    Call<AccessToken> getAccessToken();
}
