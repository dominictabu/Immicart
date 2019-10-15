package com.andromeda.immicart.mpesa;

import com.andromeda.immicart.mpesa.callback.DarajaCallback;
import com.andromeda.immicart.mpesa.callback.DarajaListener;
import com.andromeda.immicart.mpesa.callback.DarajaPaymentCallback;
import com.andromeda.immicart.mpesa.callback.DarajaPaymentListener;
import com.andromeda.immicart.mpesa.constants.TransactionType;
import com.andromeda.immicart.mpesa.model.AccessToken;
import com.andromeda.immicart.mpesa.repo.DarajaRepository;

public class Daraja {

    String consumerKey;
    String consumerSecret;
    String businessShortCode;
    String passKey;
    TransactionType transactionType;
    String callbackUrl;
    String baseUrl;

    DarajaRepository repo;

    public static Builder Builder(String consumerKey, String consumerSecret){
        return new Builder(consumerKey, consumerSecret);
    }

    protected Daraja() {
    }

    public DarajaListener<AccessToken> getAccessToken(final DarajaListener<AccessToken> listener) {
        repo.getAccessToken().enqueue(new DarajaCallback(listener));
        return listener;
    }

    public DarajaPaymentListener initiatePayment(String token, String phoneNumber, String amount,
                                                 String accountReference, String description, final DarajaPaymentListener listener) {

        repo.initiatePayment(token, phoneNumber, amount, accountReference, description,
                businessShortCode, passKey, transactionType, callbackUrl)
                .enqueue(new DarajaPaymentCallback(listener));

        return listener;
    }
}

