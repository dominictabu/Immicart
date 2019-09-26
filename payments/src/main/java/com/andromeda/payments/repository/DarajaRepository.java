package com.andromeda.payments.repository;

import com.andromeda.payments.constants.TransactionType;
import com.andromeda.payments.model.AccessToken;
import com.andromeda.payments.model.LNMExpress;
import com.andromeda.payments.model.PaymentResult;
import com.andromeda.payments.network.ApiClient;
import com.andromeda.payments.network.AuthAPI;
import com.andromeda.payments.utils.Settings;
import retrofit2.Call;

public class DarajaRepository {
    String consumerKey;
    String consumerSecret;
    String baseUrl;

    AuthAPI authApi;

    public DarajaRepository(String consumerKey, String consumerSecret, String baseUrl) {
        this.consumerKey = consumerKey;
        this.consumerSecret = consumerSecret;
        this.baseUrl = baseUrl;

        authApi = ApiClient.getAuthAPI(consumerKey, consumerSecret, baseUrl);
    }

    public Call<AccessToken> getAccessToken() {
        return ApiClient
                .getAuthAPI(consumerKey, consumerSecret, baseUrl)
                .getAccessToken();
    }

    //TODO('Refactor')
    public Call<PaymentResult> initiatePayment(String token, String phoneNumber, String amount,
                                               String accountReference, String description, String businessShortCode, String passKey,
                                               TransactionType transactionType, String callbackUrl) {

        String sanitizedPhoneNumber = Settings.formatPhoneNumber(phoneNumber);
        String timeStamp = Settings.generateTimestamp();
        String generatedPassword = Settings.generatePassword(businessShortCode, passKey, timeStamp);

        LNMExpress express = new LNMExpress(
                businessShortCode,
                generatedPassword,
                timeStamp,
                amount,
                transactionType.toString(),
                phoneNumber,
                businessShortCode,
                sanitizedPhoneNumber,
                callbackUrl,
                accountReference,
                description
        );

        return ApiClient.getAPI(baseUrl, token).getLNMPesa(express);
    }
}
