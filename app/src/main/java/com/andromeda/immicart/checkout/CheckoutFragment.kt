package com.andromeda.immicart.checkout


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.androidstudy.daraja.Daraja
import com.androidstudy.daraja.DarajaListener
import com.androidstudy.daraja.model.AccessToken

import com.andromeda.immicart.R
import com.andromeda.immicart.mpesa.callback.DarajaException
import com.andromeda.immicart.mpesa.callback.DarajaPaymentListener
import com.andromeda.immicart.mpesa.model.PaymentResult
import com.andromeda.immicart.mpesa.utils.Environment
import dagger.Provides
import javax.inject.Inject
import javax.inject.Singleton

class CheckoutFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_checkout, container, false)
    }

    fun lipaNaMpesa(){
        @Provides
        @Singleton
                Daraja providesDaraja(){
                    return Daraja.Builder(Config.CONSUMER_KEY, Config.CONSUMER_SECRET)
                        .setBusinessShortCode(Config.BUSINESS_SHORTCODE)
                        .setPassKey(AppUtils.getPassKey())
                        .setTransactionType(Config.ACCOUNT_TYPE)
                        .setCallbackUrl(Config.CALLBACK_URL)
                        .setEnvironment(Environment.SANDBOX)
                        .build()
                }

    }

    @Inject
    lateinit var daraja : Daraja

    private fun getToken() {
        daraja.getAccessToken(object : DarajaListener<AccessToken> {
            override fun onResult(accessToken: AccessToken) {
                var token = accessToken.access_token

                makePaymentRequest(token)
            }

            override fun onError(exception: DarajaException) {

            }
        })
    }
    private fun makePaymentRequest(token : String) {
        var phoneNumber = ""
        var amount = "100"
        var accountReference = "2343de"
        var description = "Payment for airtime"

        daraja.initiatePayment(token, phoneNumber, amount, accountReference, description,
            object : DarajaPaymentListener {
                override fun onPaymentRequestComplete(result: PaymentResult) {
                    Toast.makeText(baseContext, result.CustomerMessage, Toast.LENGTH_LONG).show()
                }

                override fun onPaymentFailure(exception: DarajaException) {
                    Toast.makeText(baseContext, exception.message, Toast.LENGTH_LONG).show()
                }

                override fun onNetworkFailure(exception: DarajaException) {
                    //This is invoked if the error has to do with infrastructure 404 / no internet
                    Toast.makeText(baseContext, exception.message, Toast.LENGTH_LONG).show()
                }

            }
        )
    }

}
