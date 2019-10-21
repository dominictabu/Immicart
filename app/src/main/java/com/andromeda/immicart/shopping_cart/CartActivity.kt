package com.andromeda.immicart.shopping_cart

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.andromeda.immicart.R
import com.andromeda.immicart.shopping_cart.temporary.PersonalCartFragment
import kotlinx.android.synthetic.main.activity_cart.*


class CartActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)
        setSupportActionBar(toolbar)

        toolbar_title.text = "Personal Cart"
        supportActionBar!!.setDisplayShowTitleEnabled(false);

        supportActionBar!!.setHomeButtonEnabled(true);
        supportActionBar!!.setDisplayHomeAsUpEnabled(true);

        val manager = supportFragmentManager
        val transaction = manager.beginTransaction()
        transaction.replace(R.id.content_cart_fragment, PersonalCartFragment()).commit()
    }
}
