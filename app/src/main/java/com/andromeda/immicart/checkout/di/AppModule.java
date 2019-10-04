package com.andromeda.immicart.checkout.di;

import com.andromeda.immicart.checkout.Config;
import com.andromeda.immicart.checkout.MpesaExpressApp;
import com.andromeda.immicart.checkout.util.AppUtils;
import com.andromeda.immicart.mpesa.Daraja;
import com.andromeda.immicart.mpesa.utils.Environment;
import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;

@Module
public class AppModule {

    MpesaExpressApp app;

    void AppModule(MpesaExpressApp application) {
        app = application;
    }

    @Provides
    @Singleton
    MpesaExpressApp providesApplication() {
        return app;
    }

    @Provides
    @Singleton
    Daraja providesDaraja() {
        return Daraja.Builder(Config.CONSUMER_KEY, Config.CONSUMER_SECRET)
                .setBusinessShortCode(Config.BUSINESS_SHORTCODE)
                .setPassKey(AppUtils.getPassKey())
                .setTransactionType(Config.ACCOUNT_TYPE)
                .setCallbackUrl(Config.CALLBACK_URL)
                .setEnvironment(Environment.SANDBOX)
                .build();
    }

}
