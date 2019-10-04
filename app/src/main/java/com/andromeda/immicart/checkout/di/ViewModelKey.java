package com.andromeda.immicart.checkout.di;

import androidx.lifecycle.ViewModel;
import dagger.MapKey;

import java.lang.annotation.*;

@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@MapKey
@interface ViewModelKey {
    Class<? extends ViewModel> value();
}
