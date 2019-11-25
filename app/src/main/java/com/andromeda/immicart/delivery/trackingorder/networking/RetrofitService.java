package com.andromeda.immicart.delivery.trackingorder.networking;

import com.andromeda.immicart.networking.ImmicartAPIService;
import com.andromeda.immicart.networking.RetrofitClientInstance;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitService {
    static HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY);
    static OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();
    private static Retrofit retrofit;

    public static Retrofit getRetrofitInstance(String BASE_URL) {
        if (retrofit == null) {
            retrofit = new retrofit2.Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)

                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static IGoogleApi create() {
        String baseUrl = "https://googleapis.com";
        IGoogleApi service = RetrofitService.getRetrofitInstance(baseUrl).create(IGoogleApi.class);

        return service;
    }

}
