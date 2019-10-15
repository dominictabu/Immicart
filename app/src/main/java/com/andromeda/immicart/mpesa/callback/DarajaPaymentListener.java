package com.andromeda.immicart.mpesa.callback;

import com.andromeda.immicart.mpesa.model.PaymentResult;

public interface DarajaPaymentListener {

    void onPaymentRequestComplete(PaymentResult result);

    void onPaymentFailure(DarajaException exception);

    void onNetworkFailure(DarajaException exception);
}

