package com.andromeda.immicart.checkout;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.androidstudy.daraja.Daraja;
import com.androidstudy.daraja.DarajaListener;
import com.androidstudy.daraja.model.AccessToken;
import com.androidstudy.daraja.model.LNMExpress;
import com.androidstudy.daraja.model.LNMResult;
import com.androidstudy.daraja.util.TransactionType;
import com.andromeda.immicart.R;
import butterknife.OnClick;
import com.andromeda.immicart.Scanning.persistence.Cart;

import java.util.ArrayList;
import java.util.List;

import static com.andromeda.immicart.checkout.Constants.IMMICART_CONSUMER_KEY;
import static com.andromeda.immicart.checkout.Constants.IMMICART_CONSUMER_SECRET;

public class CheckoutActivity extends AppCompatActivity {

    private static final String TAG = "CheckoutActivity";
    LinearLayout layout;
    @BindView(R.id.button_pay)
    Button payButton;
    @BindView(R.id.phoneNumber_et)
    EditText editText;
    @BindView(R.id.cart_recycler_view)
    RecyclerView imagesRecyclerView;
    @BindView(R.id.cart_items_recycler)
    RecyclerView cart_items_recycler;
    @BindView(R.id.cart_items_ll)
    LinearLayout cart_items_linearLayout;
    @BindView(R.id.recycler_ll)
    RelativeLayout cart_items_relativeLayout;
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
    List<Cart> cartList = new ArrayList<>();

    CheckOutViewModel checkOutViewModel;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        cart_items_linearLayout.setVisibility(View.GONE);
        cart_items_relativeLayout.setVisibility(View.VISIBLE);
//        imagesRecyclerView = findViewById(R.id.cart_recycler_view);

        checkOutViewModel = ViewModelProviders.of(this).get(CheckOutViewModel.class);

        checkOutViewModel.getAllScannedItems().observe(this, new Observer<List<Cart>>() {
            @Override
            public void onChanged(List<Cart> carts) {
                cartList = carts;
                ArrayList<String> images = new ArrayList<>();
                for(Cart cart : carts) {
                    images.add(cart.getImage_url());
                }
                loadImages(images);

            }
        });

        daraja = Daraja.with(IMMICART_CONSUMER_KEY, IMMICART_CONSUMER_SECRET, new DarajaListener<AccessToken>() {
            @Override
            public void onResult(@NonNull AccessToken accessToken) {
                Log.d(TAG, "Access Token " + accessToken.getAccess_token());

//               Show logs


            }

            @Override
            public void onError(String error) {
//             TODO  Show error message

            }
        });



    }

    @OnClick(R.id.button_pay)
    public void continurToPayment() {
        phoneNumber = editText.getText().toString().trim();

        if (TextUtils.isEmpty(phoneNumber)) {
            invokeMpesa(phoneNumber);


        } else {
            editText.setError("Please Provide a Phone Number");

//            Toast.makeText(CartActivity.this, "Select payment option", Toast.LENGTH_LONG).show();
        }
    }

    @OnClick(R.id.collapse_button)
    public void closeCartItems() {
        cart_items_linearLayout.setVisibility(View.GONE);
        cart_items_relativeLayout.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.expand_button)
    public void expandCartItems() {

        cart_items_linearLayout.setVisibility(View.VISIBLE);
        cart_items_relativeLayout.setVisibility(View.GONE);


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(CheckoutActivity.this, RecyclerView.VERTICAL, false);
        cart_items_recycler.setLayoutManager(linearLayoutManager);
        CartItemsAdapter cartItemsAdapter = new CartItemsAdapter(cartList, CheckoutActivity.this);
        cart_items_recycler.setAdapter(cartItemsAdapter);

    }


    void loadImages(List<String> imagesUrl) {

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(CheckoutActivity.this, RecyclerView.HORIZONTAL, false);
        imagesRecyclerView.setLayoutManager(linearLayoutManager);
        CartImagesAdapter cartImagesAdapter = new CartImagesAdapter(imagesUrl, CheckoutActivity.this);
        imagesRecyclerView.setAdapter(cartImagesAdapter);

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
                        Log.i(CheckoutActivity.this.getClass().getSimpleName(), lnmResult.ResponseDescription);
                    }

                    @Override
                    public void onError(String error) {
                        Log.i(CheckoutActivity.this.getClass().getSimpleName(), error);
                    }
                }
        );
    }
}
