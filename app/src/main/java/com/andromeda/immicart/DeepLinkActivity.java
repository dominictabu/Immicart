package com.andromeda.immicart;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.andromeda.immicart.delivery.ProductDetailActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;

public class DeepLinkActivity extends AppCompatActivity {

    private final String TAG  = "DeepLinkActivity";
    private final String QUERY_PARAM_PRODUCT_ID  = "QUERY_PARAM_PRODUCT_ID";
    private final String PRODUCT_ID  = "PRODUCT_ID";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deep_link);


        // Get tender key from intent
        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(getIntent())
                .addOnSuccessListener(this, new OnSuccessListener<PendingDynamicLinkData>() {
                    @Override
                    public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
                        Log.d(TAG, "getDynamicLink: on Success");
                        // Get deep link from result (may be null if no link is found)
                        Uri deepLink = null;
                        if (pendingDynamicLinkData != null) {
                            deepLink = pendingDynamicLinkData.getLink();
                            Log.d(TAG, "getDynamicLink: deepLink not null");
                            Log.d(TAG, "deepLink: " + deepLink.toString());
                        } else {
                            Log.d(TAG, "getDynamicLink: pendingDynamicLinkData null");

                        }

                        if (deepLink != null) {
                            String productId = deepLink.getQueryParameter(QUERY_PARAM_PRODUCT_ID);
                            Log.d(TAG, "getQueryParameter: mTenderKey: " + productId);
                            Intent intent = new Intent(DeepLinkActivity.this, ProductDetailActivity.class);
                            intent.putExtra(PRODUCT_ID, productId);
                            startActivity(intent);
                            finish();
                        } else {
                            Log.d(TAG, "getDynamicLink: no link found");
                        }
                    }
                }).addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "getDynamicLink:onFailure", e);
                //finish();
            }
        });
    }
}
