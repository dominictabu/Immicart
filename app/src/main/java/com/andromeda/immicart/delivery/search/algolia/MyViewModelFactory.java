package com.andromeda.immicart.delivery.search.algolia;


import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

/**
 * Factory for ViewModels
 */
public class MyViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    MyViewModel myViewModel;

    public MyViewModelFactory() {
        if(myViewModel == null) {
            myViewModel = new MyViewModel();
        }

    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(MyViewModel.class)) {
            return (T) myViewModel;
        }
        //noinspection unchecked
        throw new IllegalArgumentException("Unknown ViewModel class");
    }


}
