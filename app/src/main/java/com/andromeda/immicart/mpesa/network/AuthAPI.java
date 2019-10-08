package com.andromeda.immicart.mpesa.network;

import com.andromeda.immicart.mpesa.model.AccessToken;
import retrofit2.Call;
import retrofit2.http.GET;

public interface AuthAPI {

    @GET("oauth/v1/generate?grant_type=client_credentials")
    Call<AccessToken> getAccessToken();

}