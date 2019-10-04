package com.andromeda.immicart.checkout.di;


import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import com.andromeda.immicart.checkout.util.ViewModelFactory;
import com.andromeda.immicart.checkout.viewmodel.PaymentViewModel;
import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

@SuppressWarnings("WeakerAccess")
@Module
public abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(PaymentViewModel.class)
    abstract ViewModel bindPaymentViewModel(PaymentViewModel viewModel);

    @Binds
    abstract ViewModelProvider.Factory bindViewModelFactory(ViewModelFactory factory);
}

