package com.andromeda.immicart.delivery;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.andromeda.immicart.R;
import com.andromeda.immicart.delivery.Utils.MyDatabaseUtil;
import com.andromeda.immicart.delivery.ar.SceneformActivity;
import com.andromeda.immicart.delivery.authentication.AuthenticationActivity;
import com.andromeda.immicart.delivery.authentication.SelectStoreActivity;
import com.andromeda.immicart.delivery.furniture.FurnitureMainActivity;
import com.andromeda.immicart.delivery.furniture.FurnitureProductPageActivity;
import com.andromeda.immicart.delivery.furniture.MultiPhotoActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_splash);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

//        startActivity(new Intent(this, AuthenticationActivity.class));
//        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser auth = FirebaseAuth.getInstance().getCurrentUser();

        if(auth != null) {
//            getCurrentStore(auth.getUid());
            startActivity(new Intent(this, SelectStoreActivity.class));


        } else {
            startActivity(new Intent(SplashActivity.this, AuthenticationActivity.class));

        }
    }


    void getCurrentStore(String userID) {
        DatabaseReference ref = MyDatabaseUtil.getDatabase().getReference().child("customers/" + userID + "/current_store");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                CurrentStore store = dataSnapshot.getValue(CurrentStore.class);

                if(store != null) {
                    startActivity(new Intent(SplashActivity.this, ProductsPageActivity.class));
                } else {
                    startActivity(new Intent(SplashActivity.this, SelectStoreActivity.class));
                }
                finish();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
