package com.andromeda.immicart.networking;


import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.concurrent.TimeUnit;

public class RetrofitClientInstance {

    static HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY);
    static OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).connectTimeout(30, TimeUnit.SECONDS).readTimeout(30, TimeUnit.SECONDS).build();

//    client.setConnectTimeout(30, TimeUnit.SECONDS); // connect timeout
//    client.setReadTimeout(30, TimeUnit.SECONDS);    // socket timeout
    private static Retrofit retrofit;
    private static final String BASE_URL = "http://157.245.68.46/api/v1/";
    private static final String Url = "https://us-central1-immicart-2ca69.cloudfunctions.net/";

    public static Retrofit getRetrofitInstance(String url) {
        if (retrofit == null) {
            retrofit = new retrofit2.Retrofit.Builder()
                    .baseUrl(url)
                    .client(client)

                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}