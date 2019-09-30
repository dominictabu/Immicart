package com.andromeda.immicart.mpesa.network;

import com.andromeda.immicart.mpesa.okhttp.AccessTokenInterceptor;
import com.andromeda.immicart.mpesa.okhttp.AuthInterceptor;
import com.andromeda.immicart.mpesa.okhttp.UnsafeOkHttpClient;
import com.andromeda.immicart.mpesa.utils.Environment;
import com.andromeda.immicart.mpesa.utils.Settings;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.concurrent.TimeUnit;

public class ApiClient {
    private static LNMApi LNMAPI = null;
    private static AuthAPI authAPI = null;
    private static HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();

    static {
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
    }

    public static LNMApi getAPI(String BASE_URL, String authToken) {
        if (LNMAPI == null) {
            OkHttpClient client = getClientBuilder(BASE_URL)
                    .connectTimeout(Settings.CONNECT_TIMEOUT, TimeUnit.SECONDS)
                    .writeTimeout(Settings.WRITE_TIMEOUT, TimeUnit.SECONDS)
                    .readTimeout(Settings.READ_TIMEOUT, TimeUnit.SECONDS)
                    .addInterceptor(new AuthInterceptor(authToken))
                    .build();

            LNMAPI = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build()
                    .create(LNMApi.class);
        }
        return LNMAPI;
    }

    private static OkHttpClient.Builder getClientBuilder(String base_url) {
        OkHttpClient.Builder builder;
        if (base_url.equals(Environment.SANDBOX.toString())){
            builder = new UnsafeOkHttpClient()
                    .getUnsafeOkHttpClient()
                    .addInterceptor(httpLoggingInterceptor);
        }else{
            builder = new OkHttpClient.Builder();
        }

        return builder;

    }

    public static AuthAPI getAuthAPI(String CONSUMER_KEY, String CONSUMER_SECRET, String BASE_URL) {
        if (authAPI == null) {
            OkHttpClient client = getClientBuilder(BASE_URL)
                    .connectTimeout(Settings.CONNECT_TIMEOUT, TimeUnit.SECONDS)
                    .writeTimeout(Settings.WRITE_TIMEOUT, TimeUnit.SECONDS)
                    .readTimeout(Settings.READ_TIMEOUT, TimeUnit.SECONDS)
                    .addInterceptor(new AccessTokenInterceptor(CONSUMER_KEY, CONSUMER_SECRET))
                    .build();

            authAPI = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build()
                    .create(AuthAPI.class);
        }
        return authAPI;
    }
}
