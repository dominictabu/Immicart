package com.andromeda.immicart.delivery.authentication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.andromeda.immicart.R
import kotlinx.android.synthetic.main.activity_my_account.*
import android.content.Intent
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.OnCompleteListener
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import com.firebase.ui.auth.AuthUI

import com.google.firebase.auth.FirebaseAuth


class MyAccountActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_account)


        log_out_btn.setOnClickListener {
            logOut()
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
