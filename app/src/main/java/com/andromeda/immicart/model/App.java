package com.andromeda.immicart.model;

import android.app.Application;
import com.andromeda.immicart.R;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("@font/roboto")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
    }
}