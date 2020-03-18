package com.andromeda.immicart.delivery.wallet.C2BService.services;

import com.andromeda.immicart.delivery.wallet.C2BService.model.C2B;
import com.andromeda.immicart.delivery.wallet.C2BService.model.RegisterURL;
import com.andromeda.immicart.delivery.wallet.stkPush.model.AccessToken;
import com.andromeda.immicart.delivery.wallet.stkPush.model.STKPush;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * Created  on 5/28/2017.
 */

public interface SimulateService {
    @POST("mpesa/c2b/v1/simulate")
    Call<C2B> simulate(@Body C2B c2B);

    @POST("mpesa/c2b/v1/registerurl")
    Call<RegisterURL> registerURL(@Body RegisterURL registerURL);

//    @GET("jobs/pending")
//    Call<STKPush> getTasks();

//    @GET("oauth/v1/generate?grant_type=client_credentials")
//    Call<AccessToken> getAccessToken();
}