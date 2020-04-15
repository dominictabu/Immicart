package com.andromeda.immicart.delivery.authentication


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.viewpager.widget.ViewPager
import com.andromeda.immicart.BuildConfig

import com.andromeda.immicart.R
import com.andromeda.immicart.delivery.CurrentStore
import com.andromeda.immicart.delivery.ProductsPageActivity
import com.andromeda.immicart.delivery.Utils.MyDatabaseUtil
import com.andromeda.immicart.delivery.furniture.FurnitureImage
import com.andromeda.immicart.delivery.furniture.SliderImage
import com.andromeda.immicart.delivery.furniture.TestFragmentAdapter
import com.firebase.ui.auth.AuthUI
import kotlinx.android.synthetic.main.fragment_firebase_uiauthentication.*
import com.firebase.ui.auth.AuthUI.IdpConfig
import com.firebase.ui.auth.ErrorCodes
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
//import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseUserMetadata
import com.pixelcan.inkpageindicator.InkPageIndicator


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [FirebaseUIAuthenticationFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class FirebaseUIAuthenticationFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        auth = FirebaseAuth.getInstance()

    }

    fun getCurrentStore() {
        val userUID = FirebaseAuth.getInstance().uid
        val ref = MyDatabaseUtil.getDatabase().reference.child("customers/$userUID/current_store")

        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(p0: DataSnapshot) {
                val store = p0.getValue(CurrentStore::class.java)

                if(store!= null) {
                    startActivity(Intent(context, ProductsPageActivity::class.java))
                } else {
                    startActivity(Intent(context, SelectStoreActivity::class.java))
//                    findNavController().navigate(R.id.action_firebaseUIAuthenticationFragment_to_selectStoreFragment)

                }


            }

        })

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        var imagesList = ArrayList<SliderImage>()


        val images = SliderImage(R.mipmap.shop_1)
        val images1 = SliderImage(R.mipmap.shop_2)
        val images2 = SliderImage(R.mipmap.shop_3)
//        val images3 = FurnitureImage(4,R.drawable.sofa_3, false)
//        val images4 = FurnitureImage(6,R.drawable.sofa_4, false)
//        val images5 = FurnitureImage(7,R.drawable.sofa_5, false)
//        val images6 = FurnitureImage(8,R.drawable.sofa_6, false)
//        val images7 = FurnitureImage(9,R.drawable.sofa_7, false)

        imagesList.add(images)
        imagesList.add(images1)
        imagesList.add(images2)
//        imagesList.add(images3)
//        imagesList.add(images4)
//        imagesList.add(images5)
//        imagesList.add(images6)
//        imagesList.add(images7)

        val mAdapter = SliderImageFragmentAdapter(activity!!.supportFragmentManager, imagesList)

        pager?.setAdapter(mAdapter)

//        val mIndicator = findViewById<InkPageIndicator>(R.id.indicator)
        mIndicator.setViewPager(pager)

        pager?.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                Log.d(TAG, "Position: $position")
                val banner = imagesList.get(position)
                Log.d(TAG, " $banner")

            }

            override fun onPageSelected(position: Int) {
                // Check if this is the page you want.
            }
        })


        signup?.setOnClickListener {
            startSignIn()
        }
    }

    private lateinit var mIndicator : InkPageIndicator
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_firebase_uiauthentication, container, false)

        mIndicator = view.findViewById<InkPageIndicator>(R.id.indicator)

        return view
    }

    fun createProfile() {
        val db = FirebaseFirestore.getInstance();
        val userUID = FirebaseAuth.getInstance().uid

        val documentPath = "customers/$userUID"

        val email = auth.currentUser?.email
        val displayName = auth.currentUser?.displayName
        val photoURL = auth.currentUser?.photoUrl
        val phoneNumber = auth.currentUser?.phoneNumber

//        val name =

        val user = HashMap<String, Any>()
        email?.let {
            user.put("email", email)
        }
        displayName?.let {
            user.put("name", displayName)
        }
        photoURL?.let {
            user.put("imageUrl", photoURL)
        }
        phoneNumber?.let {
            user.put("phoneNumber", phoneNumber)
        }

        db.document(documentPath).set(user).addOnCompleteListener {

            if (it.isSuccessful) {
                Log.d(TAG, "Task isSuccessful")
                activity?.let {
                    startActivity(Intent(context, SelectStoreActivity::class.java))

//                it.finish()
//                startActivity(Intent(activity, SelectStoreFragment::class.java))
                }
            } else {
                Log.d(TAG, "Task is Unsuccessful")

            }

        }
    }

    private fun startSignIn() {
        val phoneConfigWithDefaultNumber = IdpConfig.PhoneBuilder()
            .setDefaultCountryIso("ke")
            .build()
        // Build FirebaseUI sign in intent. For documentation on this operation and all
        // possible customization see: https://github.com/firebase/firebaseui-android
        val intent = AuthUI.getInstance().createSignInIntentBuilder()
            .setIsSmartLockEnabled(!BuildConfig.DEBUG)
            .setAvailableProviders(listOf(AuthUI.IdpConfig.EmailBuilder().build(),
                AuthUI.IdpConfig.GoogleBuilder().build(),
                phoneConfigWithDefaultNumber
            ))
            .setLogo(R.drawable.immicart_shopper_logo)
            .setTheme(R.style.GreenTheme)
            .build()

        startActivityForResult(intent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
            val bundle : Bundle? = data?.getExtras();
            if (bundle != null) {
                for ( key in bundle!!.keySet()) {
                    if(bundle.get(key) != null) {
                        Log.e(TAG, key + " : " +  bundle!!.get(key));

                    } else {
                        Log.e(TAG, key + " : " +  "NULL");

                    }
                }
}
        if (requestCode == RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)

            if (resultCode == Activity.RESULT_OK) {
                // Sign in succeeded
                Log.d(TAG, "Data: $data")
                data?.let {
                  val metadata = auth.getCurrentUser()?.getMetadata();
                    if (metadata?.getCreationTimestamp() == metadata?.getLastSignInTimestamp()) {
                        // The user is new, show them a fancy intro screen!
                        createProfile()
                    } else {
                        // This is an existing user, show them a welcome back screen.
                        startActivity(Intent(context, SelectStoreActivity::class.java))

                    }
                }
            } else {
                // Sign in failed
                if (response == null) {
                    // User pressed back button
                    return;
                }

                if (response.getError()?.getErrorCode() == ErrorCodes.NO_NETWORK) {
                    return;
                }

                Log.e(TAG, "Sign-in error: ", response.getError());
                // Sign in failed
                Toast.makeText(activity, "Sign In Failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        private const val RC_SIGN_IN = 9001
        private const val TAG = "FirebaseUIAuthFrag"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FirebaseUIAuthenticationFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FirebaseUIAuthenticationFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

}
