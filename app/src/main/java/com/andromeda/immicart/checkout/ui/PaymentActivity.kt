package com.andromeda.immicart.checkout.ui

import android.os.Bundle
import android.widget.Toast
import com.andromeda.immicart.R
import com.andromeda.immicart.checkout.common.BaseActivity
import com.andromeda.immicart.checkout.common.Status
import com.andromeda.immicart.checkout.util.AppUtils
import com.andromeda.immicart.checkout.viewmodel.PaymentViewModel
import kotlinx.android.synthetic.main.activity_payment.*
import kotlinx.android.synthetic.main.content_payment.*
import java.util.Observer

class PaymentActivity : BaseActivity() {

    private lateinit var viewModel: PaymentViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)
//        setSupportActionBar(toolbar)

        title = "Payment"

        viewModel = getViewModel(PaymentViewModel::class.java)

//        accessToken()
        bPay.setOnClickListener{pay()}

    }

    private fun pay() {
        val phoneNumber= etPhoneNumber.text.toString()
        val amountString = etAmount.text.toString()

        if (phoneNumber.isEmpty() && amountString.isEmpty()){
            toast("You have left some fields blank")
            return
        }

        val amount =amountString.toInt()
//        initiatePayment(phoneNumber, amount)
    }

//    private fun initiatePayment(phoneNumber: String, amount: Int) {
//        val token = AppUtils.getAccessToken(baseContext)
//        viewModel.initiatePayment(token, phoneNumber, amount, "Payment").observe(this, android.arch.lifecycle.Observer { response ->
//            when (response!!.status()) {
//                Status.LOADING -> {
//                    showLoading()
//                }
//
//                Status.SUCCESS -> {
//                    stopShowingLoading()
//                    val lnm = response.data()!!
//                    toast(lnm.ResponseDescription)
//                }
//
//                Status.ERROR -> {
//                    stopShowingLoading()
//                    toast(response.error()!!.message!!)
//                }
//            }
//
//        })
//    }

    private fun toast(text: String) {
        Toast.makeText(baseContext, text, Toast.LENGTH_LONG).show()
    }

//    private fun accessToken() {
//        viewModel.accessToken().observe(this, Observer { response ->
//            when (response!!.status()) {
//                Status.LOADING -> {
//                    showLoading()
//                }
//
//                Status.SUCCESS -> {
//                    stopShowingLoading()
//                    val token = response.data()!!
//                    AppUtils.saveAccessToken(baseContext, token.access_token)
//
//                    bPay.setOnClickListener{pay()}
//                }
//
//                Status.ERROR -> {
//                    stopShowingLoading()
//                    toast("error" + response.error()!!.message)
//
//                    bPay.setOnClickListener{
//                        accessToken()
//                    }
//                }
//            }
//
//        })
//    }




}
