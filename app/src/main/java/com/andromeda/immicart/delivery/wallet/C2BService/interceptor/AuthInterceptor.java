package com.andromeda.immicart.delivery.wallet.C2BService.interceptor;

import androidx.annotation.NonNull;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;


public class AuthInterceptor implements Interceptor {

    private String mAuthToken;

    public AuthInterceptor(String authToken) {
        mAuthToken = authToken;
    }

    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request request  = chain.request().newBuilder()
                .addHeader("Authorization", "Bearer " + mAuthToken)
                .build();
        return chain.proceed(request);
    }
}