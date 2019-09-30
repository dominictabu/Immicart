package com.andromeda.immicart.mpesa.network;

import com.andromeda.immicart.mpesa.model.LNMExpress;
import com.andromeda.immicart.mpesa.model.PaymentResult;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public class LNMApi {
    @POST("mpesa/stkpush/v1/processrequest")
    public Call<PaymentResult> getLNMPesa(@Body LNMExpress lnmExpress) {
        return null;
    }
}