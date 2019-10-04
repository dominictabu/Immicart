package com.andromeda.immicart.checkout.common

import androidx.lifecycle.LiveData
import com.andromeda.immicart.mpesa.callback.DarajaException
import com.andromeda.immicart.mpesa.callback.DarajaPaymentListener
import com.andromeda.immicart.mpesa.model.PaymentResult

class DarajaPaymentLiveData : LiveData<Resource<PaymentResult>>(), DarajaPaymentListener {
    override fun onPaymentRequestComplete(result: PaymentResult) {
        value = Resource(result)
    }

    override fun onPaymentFailure(exception: DarajaException) {
        value = Resource(DarajaException(exception.message))
    }

    override fun onNetworkFailure(exception: DarajaException?) {
        value = Resource(DarajaException(exception!!.message))
    }

    init {
        value = Resource(Status.LOADING)
    }
}