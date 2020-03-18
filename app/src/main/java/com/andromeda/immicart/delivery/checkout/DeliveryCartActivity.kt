package com.andromeda.immicart.delivery.checkout

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.andromeda.immicart.R

class DeliveryCartActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_delivery_cart)
//        setSupportActionBar(toolbar)

//        toolbar_title.text = "Personal Cart"
//        supportActionBar!!.setDisplayShowTitleEnabled(false);
//
//        supportActionBar!!.setHomeButtonEnabled(true);
//        supportActionBar!!.setDisplayHomeAsUpEnabled(true);

//        val navController = findNavController(R.id.delivery_cart_nav_host_fragment)
//
//        val config = AppBarConfiguration(
//            setOf(
//                R.id.personalCartFragment2,
//                R.id.deliveryCartFragment2
//        ))
//
//        toolbar.setupWithNavController(navController)



//        val manager = supportFragmentManager
//        val transaction = manager.beginTransaction()
//        transaction.replace(R.id.content_cart_fragment, DeliveryCartFragment()).commit()

    }



    fun performFragmnetTransaction(fragment: Fragment) {
        val manager = supportFragmentManager
        val transaction = manager.beginTransaction()
        transaction.replace(R.id.content_cart_fragment, fragment).commit()
    }

}
