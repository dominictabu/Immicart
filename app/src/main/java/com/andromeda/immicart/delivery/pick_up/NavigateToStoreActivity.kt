package com.andromeda.immicart.delivery.pick_up

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.andromeda.immicart.R
import kotlinx.android.synthetic.main.activity_navigate_to_store.*

class NavigateToStoreActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_navigate_to_store)

        at_the_store?.setOnClickListener {
            startActivity(Intent(this, ShopperActivity::class.java))
        }

    }
}
