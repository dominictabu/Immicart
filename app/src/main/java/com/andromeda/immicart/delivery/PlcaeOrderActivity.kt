package com.andromeda.immicart.delivery

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.andromeda.immicart.R
import com.andromeda.immicart.delivery.checkout.DeliveryDetailsFragment
import kotlinx.android.synthetic.main.activity_plcae_order.*

class PlcaeOrderActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_plcae_order)

        setSupportActionBar(place_order_toolbar)

        if (supportActionBar != null) {
            supportActionBar!!.setDisplayShowTitleEnabled(false);

            supportActionBar!!.setHomeButtonEnabled(true)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        }

        val manager = supportFragmentManager
        val transaction = manager.beginTransaction()
        transaction.replace(R.id.container, DeliveryDetailsFragment()).commit()


    }
}
