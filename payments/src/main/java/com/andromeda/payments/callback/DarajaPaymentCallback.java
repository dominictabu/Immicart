package com.andromeda.payments.callback;

import androidx.annotation.NonNull;
import com.andromeda.payments.model.ErrorResponse;
import com.andromeda.payments.model.PaymentResult;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.io.IOException;

public class DarajaPaymentCallback implements Callback<PaymentResult> {

    final DarajaPaymentListener listener;

    public DarajaPaymentCallback(final DarajaPaymentListener listener) {
        this.listener = listener;
    }

    @Override
    public void onResponse(@NonNull Call<PaymentResult> call, @NonNull Response<PaymentResult> response) {
        if (response.isSuccessful()) {
            PaymentResult result = response.body();
            if (result != null) {
                if (result.ResponseCode.equals("0")){
                    listener.onPaymentRequestComplete(result);
                }else{
                    String error = result.ResponseCode + " : " + result.ResponseDescription;
                    listener.onPaymentFailure(new DarajaException(error));
                }

                return;
            }
        }else {
            try {
                Gson gson = new GsonBuilder().create();
                ErrorResponse error = gson.fromJson(response.errorBody().string(), ErrorResponse.class);
                listener.onPaymentFailure(new DarajaException(error));
            } catch (IOException e) {
                e.printStackTrace();
                listener.onPaymentFailure(new DarajaException(response.code() + ""));
            }
        }
    }

    @Override
    public void onFailure(@NonNull Call<PaymentResult> call, @NonNull Throwable t) {
        listener.onNetworkFailure(new DarajaException(t.getLocalizedMessage()));
    }
}
