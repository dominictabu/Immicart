package com.andromeda.immicart.delivery


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProviders

import com.andromeda.immicart.R
import com.google.firebase.firestore.DocumentReference
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.libraries.places.internal.db
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_plcae_order.*
import kotlinx.android.synthetic.main.fragment_delivery_details.*
import java.text.DecimalFormat

import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap


class DeliveryDetailsFragment : Fragment() {

    private lateinit var viewModel: DeliveryDetailViewModel
    private lateinit var auth: FirebaseAuth
    private lateinit var deliveryLocation: Place
    private var storeSubtotal: Float? = null
    private  var TAG: String = "DeliveryDetailsFragment"
    lateinit var cartItems: List<DeliveryCart>


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_delivery_details, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(activity!!).get(DeliveryDetailViewModel::class.java)

        viewModel.allDeliveryLocations().observe(this, androidx.lifecycle.Observer {

            it?.let {
                if(it.size > 0) {
                    val place = it[0]
                    deliveryLocation = place

                    address_one.text = place.name
                    address_two.text = place.address
                }

            }

        })

        viewModel.allDeliveryItems().observe(this, androidx.lifecycle.Observer { items ->
            // Update the cached copy of the words in the adapter.
            items?.let {
                //                cartItems = items
//                badge.text = it.count().toString()
//                adapter.setCartItems(it)
                cartItems = items
//                adapter.submitList(it)

                var total = 0
                it.forEach {
                    val quantity = it.quantity
                    val unitPrice = it.offerPrice.toInt()
                    val subtotal = quantity * unitPrice
                    total += subtotal
                }

                storeSubtotal= total.toFloat()

                val formatter = DecimalFormat("#,###,###");
                val totalFormattedString = formatter.format(total);

                store_subtotal.setText("KES " + totalFormattedString)
            }
        })

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        (activity!! as PlcaeOrderActivity).place_order_button.setOnClickListener {
            signInAnonymously()
        }


    }


    fun signInAnonymously() {

        auth.signInAnonymously()
            .addOnCompleteListener {

                if (it.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInAnonymously:success")
                    val user = auth.currentUser
//                    updateUI(user)
                    checkOut()
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInAnonymously:failure", it.exception)
                    Toast.makeText(activity!!, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                }

                // ...
            }
    }


    fun checkOut() {

        val collectionPath =  "orders"
        var user = auth.currentUser?.uid;

//        var deliveryAddress: String = deliveryLocation

        var customerUID: String =  auth.currentUser?.uid!!
        var storeFee: Float = 0f

        if(storeSubtotal != null) {
            storeFee = storeSubtotal!!
        }
        var serviceFee: Float = 50f
        var deliveryFee: Float = 100f
        var items: List<DeliveryCart> = cartItems

        val store : Store = Store(1, "Tuskys T-Mall", "T-Mall Langata Road", "latLng(25.00, 0.5)", "data:image/jpeg;base64,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6vn1XEUsPCWMfU134N4wNn1Ur29J8KyVKStjAKnUvHhy8SJz9nbr0bdj2bVRS7QVBQ15pBwcggYzwODjOMgTsW9BaahEVUQDCqoCqB0AHAT0mg/G2QlLhyaTLvCi0sn5+322YlHaVahRBCa10hs8C4BOCea5M+97t0amznpIX7U1NWNCMMYOAOOck8eA5Ylz7wbtW98qi4TJUnSynSwzzGR4cOU6nZ93ScnAxx4nljOevtnQx/5BGMYdM/KS/XJSeiy2UJsjdO4uLVby1OtlqMHpghXQpgqVOe9kcfA++WvsXaV3V2ZVr107OsKVTs1wytlFPfYHiCSM46AdZ1NibBt7IOtvT0ByC/eZskcB6ROBOnKWv2xG94UcpPKb7/Ylp0251yU9t7cs3G1a1valVCWyO+fRD6dIU45FiBx9pM4e1txb21ovXqogpoMsRUVsDOOXxEub/gKC9N6j1EdkC1UUqEqheA7QYyTy+Qm9tGxSvSalUzpbGcHB4EEYI5cRLa2/uSgl1j0z06ryRPRpp57lSeT7cYXYNxchhQ/6IDAF2DYJPMhRgj2ky4EdQRTBUELkLkDCjhnHgJp7F2SlpT7KmzsmosA5DY1HJwQB4knjPu/2RQuCprUadQr6JdQxA6Anw9k1uv18dVf8cnuLtgnpp4cencpbfmu17tYqgOGanStzg4Zc6Qw6rqLcROvceSa4DEJcUmTTnUwdTnppGfnmWJtLdW2uK9Gu6stSkoFMoxTAU5TGnlpPETtAS9bt1QhCNHRLo00RLSZbcyufI7sVBQa+IzUZmpp/KgxnHtJ8egkm362cteyrK66gKbFAMahVyvZlc8OfA+xjOnsbY9KzRqdAFaZYtpLFgpPPTnkJsXdstVNDZxlTwODlWDjj71EoXbRUtar0+i7fYmjTircKV2f5N7yqWUmkjJV0VQzEle6r6u6CGGG8J4DcK5N2LYYKGqydr6KkIqs5APHgG+MvFbZRUaqAdbKFbicEKSRw5Z7x4858Ns+mWRsHK1WqLgn0mUqc9QQSMcpsv+wveee2OnT5kP4JdD0srZKNNKVMaURQqj2D857TEzOXsnKcnKTLySSwhERPBkREQBERAEREAREQBMTMQBERAMTMRAEREAxMxEAREQDEzEQBERAEREAREQBERAEREAREQBERAEREAREQBERAEREAREQBERAEREAREQBERAEREAREQBERAEREAREQBERAEREAREQBERAEREAREQBERAEREAREQBERAEREAREQBERAEREAREQBERAEREAREQBERAEREAREQBERAEREAREQBERAEREAREQBERAEREAREQDEREyBERAEREAREQBERAEREAREQBERAEREAREQBERAEREAREQBERAEREA//9k=")

//        val rating = ratingBar.getRating()

        val dateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.ENGLISH)
        val cal = Calendar.getInstance()

        val datePosted = dateFormat.format(cal.getTime())
//

        val orderMap = HashMap<String, Any>()
        orderMap.put("deliveryAddress", deliveryLocation)
        orderMap.put("customerUID", customerUID)
        orderMap.put("storeFee", storeFee)
        orderMap.put("serviceFee", serviceFee)
        orderMap.put("deliveryFee", deliveryFee)
        orderMap.put("items", items)
        orderMap.put("datePosted", datePosted)
        orderMap.put("store", store)

        val db = FirebaseFirestore.getInstance();
//
        db.collection(collectionPath).add(orderMap).addOnSuccessListener(OnSuccessListener<DocumentReference> {
            Toast.makeText(activity!!, "Successfully Placed order.",
                Toast.LENGTH_LONG).show()
            Log.d(TAG, "Success DocumentReference: ${it.toString()}")

        }).addOnFailureListener {
            Log.d(TAG, "Exception: ${it.toString()}")
        }


    }

}
