package com.andromeda.immicart.checkout;

import android.view.View;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.androidstudy.daraja.Daraja;
import com.androidstudy.daraja.DarajaListener;
import com.androidstudy.daraja.model.AccessToken;
import com.androidstudy.daraja.model.LNMExpress;
import com.androidstudy.daraja.util.TransactionType;
import com.andromeda.immicart.R;

import static com.andromeda.immicart.checkout.Constants.IMMICART_CONSUMER_KEY;
import static com.andromeda.immicart.checkout.Constants.IMMICART_CONSUMER_SECRET;

public class CheckoutActivity extends AppCompatActivity {

    LinearLayout layout;

    Daraja daraja;
    String phoneNumber;
    String paybillNumber = "174379";
    static final String passKey = "bfb279f9aa9bdbcf158e97dd71a467cd2e0c893059b10f78e6b72ada1ed2c919";
    String amount = "";
    String partyA = "";
    String partyB = "";
    static final String callbackUrl = ""; //Use our callback url
    String accountReference = "";
    String transactionDescription = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        daraja = Daraja.with(IMMICART_CONSUMER_KEY, IMMICART_CONSUMER_SECRET, new DarajaListener<AccessToken>() {
            @Override
            public void onResult(@NonNull AccessToken accessToken) {

//               Show logs


            }

            @Override
            public void onError(String error) {
//             TODO  Show error message

            }
        });


        layout = findViewById(R.id.checkoutlayout);
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }


    LNMExpress lnmExpress = new LNMExpress(
            paybillNumber,
            passKey,
            TransactionType.CustomerBuyGoodsOnline, // TransactionType.CustomerBuyGoodsOnline, // TransactionType.CustomerPayBillOnline  <- Apply one of these two
            amount,
            phoneNumber,
            paybillNumber,
            phoneNumber,
            callbackUrl,
            accountReference,
            transactionDescription);

//    stkPush



}
