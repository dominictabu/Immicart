package com.andromeda.immicart.delivery.account

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.andromeda.immicart.R
import kotlinx.android.synthetic.main.activity_my_account.*
import android.content.Intent
import com.andromeda.immicart.delivery.authentication.AuthenticationActivity
import com.firebase.ui.auth.AuthUI


class MyAccountActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_account)


        log_out_btn.setOnClickListener {
            logOut()
        }

        closeBtn.setOnClickListener {
            finish()
        }
    }


    fun logOut() {
        AuthUI.getInstance()
            .signOut(this)
            .addOnCompleteListener {
                if(it.isSuccessful) {
                    startActivity(Intent(this, AuthenticationActivity::class.java))

            }

    }


    }
}
