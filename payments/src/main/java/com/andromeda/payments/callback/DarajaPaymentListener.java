package com.andromeda.payments.callback;

import com.andromeda.payments.model.PaymentResult;

public interface DarajaPaymentListener {

    void onPaymentRequestComplete(PaymentResult result);

    void onPaymentFailure(DarajaException exception);

    void onNetworkFailure(DarajaException exception);
}

