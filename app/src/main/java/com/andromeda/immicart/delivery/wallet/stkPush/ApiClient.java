package com.andromeda.immicart.delivery.wallet.stkPush;


import java.util.concurrent.TimeUnit;

import com.andromeda.immicart.delivery.wallet.C2BService.services.SimulateService;
import com.andromeda.immicart.delivery.wallet.stkPush.interceptor.AccessTokenInterceptor;
import com.andromeda.immicart.delivery.wallet.stkPush.interceptor.AuthInterceptor;
import com.andromeda.immicart.delivery.wallet.stkPush.services.STKPushService;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.andromeda.immicart.delivery.wallet.stkPush.util.AppConstants.*;

public class ApiClient {

    private Retrofit retrofit;
    private boolean isDebug;
    private boolean isGetAccessToken;
    private String mAuthToken;
    private HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();

    /**
     * Set the {@link Retrofit} log level. This allows one to view network traffic.
     *
     * @param isDebug If true, the log level is set to
     *                {@link HttpLoggingInterceptor.Level#BODY}. Otherwise
     *                {@link HttpLoggingInterceptor.Level#NONE}.
     */
    public ApiClient setIsDebug(boolean isDebug) {
        this.isDebug = isDebug;
        return this;
    }

    /**
     * Helper method used to set the authenication Token
     *
     * @param authToken token from api
     */
    public ApiClient setAuthToken(String authToken) {
        mAuthToken = authToken;
        return this;
    }

    /**
     * Helper method used to determine if get token enpoint has been invoked. This should be called
     * only when requesting of an accessToken
     *
     * @param getAccessToken {@link Boolean}
     */
    public ApiClient setGetAccessToken(boolean getAccessToken) {
        isGetAccessToken = getAccessToken;
        return this;
    }

    /**
     * Configure OkHttpClient
     *
     * @return OkHttpClient
     */
    private OkHttpClient.Builder okHttpClient() {
        OkHttpClient.Builder okHttpClient = new OkHttpClient.Builder();
        okHttpClient
                .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
                .addInterceptor(httpLoggingInterceptor);

        return okHttpClient;
    }

    /**
     * Return the current {@link Retrofit} instance. If none exists (first call, API key changed),
     * builds a new one.
     * <p/>
     * When building, sets the endpoint and a {@link HttpLoggingInterceptor} which adds the API key as query param.
     */
    private Retrofit getRestAdapter() {

        Retrofit.Builder builder = new Retrofit.Builder();
        builder.baseUrl(BASE_URL);
        builder.addConverterFactory(GsonConverterFactory.create());

        if (isDebug) {
            httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        }

        OkHttpClient.Builder okhttpBuilder = okHttpClient();

        if (isGetAccessToken) {
            okhttpBuilder.addInterceptor(new AccessTokenInterceptor());
        }

        if (mAuthToken != null && !mAuthToken.isEmpty()) {
            okhttpBuilder.addInterceptor(new AuthInterceptor(mAuthToken));
        }

        builder.client(okhttpBuilder.build());

        retrofit = builder.build();

        return retrofit;
    }

    /**
     * Create service instance.
     *
     * @return STKPushService Service.
     */
    public STKPushService mpesaService() {
        return getRestAdapter().create(STKPushService.class);
    }

    public SimulateService mpesaSimulateService() {
        return getRestAdapter().create(SimulateService.class);
    }


}
