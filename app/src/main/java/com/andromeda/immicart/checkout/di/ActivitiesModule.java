package com.andromeda.immicart.checkout.di;

import com.andromeda.immicart.checkout.ui.PaymentActivity;
import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
abstract class ActivitiesModule {

    @ContributesAndroidInjector
    abstract PaymentActivity contributesPaymentActivity();


}
