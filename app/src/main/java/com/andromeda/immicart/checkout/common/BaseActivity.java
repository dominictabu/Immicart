package com.andromeda.immicart.checkout.common;

import android.annotation.SuppressLint;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import com.andromeda.immicart.checkout.ui.ProgressDialogFragment;
import dagger.android.support.DaggerAppCompatActivity;

import javax.inject.Inject;

@SuppressLint("Registered")
public class BaseActivity extends DaggerAppCompatActivity {

    @Inject
    public ViewModelProvider.Factory viewModelFactory;

    public <T extends ViewModel> T getViewModel(final Class<T> cls) {
        return ViewModelProviders.of(this, viewModelFactory).get(cls);
    }

    ProgressDialogFragment progressDialog;

    public void showLoading(String title, String message) {
        progressDialog = ProgressDialogFragment.newInstance(title, message);
        progressDialog.setCancelable(false);
        progressDialog.show(getSupportFragmentManager(), "progress");
    }

    public void showLoading() {
        showLoading("This will only take a sec", "Loading");
    }

    public void stopShowingLoading() {
        progressDialog.dismiss();
    }

}
