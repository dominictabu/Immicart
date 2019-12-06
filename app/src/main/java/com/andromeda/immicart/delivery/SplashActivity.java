package com.andromeda.immicart.delivery;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.andromeda.immicart.R;
import com.andromeda.immicart.delivery.authentication.AuthenticationActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_splash);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        startActivity(new Intent(this, AuthenticationActivity.class));

    }
}
