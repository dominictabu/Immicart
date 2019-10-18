package com.andromeda.immicart.checkout;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.androidstudy.daraja.Daraja;
import com.androidstudy.daraja.DarajaListener;
import com.androidstudy.daraja.model.AccessToken;
import com.androidstudy.daraja.model.LNMExpress;
import com.androidstudy.daraja.model.LNMResult;
import com.androidstudy.daraja.util.TransactionType;
import com.andromeda.immicart.R;

import static com.andromeda.immicart.checkout.Constants.IMMICART_CONSUMER_KEY;
import static com.andromeda.immicart.checkout.Constants.IMMICART_CONSUMER_SECRET;

public class CheckoutActivity extends AppCompatActivity {

    LinearLayout layout;
    Button payButton;
    EditText editText;
    Daraja daraja;
    String phoneNumber; //Get this from the user input
    String paybillNumber = "174379";
    static final String passKey = "bfb279f9aa9bdbcf158e97dd71a467cd2e0c893059b10f78e6b72ada1ed2c919";
    String amount = ""; //Fetch this information from the amount
    String partyA = "";
    String partyB = "";
    static final String callbackUrl = ""; //Use our callback url
    String accountReference = "";
    String transactionDescription = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        payButton = findViewById(R.id.button_pay);
        editText = findViewById(R.id.phoneNumber_et);

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

        payButton.setOnClickListener({

            //Get Phone Number from User Input
            phoneNumber = editText.getText().toString().trim();

            if (TextUtils.isEmpty(phoneNumber)) {
                editText.setError("Please Provide a Phone Number");
                return;
            }




            LNMExpress lnmExpress = new LNMExpress(
                    paybillNumber, //Supermarket paybill number to be fetched at runtime
                    passKey,  //Api pass key to be stored in the config file. This one is for testing
                    TransactionType.CustomerBuyGoodsOnline, //The main one used by supermarkets is the business till number
                    // TransactionType.CustomerBuyGoodsOnline, // TransactionType.CustomerPayBillOnline  <- Apply one of these two The
                    amount,// Amount to be fetched from the total amount to be paid by the customer
                    phoneNumber, //Phone number initiating the payments
                    partyA, //Business paybill number. To be fetched at runtime
                    partyB,
                    callbackUrl, // Url where all these will be stored
                    accountReference, //This will basically be the name of the store
                    transactionDescription);

            daraja.requestMPESAExpress(lnmExpress,
                    new DarajaListener<LNMResult>() {
                        @Override
                        public void onResult(@NonNull LNMResult lnmResult) {
                            Log.i(CheckoutActivity.this.getClass().getSimpleName(), lnmResult.ResponseDescription);
                        }

                        @Override
                        public void onError(String error) {
                            Log.i(CheckoutActivity.this.getClass().getSimpleName(), error);
                        }
                    }
            );
        });
    }
}
