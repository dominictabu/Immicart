package com.andromeda.immicart.delivery.checkout


import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.Toast
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.andromeda.immicart.R
import com.andromeda.immicart.checkout.CartImagesAdapter
import com.andromeda.immicart.checkout.DeliveryCartItemsAdapter
import com.andromeda.immicart.delivery.*
import com.andromeda.immicart.delivery.delivery_location.Place
import com.andromeda.immicart.delivery.persistence.CurrentLocation
import com.google.firebase.firestore.DocumentReference
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.libraries.places.internal.it
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_delivery_details.*
import java.text.DecimalFormat
import com.andromeda.immicart.delivery.choose_store.Store
import com.andromeda.immicart.delivery.delivery_location.PickDeliveryAddressActivity


import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap








class DeliveryDetailsFragment : Fragment() {

    private lateinit var viewModel: DeliveryCartViewModel
    private lateinit var auth: FirebaseAuth
    private lateinit var deliveryLocation: CurrentLocation
    private lateinit var currentStore: Store
    private var storeSubtotal: Float? = null
    private  var TAG: String = "DeliveryDetailsFragment"
    var cartItems: List<DeliveryCart> = ArrayList<DeliveryCart>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_delivery_details, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(activity!!).get(DeliveryCartViewModel::class.java)

        viewModel.allDeliveryLocations().observe(activity!!, androidx.lifecycle.Observer {

            it?.let {
                if(it.size > 0) {
                    val place = it[0]
                    deliveryLocation = place

                    address_one.text = place.name
                    address_two.text = place.address
                }

            }

        })

        auth = FirebaseAuth.getInstance()


        viewModel.allDeliveryItems().observe(this, androidx.lifecycle.Observer { items ->
            // Update the cached copy of the words in the adapter.
            items?.let {
                //                cartItems = items
//                badge.text = it.count().toString()
//                adapter.setCartItems(it)
                cartItems = items
                nuberOfItemsTitle.text = "${cartItems.size} Items"


//                adapter.submitList(it)

                val images : ArrayList<String> = ArrayList()
                var total = 0
                it.forEach {
                    val quantity = it.quantity
                    val unitPrice = it.offerPrice.toInt()
                    val subtotal = quantity * unitPrice
                    total += subtotal
                    images.add(it.image_url)
                }

                loadImages(images)

                storeSubtotal= total.toFloat()

                val formatter = DecimalFormat("#,###,###");
                val totalFormattedString = formatter.format(total);

                store_subtotal.setText("KES " + totalFormattedString)
            }
        })

        viewModel.currentStores().observe(activity!!, androidx.lifecycle.Observer {
            it?.let {
                if(it.size > 0) {
                    Log.d(TAG, "Stores size more than 0")
                     currentStore = it[0]

                    toolbar_title.text = "${currentStore.name}'s order"

                } else {
                    Log.d(TAG, "Stores size 0")

                }
            }
        })


        recycler_ll.visibility = View.VISIBLE
        cart_items_ll.visibility = View.GONE

        // Initialize Firebase Auth

        edit_location.setOnClickListener {
            startActivity(Intent(activity!!, PickDeliveryAddressActivity::class.java))
        }

        place_order_button.setOnClickListener {
//            signInAnonymously()
            checkOut()
        }


        expand_button.setOnClickListener {
            recycler_ll.visibility = View.GONE
            cart_items_ll.visibility = View.VISIBLE
            number_of_items_vertical_rv.text = "${cartItems.size} Items"
            loadItems()
        }

        collapse_button.setOnClickListener {
            recycler_ll.visibility = View.VISIBLE
            cart_items_ll.visibility = View.GONE
//            loadItems()
        }


    }


//    fun signInAnonymously() {
//
//        auth.signInAnonymously()
//            .addOnCompleteListener {
//
//                if (it.isSuccessful) {
//                    // Sign in success, update UI with the signed-in user's information
//                    Log.d(TAG, "signInAnonymously:success")
//                    val user = auth.currentUser
////                    updateUI(user)
//                    checkOut()
//                } else {
//                    // If sign in fails, display a message to the user.
//                    Log.w(TAG, "signInAnonymously:failure", it.exception)
//                    Toast.makeText(activity!!, "Authentication failed.",
//                        Toast.LENGTH_SHORT).show()
//                }
//
//                // ...
//            }
//    }


    fun loadImages(images: List<String>) {
        val linearLayoutManager = LinearLayoutManager(activity!!, RecyclerView.HORIZONTAL, false)
        cart_recycler_view.layoutManager = linearLayoutManager
        val cartImagesAdapter  = CartImagesAdapter(images,activity!!)
        cart_recycler_view.adapter = cartImagesAdapter
    }

    fun loadItems() {
        val linearLayoutManager = LinearLayoutManager(activity!!, RecyclerView.VERTICAL, false)

        cart_items_recycler.layoutManager = linearLayoutManager
        val deliveryCartItemsAdapter  = DeliveryCartItemsAdapter(cartItems,activity!!)
        cart_items_recycler.adapter = deliveryCartItemsAdapter
    }

    fun getDeliveryTime() : DeliveryTime? {


        if(segmented2.checkedRadioButtonId == -1) {
            //TODO The User has not selected the day of deliver
            return null

            //No Buttons Selected
        } else {


            if(delivery_time_radio_group.checkedRadioButtonId != -1) {

                val selectedTime = delivery_time_radio_group.checkedRadioButtonId
                val timeRadioButton: RadioButton = activity!!.findViewById(selectedTime)

                val time= timeRadioButton.text
                val dateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.ENGLISH)



                val selectedButton = segmented2.checkedRadioButtonId
                val radioButton: RadioButton = activity!!.findViewById(selectedButton)

                if (selectedButton == R.id.todayBtn) {
                    val cal = Calendar.getInstance()
                    cal.getTime()
                    val year = cal.get(Calendar.YEAR)
                    val month = cal.get(Calendar.MONTH)
                    val day = cal.get(Calendar.DAY_OF_MONTH)
                    val datePosted = dateFormat.format(cal.getTime())




                    val deliveryTime = DeliveryTime(time.toString(), getDayFromInt(day)!!, datePosted,getMonthFromInt(month)!!, year = year.toString())
                    return deliveryTime


                } else if (selectedButton == R.id.tomorrowBtn) {
                    val cal = Calendar.getInstance()
                    cal.getTime()
                    cal.add(Calendar.DATE, 1);  // number of days to add
                    val year = cal.get(Calendar.YEAR)
                    val month = cal.get(Calendar.MONTH)
                    val day = cal.get(Calendar.DAY_OF_MONTH)
                    val datePosted = dateFormat.format(cal.getTime())

                    val deliveryTime = DeliveryTime(time.toString(), getDayFromInt(day)!!, datePosted,getMonthFromInt(month)!!, year = year.toString())
                    return deliveryTime

                }
            } else {
                return null


                //TODO The User has not selected the delivery time

            }
        }
        return null

    }

    fun getDayFromInt(day: Int) : String? {

        when(day) {
            1 -> return "Monday"
            2 -> return "Tuesday"
            3 -> return "Wednesday"
            4 -> return "Thursday"
            5 -> return "Friday"
            6 -> return "Saturday"
            0 -> return "Sunday"

        }

        return null

    }

    fun getMonthFromInt(day: Int) : String? {
        when(day) {
            0 -> return "January"
            1 -> return "February"
            2 -> return "March"
            3 -> return "April"
            4 -> return "May"
            5 -> return "June"
            6 -> return "July"
            7 -> return "August"
            8 -> return "September"
            9 -> return "October"
            10 -> return "November"
            11 -> return "December"

        }

        return null

    }

    data class DeliveryTime(val timeOfDelivery: String, val day: String, val date: String, val month: String, val year: String)

    fun checkOut() {

        val collectionPath = "orders"
        var user = auth.currentUser?.uid;

//        var deliveryAddress: String = deliveryLocation

        var customerUID: String = auth.currentUser?.uid!!
        var storeFee: Float = 0f

        if (storeSubtotal != null) {
            storeFee = storeSubtotal!!
        }
        var serviceFee: Float = 50f
        var deliveryFee: Float = 100f
        var items: List<DeliveryCart> = cartItems


//        val rating = ratingBar.getRating()
        val date: Date // your date
// Choose time zone in which you want to interpret your Date

        val dateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.ENGLISH)
        val nestedDateData = HashMap<String, Any>()

        val cal = Calendar.getInstance()
        cal.getTime()
        val year = cal.get(Calendar.YEAR)
        val month = cal.get(Calendar.MONTH)
        val day = cal.get(Calendar.DAY_OF_MONTH)

        val datePosted = dateFormat.format(cal.getTime())
//
        nestedDateData.put("date", datePosted)
        nestedDateData.put("year", year)
        nestedDateData.put("month", month)
        nestedDateData.put("day", day)


        val orderMap = HashMap<String, Any>()
        val nestedDeliveryAddressData = HashMap<String, Any>()
        val nestedChargesData = HashMap<String, Any>()
        val nestedStoreData = HashMap<String, Any>()
        val nestedDeliveryTimeData = HashMap<String, Any>()

        val deliveryTime = getDeliveryTime()
        deliveryTime?.let {
            nestedDeliveryTimeData.put("date", it.date)
            nestedDeliveryTimeData.put("day", it.day)
            nestedDeliveryTimeData.put("month", it.month)
            nestedDeliveryTimeData.put("year", it.year)
        }


        deliveryLocation?.let {

        nestedDeliveryAddressData.put("Name", deliveryLocation.name!!)
        nestedDeliveryAddressData.put("Address", deliveryLocation.address!!)
        nestedDeliveryAddressData.put("PlaceFullText", deliveryLocation.placeFullText!!)
        nestedDeliveryAddressData.put("LatLng", deliveryLocation.latLng!!)
      }




        nestedChargesData.put("storeSubtotal", storeFee)
        nestedChargesData.put("serviceFee", serviceFee)
        nestedChargesData.put("deliveryFee", deliveryFee)

        nestedStoreData.put("key", currentStore.key)
        nestedStoreData.put("logoURL", currentStore.logoUrl)
        nestedStoreData.put("latLng", currentStore.latlng)
        nestedStoreData.put("name", currentStore.name)
        nestedStoreData.put("address", currentStore.address)




        orderMap.put("customerUID", customerUID)
        orderMap.put("mobileNumber", "0796026997")
        orderMap.put("items", items)
        orderMap.put("datePosted", datePosted)
        orderMap.put("deliveryTime", nestedDeliveryTimeData)
        orderMap.put("payments", nestedChargesData)
        orderMap.put("deliveryAddress", nestedDeliveryAddressData)
        orderMap.put("store", nestedStoreData)



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
