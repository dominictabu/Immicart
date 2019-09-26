package com.andromeda.payments;

import com.andromeda.payments.callback.DarajaCallback;
import com.andromeda.payments.callback.DarajaListener;
import com.andromeda.payments.callback.DarajaPaymentCallback;
import com.andromeda.payments.callback.DarajaPaymentListener;
import com.andromeda.payments.constants.TransactionType;
import com.andromeda.payments.model.AccessToken;
import com.andromeda.payments.repository.DarajaRepository;

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
