package com.andromeda.immicart.delivery

import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity;
import com.andromeda.immicart.R

import kotlinx.android.synthetic.main.activity_delivery_cart.*

class DeliveryCartActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_delivery_cart)
        setSupportActionBar(toolbar)

        toolbar_title.text = "Personal Cart"
        supportActionBar!!.setDisplayShowTitleEnabled(false);

        supportActionBar!!.setHomeButtonEnabled(true);
        supportActionBar!!.setDisplayHomeAsUpEnabled(true);

        val manager = supportFragmentManager
        val transaction = manager.beginTransaction()
        transaction.replace(R.id.content_cart_fragment, DeliveryCartFragment()).commit()

    }

}
