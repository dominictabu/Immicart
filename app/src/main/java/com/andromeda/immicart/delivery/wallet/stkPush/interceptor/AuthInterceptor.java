package com.andromeda.immicart.delivery.wallet.stkPush.interceptor;

import androidx.annotation.NonNull;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

/**
 * This class add information an authorization key to {@link okhttp3.OkHttpClient} which is passed in
 * {ApiClient#getRestAdapter} which is required when making a request.
 *
 * @author Thomas Kioko
 */
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