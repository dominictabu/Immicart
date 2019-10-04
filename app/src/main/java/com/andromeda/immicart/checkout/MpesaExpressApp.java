package com.andromeda.immicart.checkout;


import dagger.android.AndroidInjector;
import dagger.android.support.DaggerApplication;

public class MpesaExpressApp extends DaggerApplication {

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected AndroidInjector<? extends DaggerApplication> applicationInjector() {
        return DaggerAppComponent.create();
    }
}
