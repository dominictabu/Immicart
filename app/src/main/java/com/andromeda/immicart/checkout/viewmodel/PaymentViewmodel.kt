package com.andromeda.immicart.checkout.viewmodel

import androidx.lifecycle.ViewModel
import com.andromeda.immicart.mpesa.model.AccessToken
import javax.inject.Inject

class PaymentViewModel @Inject
internal constructor(private val paymentRepository: PaymentRepository) : ViewModel() {

    fun initiatePayment(token : String, phone: String, amount: Int, description: String): DarajaPaymentLiveData {
        return paymentRepository.initiatePayment(token, phone, amount, description)
    }

    fun accessToken(): DarajaLiveData<AccessToken> {
        return paymentRepository.accessToken
    }

}