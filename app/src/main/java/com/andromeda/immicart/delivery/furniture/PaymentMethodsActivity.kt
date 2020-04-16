package com.andromeda.immicart.delivery.furniture

import android.app.Activity
import android.content.ComponentName
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.andromeda.immicart.R
import com.pesapal.pesapalandroid.data.Payment
import kotlinx.android.synthetic.main.activity_payment_methods.*
import java.io.Serializable

data class PaymentMethod(val link: Int, val name: String) : Serializable
class PaymentMethodsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_methods)


        val items = ArrayList<PaymentMethod>()

        val item = PaymentMethod(R.drawable.mpesa, "MPESA")
        val item1 = PaymentMethod(R.drawable.mpesa, "PayPal")
        val item2 = PaymentMethod(R.drawable.mpesa, "PesaPal")

        items.add(item)
        items.add(item1)
        items.add(item2)
        payment_method_recycler?.let {
            it.layoutManager = GridLayoutManager(this, 2)
            it.adapter = PaymentMethodRecyclerAdapter(items, this, {method: PaymentMethod -> chosenMethod(method)})
        }

//        pesapal?.setOnClickListener {
//            makePayments()
//
//        }
    }

    fun chosenMethod(paymentMethod: PaymentMethod) {
        val intent = Intent()
        intent.putExtra(METHOD, paymentMethod)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    val METHOD = "METHOD"

    fun makePayments() {
        val payment = Payment()
        payment.reference = "p0909788786t9"//transaction unique reference
        payment.amount = 100.00
        payment.account = "just extra info, e.g dstv account number, order id"
        payment.description = "test_payment"
        payment.email = "dommietabu@gmail.com.com"
        payment.currency = "KES"
        payment.firstName = "Jane"
        payment.lastName = "Doe"
        payment.phoneNumber = "0796026997"
        val cn = ComponentName(this, "com.pesapal.pesapalandroid.PesapalPayActivity")

        val intent = Intent().setComponent(cn)

        intent.putExtra("payment", payment)

        startActivityForResult(intent, PAYMENT_ACTIVITY_REQUEST_CODE)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
//        val bundle: Bundle? = data?.getExtras();
//        if (bundle != null) {
//            for (key in bundle!!.keySet()) {
//                if (bundle.get(key) != null) {
//                    Log.e(TAG, key + " : " + bundle!!.get(key));
//
//                } else {
//                    Log.e(TAG, key + " : " + "NULL");
//
//                }
//            }
//        }

        if(requestCode == PAYMENT_ACTIVITY_REQUEST_CODE && data != null) {
            val payment : String = data.getStringExtra("payment")
            Log.d(TAG, "PAyment: $payment")

        }

    }

    val SETTINGS_ACTIVITY_REQUEST_CODE = 100
    val PAYMENT_ACTIVITY_REQUEST_CODE = 101
    val TAG = "PaymentMethodsActivity"
}
