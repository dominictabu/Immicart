package com.andromeda.immicart.checkout.di;

import com.andromeda.immicart.checkout.MpesaExpressApp;
import dagger.Component;
import dagger.android.AndroidInjector;
import dagger.android.support.AndroidSupportInjectionModule;
import dagger.android.support.DaggerApplication;

import javax.inject.Singleton;

@Singleton
@Component(modules = {
        AndroidSupportInjectionModule.class,
        ViewModelModule.class,
        ActivitiesModule.class,
        AppModule.class
})
interface AppComponent extends AndroidInjector<DaggerApplication> {

    void inject(MpesaExpressApp app);

}
