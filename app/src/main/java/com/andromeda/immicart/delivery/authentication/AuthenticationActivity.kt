package com.andromeda.immicart.delivery.authentication

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.andromeda.immicart.R
import com.andromeda.immicart.delivery.ProductsPageActivity
import com.google.firebase.auth.FirebaseAuth
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper


class AuthenticationActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth


    protected override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authentication)

        auth = FirebaseAuth.getInstance()

    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser

        if(currentUser != null) {
            startActivity(Intent(this, ProductsPageActivity::class.java))
        }
    }

}
