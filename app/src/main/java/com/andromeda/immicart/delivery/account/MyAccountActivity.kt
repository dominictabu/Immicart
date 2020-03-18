package com.andromeda.immicart.delivery.account

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.andromeda.immicart.R
import kotlinx.android.synthetic.main.activity_my_account.*
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.andromeda.immicart.BuildConfig
import com.andromeda.immicart.delivery.authentication.AuthenticationActivity
import com.bumptech.glide.Glide
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.google.firebase.dynamiclinks.ShortDynamicLink
import com.google.firebase.firestore.FirebaseFirestore


class MyAccountActivity : AppCompatActivity() {

    private val TAG = "MyAccountActivity"
    var shareLink : String? = null
    private lateinit var viewModel: MyAccountViewModel




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_account)

        viewModel = ViewModelProviders.of(this).get(MyAccountViewModel::class.java)
        shareContent()
//        getAccountInfo()


        log_out_btn?.setOnClickListener {
            logOut()
        }

        closeBtn?.setOnClickListener {
            finish()
        }

        inivite_frds_ll?.setOnClickListener {
            shareApp()

        }

        val auth = FirebaseAuth.getInstance()

        val email = auth.currentUser?.email
        val displayName = auth.currentUser?.displayName
        val photoURL = auth.currentUser?.photoUrl
        val phoneNumber = auth.currentUser?.phoneNumber

        if(displayName != null) {
            user_name?.text = "Hi, ${displayName}"

        } else if (email != null) {
            user_name?.text = "Hi, ${email}"

        } else if (phoneNumber != null) {
            user_name?.text = "Hi, ${phoneNumber}"

        }
        Glide.with(this).load(photoURL).placeholder(R.drawable.ic_account_circle_green_24dp).into(profile_image)

    }

    fun shareApp() {

        viewModel.shareAppDynamicLink.observe(this, Observer {
            try {
                val shareIntent = Intent(Intent.ACTION_SEND)
                shareIntent.type = "text/plain"
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Immicart Shopping")
                var shareMessage = "\n Get the things you love from the stores you trust. Try Immicart now\n\n"
                shareMessage  = shareMessage + it
//                shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID + "\n\n"
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
                startActivity(Intent.createChooser(shareIntent, "Share Immicart Shopping app with: "))
            } catch (e: Exception) {
                //e.toString();
            }
        })
    }



    fun shareContent() {

        val link = "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID + "\n\n"

        Log.d(TAG, "shareContent method called")

        FirebaseDynamicLinks.getInstance().createDynamicLink()
            .setLink(Uri.parse(link))
            .setDomainUriPrefix("https://immicart.page.link")
            // Set parameters
            // ...
            .buildShortDynamicLink(ShortDynamicLink.Suffix.SHORT)
            .addOnSuccessListener { result ->
                // Short link created
                val shortLink = result.shortLink
                viewModel.setShareAppDynamicLink(shortLink.toString())
                Log.d(TAG, "shortLink : $shortLink")
                shareLink = shortLink.toString()
//                shareDeepLink(shortLink.toString())
                val flowchartLink = result.previewLink
            }.addOnFailureListener {
                // Error
                // ...
            }

    }


    fun getAccountInfo() {
        val db = FirebaseFirestore.getInstance();
        val userUID = FirebaseAuth.getInstance().uid
        val documentPath = "customers/$userUID"
        db.document(documentPath).get().addOnSuccessListener {
            val user = it.toObject(UserInfo::class.java)
            user?.let {
                user_name?.text = "Hi, ${user.name}"
                Glide.with(this).load(user.imageUrl).placeholder(R.drawable.ic_account_circle_green_24dp).into(profile_image)
            }



        }
    }






    fun logOut() {
        AuthUI.getInstance()
            .signOut(this)
            .addOnCompleteListener {
                if(it.isSuccessful) {
                    startActivity(Intent(this, AuthenticationActivity::class.java))
                    finish()

                }

    }


    }
}


data class UserInfo(
    var name: String? = "",
    var email: String? = "",
    var imageUrl: String? = ""
)
