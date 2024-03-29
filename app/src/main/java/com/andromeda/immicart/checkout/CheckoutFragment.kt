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
//This won't be used for now!

    }


