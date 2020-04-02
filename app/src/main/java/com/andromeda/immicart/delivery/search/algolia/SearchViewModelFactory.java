package com.andromeda.immicart.delivery.search.algolia;


import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import com.algolia.search.client.ClientSearch;
import com.andromeda.immicart.delivery.search.algolia.MyViewModel;

public class SearchViewModelFactory implements ViewModelProvider.Factory {

//    private Application mApplication;
    private String algoliaIndex;
//    private Application mApplication;
    private AlgoliaCredentails client;

    public SearchViewModelFactory(AlgoliaCredentails client, String algoliaIndex) {
        this.algoliaIndex = algoliaIndex;
        this.client = client;

    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {

        if (modelClass.isAssignableFrom(MyViewModel.class)) {
            return (T) new MyViewModel(client, algoliaIndex);
        }
        //noinspection unchecked
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}