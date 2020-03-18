package com.andromeda.immicart.delivery.search.algolia;


import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import com.andromeda.immicart.delivery.search.algolia.MyViewModel;

public class SearchViewModelFactory implements ViewModelProvider.Factory {

//    private Application mApplication;
    private String algoliaIndex;

    public SearchViewModelFactory(String extra) {
        algoliaIndex = extra;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {

        if (modelClass.isAssignableFrom(MyViewModel.class)) {
            return (T) new MyViewModel(algoliaIndex);
        }
        //noinspection unchecked
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
