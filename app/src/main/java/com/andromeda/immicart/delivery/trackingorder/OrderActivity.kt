package com.andromeda.immicart.delivery.trackingorder

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import com.andromeda.immicart.R

class OrderActivity : AppCompatActivity() {

    private lateinit var ordersViewModel: OrdersViewModel
    private  var SELECTED_ORDER: String = "SELECTED_ORDER"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order)

        ordersViewModel = ViewModelProviders.of(this).get(OrdersViewModel::class.java)

        if(intent.hasExtra(SELECTED_ORDER)) {
            val orderID = intent.getStringExtra(SELECTED_ORDER)
            ordersViewModel.setSelectedOrderID(orderID)
        }


    }
}
