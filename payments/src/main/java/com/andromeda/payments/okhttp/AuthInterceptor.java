package com.andromeda.payments.okhttp;

import androidx.annotation.NonNull;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

public class AuthInterceptor implements Interceptor {

    private final String authToken;

    public AuthInterceptor(String authToken) {
        this.authToken = authToken;
    }

    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request request = chain.request().newBuilder()
                .addHeader("Authorization", "Bearer " + authToken)
                .build();
        return chain.proceed(request);
    }
}
