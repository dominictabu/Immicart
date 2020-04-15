package com.andromeda.immicart.delivery.checkout

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.andromeda.immicart.R
import com.andromeda.immicart.delivery.choose_store.Store

class DeliveryCartActivity : AppCompatActivity() {

    private  val CURRENT_STORE = "CURRENT_STORE"
    private lateinit var deliveryCartViewModel: DeliveryCartViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_delivery_cart)
        deliveryCartViewModel = ViewModelProviders.of(this).get(DeliveryCartViewModel::class.java)
        if(intent.hasExtra(CURRENT_STORE)) {
            val store = intent.getSerializableExtra(CURRENT_STORE) as Store
            deliveryCartViewModel.setCurrentStore(store)

        }
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
