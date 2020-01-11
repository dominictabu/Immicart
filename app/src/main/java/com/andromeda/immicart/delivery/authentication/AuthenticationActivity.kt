package com.andromeda.immicart.delivery.authentication

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.andromeda.immicart.R
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import com.andromeda.immicart.delivery.ProductsPageActivity
import com.google.firebase.auth.FirebaseAuth


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
