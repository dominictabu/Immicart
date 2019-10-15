package com.andromeda.immicart.checkout.repo

import com.andromeda.immicart.checkout.common.DarajaLiveData
import com.andromeda.immicart.checkout.common.DarajaPaymentLiveData
import com.andromeda.immicart.checkout.util.AppUtils
import com.andromeda.immicart.mpesa.Daraja
import com.andromeda.immicart.mpesa.model.AccessToken
import javax.inject.Inject

class PaymentRepository @Inject
constructor() {

    @Inject
    lateinit var daraja : Daraja

    val accessToken: DarajaLiveData<AccessToken>
        get() {
            val accessTokenLiveData = DarajaLiveData<AccessToken>()
            daraja.getAccessToken(accessTokenLiveData)

            return accessTokenLiveData
        }

    fun initiatePayment(token : String, phoneNumber: String, amount: Int, description: String): DarajaPaymentLiveData {
        val listener = DarajaPaymentLiveData()

        daraja.initiatePayment(token, phoneNumber, amount.toString(), AppUtils.UUID(), description, listener)

        return listener
    }

}