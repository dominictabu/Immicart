package com.andromeda.immicart.wallet

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.androidstudy.daraja.Daraja
import com.androidstudy.daraja.DarajaListener
import com.androidstudy.daraja.model.AccessToken
import com.andromeda.immicart.R
import com.andromeda.immicart.checkout.CheckoutActivity
import com.andromeda.immicart.checkout.Constants


class PaymentDetailsFragment : Fragment() {
    lateinit var daraja: Daraja
    var phoneNumber: String = ""
    var paybillNumber = "174379" // The default
    val passKey = "bfb279f9aa9bdbcf158e97dd71a467cd2e0c893059b10f78e6b72ada1ed2c919"
    var amountPayable = "" //Fetch this information from the amount

    var partyA = ""
    var partyB = ""
    val callbackUrl = "" //Use our callback url

    var accountReference = ""
    var transactionDescription = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_payment_details, container, false)

        daraja = Daraja.with(
            Constants.IMMICART_CONSUMER_KEY,
            Constants.IMMICART_CONSUMER_SECRET,
            object : DarajaListener<AccessToken> {
                override fun onResult(accessToken: AccessToken) {
                }

                override fun onError(error: String) { //             TODO  Show error message
                }
            })


    }





}

