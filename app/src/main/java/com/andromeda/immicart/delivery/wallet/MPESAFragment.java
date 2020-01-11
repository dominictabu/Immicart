package com.andromeda.immicart.delivery.wallet;


import android.graphics.Color;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.*;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.androidstudy.daraja.Daraja;
import com.androidstudy.daraja.DarajaListener;
import com.androidstudy.daraja.model.LNMExpress;
import com.androidstudy.daraja.model.LNMResult;
import com.androidstudy.daraja.util.TransactionType;
import com.andromeda.immicart.R;
import com.andromeda.immicart.delivery.trackingorder.TrackOrderBottomSheet;
import com.andromeda.immicart.delivery.wallet.stkPush.ApiClient;
import com.andromeda.immicart.delivery.wallet.stkPush.model.AccessToken;
import com.andromeda.immicart.delivery.wallet.stkPush.model.STKPush;
import com.andromeda.immicart.delivery.wallet.stkPush.util.Utils;
import com.andromeda.immicart.networking.ImmicartAPIService;
import com.andromeda.immicart.networking.Model;
import com.google.android.gms.tasks.*;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.*;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.FirebaseFunctionsException;
import com.google.firebase.functions.HttpsCallableResult;
import org.jetbrains.annotations.NotNull;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static com.andromeda.immicart.delivery.wallet.stkPush.util.AppConstants.*;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MPESAFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MPESAFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "MPESAFragment";
    String Url = "https://us-central1-immicart-2ca69.cloudfunctions.net/mpesaCallbackURL";
    String onCallURL = "https://us-central1-immicart-2ca69.cloudfunctions.net/addMessage";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    Daraja daraja;
    String phoneNumber; //Get this from the user input
//    String paybillNumber = "174379";
    String paybillNumber = "174379";
    static final String passKey = "bfb279f9aa9bdbcf158e97dd71a467cd2e0c893059b10f78e6b72ada1ed2c919";
    String amount = "1000"; //Fetch this information from the amount
    String partyA = "174379";
    String partyB = "174379";
    static final String callbackUrl = "http://mycallbackurl.com/checkout.php"; //Use our callback url
    String accountReference = "001ABC";
    String transactionDescription = "Immicart Account Deposit";

    EditText mobileNumberEdtxt;
    EditText amount_edittext;
    TextView error_txt_amount;
    Button continueBtn;
    LinearLayout loadingLayout;
    Button disabledLayout;
    ImageButton go_back_button;
    TextView error_txt;
    private FirebaseFunctions mFunctions;

    private ApiClient mApiClient;
    FirebaseFirestore db;
    FirebaseUser currentFirebaseUser;


    public MPESAFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MPESAFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MPESAFragment newInstance(String param1, String param2) {
        MPESAFragment fragment = new MPESAFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_mpesa, container, false);
        mobileNumberEdtxt = view.findViewById(R.id.number_edittext);
        amount_edittext = view.findViewById(R.id.amount_edittext);
        error_txt_amount = view.findViewById(R.id.error_txt_amount);
        continueBtn = view.findViewById(R.id.continue_button);
        go_back_button = view.findViewById(R.id.go_back_button);
        disabledLayout = view.findViewById(R.id.continue_button_disabled);
        loadingLayout = view.findViewById(R.id.loadinglayout);
        error_txt = view.findViewById(R.id.error_txt);
        return  view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        mFunctions = FirebaseFunctions.getInstance();
        db = FirebaseFirestore.getInstance();
        currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;


        mApiClient = new ApiClient();
        mApiClient.setIsDebug(true); //Set True to enable logging, false to disable.
        getAccessToken();



        mobileNumberEdtxt.requestFocus();
        mobileNumberEdtxt.addTextChangedListener(new PhoneNumberFormattingTextWatcher());

        mobileNumberEdtxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                String phoneNumber = s.toString();

                amount_edittext.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        String amount = s.toString();
                        if(validateNumber(phoneNumber) && !TextUtils.isEmpty(amount)) {
                            continueBtn.setVisibility(View.VISIBLE);
                            disabledLayout.setVisibility(View.GONE);
                            loadingLayout.setVisibility(View.GONE);

                        } else {
                            continueBtn.setVisibility(View.GONE);
                            disabledLayout.setVisibility(View.VISIBLE);
                            loadingLayout.setVisibility(View.GONE);
                        }


                    }
                });

            }
        });

        go_back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();

            }
        });
//        showSuccessDialog();





//        daraja = Daraja.with(IMMICART_CONSUMER_KEY, IMMICART_CONSUMER_SECRET, new DarajaListener<AccessToken>() {
//            @Override
//            public void onResult(@NonNull AccessToken accessToken) {
//                Log.d(TAG, "Access Token " + accessToken.getAccess_token());
//
////               Show logs
//
//
//            }
//
//            @Override
//            public void onError(String error) {
////             TODO  Show error message
//
//            }
//        });


        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String number = mobileNumberEdtxt.getText().toString();
                String amount = amount_edittext.getText().toString();
                if(TextUtils.isEmpty(number)) {
                    error_txt.setVisibility(View.VISIBLE);
                    error_txt.setText("Number is required");

                } else if (!validateNumber(number)) {
                    error_txt.setVisibility(View.VISIBLE);
                    error_txt.setText("Number is not correct");

//                    String phoneNumber = formatPhoneNumber(number);
//                    invokeMpesa(number);
//                    error_txt.setVisibility(View.GONE);

//                    invokeMpesa(number);

                } else if(TextUtils.isEmpty(amount)){

                    error_txt_amount.setVisibility(View.VISIBLE);
//                }  else if(Integer.valueOf(amount) < 100) {
//                    error_txt_amount.setVisibility(View.VISIBLE);

                } else {
                    mobileNumberEdtxt.clearFocus();
                    amount_edittext.clearFocus();
                    String phoneNumber = formatPhoneNumber(number);
                    error_txt.setVisibility(View.GONE);
                    error_txt_amount.setVisibility(View.GONE);
//                    TrackOrderBottomSheet addPhotoBottomDialogFragment =
//                            TrackOrderBottomSheet.newInstance();
//                    addPhotoBottomDialogFragment.show(getChildFragmentManager(),
//                            "add_photo_dialog_fragment");
//                    invokeMpesa(phoneNumber);

                    recordRequest(amount, phoneNumber);

//                    mpesaStkPushOnCall(phoneNumber, amount)

//                            .addOnCompleteListener(new OnCompleteListener<String>() {
//                                @Override
//                                public void onComplete(@NonNull Task<String> task) {
//                                    if (!task.isSuccessful()) {
//                                        Exception e = task.getException();
//                                        if (e instanceof FirebaseFunctionsException) {
//                                            FirebaseFunctionsException ffe = (FirebaseFunctionsException) e;
//                                            FirebaseFunctionsException.Code code = ffe.getCode();
//                                            Log.d(TAG, "Status Code Error: " + code);
//                                            Object details = ffe.getDetails();
//                                            Log.d(TAG, "Details: " + details);
//                                        }
//
//                                        // ...
//                                    }
//
//                                    // ...
//                                }
//                            });


                }

            }
        });


    }


    private Task<String> mpesaStkPushOnCall(String phone, String amount) {
        Log.d(TAG, "mpesaStkPushOnCall called");
        // Create the arguments to the callable function.
        Map<String, Object> data = new HashMap<>();
        data.put("phone", phone);
        data.put("amount", amount);
        data.put("push", true);

        return mFunctions
                .getHttpsCallable("mpesaStkPushOnCall")
                .call(data)
                .continueWith(new Continuation<HttpsCallableResult, String>() {
                    @Override
                    public String then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                        // This continuation runs on either success or failure, but if the task
                        // has failed then getResult() will throw an Exception which will be
                        // propagated down.

                        String result = (String) task.getResult().getData();
                        Log.d(TAG, "mpesaStkPushOnCall result: " + result);

                        return result;

                    }
                });
    }


    void makeRequest() {
        ImmicartAPIService immicartAPIService = ImmicartAPIService.create();
        immicartAPIService.getMPESAResponse().enqueue(new Callback<Model.MPESAResponse>() {
            @Override
            public void onResponse(@NotNull Call<Model.MPESAResponse> call, @NotNull Response<Model.MPESAResponse> response) {
                Log.d(TAG, "MPESA RESPONSE " + response.body());
                Log.d(TAG, "MPESA RESPONSE STATUS CODE" + response.code());

            }

            @Override
            public void onFailure(Call<Model.MPESAResponse> call, Throwable t) {
                Log.d(TAG, "MPESA Throwable: " + t);

            }
        });

    }


    boolean validateNumber(String number) {

        //matches 10-digit numbers only
        String regexStr = "^[0-9]{10}$";
        if(number.matches(regexStr) && number.startsWith("07")) {
            return true;
        } else {
            return false;
        }

    }

    //The MSISDN sending the funds
    public static String formatPhoneNumber(String phoneNumber) {
        if (phoneNumber.equals("")) {
            return "";
        }
        if (phoneNumber.length() < 11 & phoneNumber.startsWith("0")) {
            //here we can just remove the inline variable instead of the p. Like you did with the rest
            //String p = phoneNumber.replaceFirst("^0", "254");
            //return p
            return phoneNumber.replaceFirst("^0", "254");
        }
        if (phoneNumber.length() == 13 && phoneNumber.startsWith("+")) {
            return phoneNumber.replaceFirst("^+", "");
        }
        return phoneNumber;
    }

    public void invokeMpesa(String phoneNumber) {

        //Get Phone Number from User Input
        TrackOrderBottomSheet addPhotoBottomDialogFragment =
                TrackOrderBottomSheet.newInstance();
        addPhotoBottomDialogFragment.show(getChildFragmentManager(),
                "add_photo_dialog_fragment");


        //TODO :: REPLACE WITH YOUR OWN CREDENTIALS  :: THIS IS SANDBOX DEMO
        LNMExpress lnmExpress = new LNMExpress(
                paybillNumber, //Supermarket paybill number to be fetched at runtime
                passKey,  //Api pass key to be stored in the config file. This one is for testing
                TransactionType.CustomerPayBillOnline, //The main one used by supermarkets is the business till number
                // TransactionType.CustomerBuyGoodsOnline, // TransactionType.CustomerPayBillOnline  <- Apply one of these two The
                amount,// Amount to be fetched from the total amount to be paid by the customer
                partyA, //Business paybill number. To be fetched at runtime
                partyB,
                phoneNumber,
                Url, // Url where all these will be stored
                accountReference, //This will basically be the name of the store
                transactionDescription);


        daraja.requestMPESAExpress(lnmExpress,
                new DarajaListener<LNMResult>() {
                    @Override
                    public void onResult(@NonNull LNMResult lnmResult) {
                        makeRequest();
                        Log.i(MPESAFragment.this.getClass().getSimpleName(), lnmResult.ResponseDescription);
                    }

                    @Override
                    public void onError(String error) {
                        Log.i(MPESAFragment.this.getClass().getSimpleName(), error);
                    }
                }
        );
    }


    public void getAccessToken() {
        mApiClient.setGetAccessToken(true);
        mApiClient.mpesaService().getAccessToken().enqueue(new Callback<AccessToken>() {
            @Override
            public void onResponse(@NonNull Call<AccessToken> call, @NonNull Response<AccessToken> response) {

                if (response.isSuccessful()) {
                    mApiClient.setAuthToken(response.body().accessToken);
                }
            }

            @Override
            public void onFailure(@NonNull Call<AccessToken> call, @NonNull Throwable t) {

            }
        });
    }

    public void performSTKPush(String phone_number, String amount, String documentID) {
        TrackOrderBottomSheet addPhotoBottomDialogFragment =
                TrackOrderBottomSheet.newInstance();
        addPhotoBottomDialogFragment.show(getChildFragmentManager(),
                "add_photo_dialog_fragment");
        String uid = currentFirebaseUser.getUid();


        String timestamp = Utils.getTimestamp();
        STKPush stkPush = new STKPush(
                BUSINESS_SHORT_CODE,
                Utils.getPassword(BUSINESS_SHORT_CODE, PASSKEY, timestamp),
                timestamp,
                TRANSACTION_TYPE,
                amount,
                phone_number,
                PARTYB,
                phone_number,
                CALLBACKURL + uid + "&documentID=" + documentID,
                "test", //The account reference
                "test"  //The transaction description
        );

        mApiClient.setGetAccessToken(false);

        mApiClient.mpesaService().sendPush(stkPush).enqueue(new Callback<STKPush>() {
            @Override
            public void onResponse(@NonNull Call<STKPush> call, @NonNull Response<STKPush> response) {
                try {
                    if (response.isSuccessful()) {
                        snapshotListeners(documentID);
                        Log.d(TAG, "STK PUSH PROMPT Successful" + response.body());
                        Timber.d("post submitted to API. %s", response.body());
                    } else {
                        showErrorDialog();
                        addPhotoBottomDialogFragment.dismiss();
                        Log.d(TAG, "STK PUSH PROMPT UnSuccessful" + response.errorBody().string());
                        Timber.e("Response %s", response.errorBody().string());
                    }
                } catch (Exception e) {
                    addPhotoBottomDialogFragment.dismiss();
                    e.printStackTrace();
                    showErrorDialog();

                }
            }

            @Override
            public void onFailure(@NonNull Call<STKPush> call, @NonNull Throwable t) {
//              TODO  mProgressDialog.dismiss();
                Timber.e(t);
            }
        });
    }


    void showErrorDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
// ...Irrelevant code for customizing the buttons and title
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.top_up_failed_alert_dialog, null);
        dialogBuilder.setView(dialogView);

        Button button = (Button) dialogView.findViewById(R.id.close_dialog);

        AlertDialog alertDialog = dialogBuilder.create();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                alertDialog.dismiss();
                if(getActivity() != null)
                    getActivity().finish();
            }
        });

        alertDialog.show();
    }

    void showSuccessDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
// ...Irrelevant code for customizing the buttons and title
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.top_up_successful_dialog, null);
        dialogBuilder.setView(dialogView);

        Button button = (Button) dialogView.findViewById(R.id.close_dialog);

        String s= "KES 500 was sucessfully deposited to your Immicart Wallet";
        SpannableString ss1=  new SpannableString(s);
        ss1.setSpan(new RelativeSizeSpan(1.2f), 0,7, 0); // set size
        ss1.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 0, 7, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//        ss1.setSpan(new ForegroundColorSpan(Color.RED), 0, 6, 0);// set color
        TextView tv= (TextView) dialogView.findViewById(R.id.message);
        tv.setText(ss1);

        AlertDialog alertDialog = dialogBuilder.create();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getActivity() != null)
                    getActivity().finish();
            }
        });

        alertDialog.show();
    }


    void snapshotListeners(String documentID) {
        String uid = currentFirebaseUser.getUid();

        String docPath = "wallet/" + uid +"/requests/" +  documentID;
        final DocumentReference docRef = db.document(docPath);
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.d(TAG ,"Listen failed.", e);
                    return;
                }

                String source = snapshot != null && snapshot.getMetadata().hasPendingWrites()
                        ? "Local" : "Server";

                if (snapshot != null && snapshot.exists()) {
                    Log.d(TAG, source + " data: " + snapshot.getData());

                    Map<String, Object> stringObjectMap = snapshot.getData();
                    if (stringObjectMap != null) {
                        if(stringObjectMap.containsKey("success")) {
                            boolean success = (boolean) stringObjectMap.get("success");

                                if(success) {
                                    Log.d(TAG, "Success true");
                                    showSuccessDialog();
                                } else {
                                    Log.d(TAG, "Success false");
                                    showErrorDialog();
                                }
                            }
                        }

                    //TODO Show Error or success dialog
                } else {
                    Log.d(TAG, source + " data: null");
                }
            }
        });

    }


    void updateCreditsAmount(int currentAmountModified) {



        String uid = currentFirebaseUser.getUid();
        String documentPath = "customers/" + uid + "/wallet/data";


        final DocumentReference sfDocRef = db.document(documentPath);

        db.runTransaction(new Transaction.Function<Long>() {
            @Override
            public Long apply(@NotNull Transaction transaction) throws FirebaseFirestoreException {
                DocumentSnapshot snapshot = transaction.get(sfDocRef);
                if(snapshot.contains("credit")) {
                    int newCredit = (int) snapshot.get("credit") + currentAmountModified;

                    transaction.update(sfDocRef, "credit", newCredit);
                    return (long) newCredit;

                } else {

                    transaction.update(sfDocRef, "credit", currentAmountModified);
                    return (long) currentAmountModified;

                }
//                if (newPopulation <= 1000000) {
//                    transaction.update(sfDocRef, "population", newPopulation);
//                    return newPopulation;
//                } else {
//                    throw new FirebaseFirestoreException("Population too high",
//                            FirebaseFirestoreException.Code.ABORTED);
//                }
            }
        }).addOnSuccessListener(new OnSuccessListener<Long>() {
            @Override
            public void onSuccess(Long result) {
                Log.d(TAG, "Transaction success: " + result);
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Transaction failure.", e);
                    }
                });


    }




    void recordRequest(String amount, String phone) {

        continueBtn.setVisibility(View.GONE);
        disabledLayout.setVisibility(View.GONE);
        loadingLayout.setVisibility(View.VISIBLE);

        Map<String, Object> mpesaRequest = new HashMap<>();
        mpesaRequest.put("amount", amount);
        mpesaRequest.put("phone", phone);
        String uid = currentFirebaseUser.getUid();
        String collectionPath = "wallet/" + uid + "/requests";


        db.collection(collectionPath).add(mpesaRequest)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                        String documentID = documentReference.getId();
                        performSTKPush(phone, amount, documentID);


                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);

                    }
                });
    }


    void performTransaction() {

    }
}
