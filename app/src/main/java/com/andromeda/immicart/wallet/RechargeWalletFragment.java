package com.andromeda.immicart.wallet;


import android.os.Bundle;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.androidstudy.daraja.Daraja;
import com.androidstudy.daraja.DarajaListener;
import com.androidstudy.daraja.model.AccessToken;
import com.androidstudy.daraja.model.LNMExpress;
import com.androidstudy.daraja.model.LNMResult;
import com.androidstudy.daraja.util.TransactionType;
import com.andromeda.immicart.R;
import com.andromeda.immicart.checkout.CheckoutActivity;

import static com.andromeda.immicart.checkout.Constants.IMMICART_CONSUMER_KEY;
import static com.andromeda.immicart.checkout.Constants.IMMICART_CONSUMER_SECRET;


public class RechargeWalletFragment extends Fragment {
    private static final String TAG = "CheckoutActivity";
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





    public RechargeWalletFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_recharge_wallet, container, false);



    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        daraja = Daraja.with(IMMICART_CONSUMER_KEY, IMMICART_CONSUMER_SECRET, new DarajaListener<AccessToken>() {
            @Override
            public void onResult(@NonNull AccessToken accessToken) {
                Log.d(TAG, "Access Token " + accessToken.getAccess_token());
//
//               Show logs


            }

            @Override
            public void onError(String error) {
//             TODO  Show error message

            }
        });

    }

    public void invokeMpesa(String phoneNumber) {

        //Get Phone Number from User Input



        //TODO :: REPLACE WITH YOUR OWN CREDENTIALS  :: THIS IS SANDBOX DEMO
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
                        Log.i(this.getClass().getSimpleName(), lnmResult.ResponseDescription);
                    }

                    @Override
                    public void onError(String error) {
                        Log.i(this.getClass().getSimpleName(), error);
                    }
                }
        );
    }

}
