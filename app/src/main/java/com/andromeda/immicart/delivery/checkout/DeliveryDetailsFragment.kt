package com.andromeda.immicart.delivery.checkout


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.andromeda.immicart.R
import com.andromeda.immicart.checkout.CartImagesAdapter
import com.andromeda.immicart.checkout.DeliveryCartItemsAdapter_
import com.andromeda.immicart.delivery.DeliveryCart
import com.andromeda.immicart.delivery.choose_store.Store
import com.andromeda.immicart.delivery.delivery_location.PickDeliveryAddressActivity
import com.andromeda.immicart.delivery.persistence.CurrentLocation
import com.andromeda.immicart.delivery.wallet.MPESAActivity
import com.firebase.geofire.GeoFire
import com.firebase.geofire.GeoLocation
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.type.LatLng
import kotlinx.android.synthetic.main.fragment_delivery_details.*
import kotlinx.android.synthetic.main.fragment_place_order.*
import kotlinx.android.synthetic.main.item_product.*
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern
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
//            checkOut()
            findNavController().navigate(R.id.action_deliveryDetailsFragment_to_loadingPlaceOrderFragment)
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

        top_up_button?.setOnClickListener {
            startActivity(Intent(activity!!, MPESAActivity::class.java))

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
        val deliveryCartItemsAdapter  = DeliveryCartItemsAdapter_(cartItems,activity!!)
        cart_items_recycler.adapter = deliveryCartItemsAdapter
    }

//    fun getDeliveryTime() : DeliveryTime? {
//
//
//        if(segmented2.checkedRadioButtonId == -1) {
//            //TODO The User has not selected the day of deliver
//            return null
//
//            //No Buttons Selected
//        } else {
//
//
//            if(delivery_time_radio_group.checkedRadioButtonId != -1) {
//
//                val selectedTime = delivery_time_radio_group.checkedRadioButtonId
//                val timeRadioButton: RadioButton = activity!!.findViewById(selectedTime)
//
//                val time= timeRadioButton.text
//                val dateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.ENGLISH)
//
//
//
//                val selectedButton = segmented2.checkedRadioButtonId
//                val radioButton: RadioButton = activity!!.findViewById(selectedButton)
//
//                if (selectedButton == R.id.todayBtn) {
//                    val cal = Calendar.getInstance()
//                    cal.getTime()
//                    val year = cal.get(Calendar.YEAR)
//                    val month = cal.get(Calendar.MONTH)
//                    val day = cal.get(Calendar.DAY_OF_MONTH)
//                    val datePosted = dateFormat.format(cal.getTime())
//
//
//
//
//                    val deliveryTime = DeliveryTime(time.toString(), getDayFromInt(day)!!, datePosted,getMonthFromInt(month)!!, year = year.toString())
//                    return deliveryTime
//
//
//                } else if (selectedButton == R.id.tomorrowBtn) {
//                    val cal = Calendar.getInstance()
//                    cal.getTime()
//                    cal.add(Calendar.DATE, 1);  // number of days to add
//                    val year = cal.get(Calendar.YEAR)
//                    val month = cal.get(Calendar.MONTH)
//                    val day = cal.get(Calendar.DAY_OF_MONTH)
//                    val datePosted = dateFormat.format(cal.getTime())
//
//                    val deliveryTime = DeliveryTime(time.toString(), getDayFromInt(day)!!, datePosted,getMonthFromInt(month)!!, year = year.toString())
//                    return deliveryTime
//
//                }
//            } else {
//                return null
//
//
//                //TODO The User has not selected the delivery time
//
//            }
//        }
//        return null
//
//    }

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

        val timeInMillis = System.currentTimeMillis();

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

//        val deliveryTime = getDeliveryTime()
//        deliveryTime?.let {
//            nestedDeliveryTimeData.put("date", it.date)
//            nestedDeliveryTimeData.put("day", it.day)
//            nestedDeliveryTimeData.put("month", it.month)
//            nestedDeliveryTimeData.put("year", it.year)
//        }

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
        orderMap.put("timeInMillisCreated", timeInMillis)
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

            val key = it.id

            currentStore?.let {
                val ref = FirebaseDatabase.getInstance().getReference("current_store_order_locations")
                val geoFire =  GeoFire(ref);
                val latLng = convertLatLng(it.latlng)
                val lat = latLng.latitude
                val lon = latLng.longitude

                geoFire.setLocation(key,  GeoLocation(lat, lon), object : GeoFire.CompletionListener {
                    override fun onComplete(key: String?, error: DatabaseError?) {

                    }

                })
            }

            deliveryLocation?.let {
                val ref = FirebaseDatabase.getInstance().getReference("customer_order_locations")
                val geoFire =  GeoFire(ref);

                val latLng = convertLatLng(it.latLng)
                val lat = latLng.latitude
                val lon = latLng.longitude

                geoFire.setLocation(key,  GeoLocation(lat, lon), object : GeoFire.CompletionListener {
                    override fun onComplete(key: String?, error: DatabaseError?) {

                    }

                })
            }

        }).addOnFailureListener {
            Log.d(TAG, "Exception: ${it.toString()}")
        }

    }

    fun convertLatLng(latlng : String) : LatLng {
        var from_lat_lng : String? = null
          val m : Matcher  = Pattern.compile("\\(([^)]+)\\)").matcher(latlng);
            while(m.find()) {
                from_lat_lng = m.group(1) ;
            }
            val gpsVal = (from_lat_lng?.split(","));
            val lat : Double = (gpsVal?.get(0))!!.toDouble();
            val lon : Double = (gpsVal?.get(1))!!.toDouble();

        val latLng = LatLng.newBuilder().setLatitude(lat).setLongitude(lon).build()

        return  latLng

    }

    internal fun snapshotListeners() {
        var uid = auth.currentUser?.uid;
        val db = FirebaseFirestore.getInstance()
        uid?.let {

            val docPath = "customers/$uid/wallet/data"
            val docRef = db.document(docPath)
            docRef.addSnapshotListener(EventListener<DocumentSnapshot> { snapshot, e ->
                if (e != null) {
                    Log.d(TAG, "Listen failed.", e)
                    return@EventListener
                }
                val source = if (snapshot != null && snapshot.metadata.hasPendingWrites())
                    "Local"
                else
                    "Server"
                if (snapshot != null && snapshot.exists()) {
                    Log.d(TAG, source + " data: " + snapshot.data)

                    val stringObjectMap = snapshot.data
                    if (stringObjectMap != null) {
                        if (stringObjectMap.containsKey("credit")) {
                            val credit = stringObjectMap["credit"] as Long
                            wallet_credit?.text = "KES ${credit.toInt()}"
                        }
                    }
                    //TODO Show Error or success dialog
                } else {
                    Log.d(TAG, "$source data: null")
                }
            })


        }
    }

    fun amountEnough(){


        var deliveryFee = delivery_fee.toString().toInt()
        val storeSubtotal = store_subtotal.toString().toInt()
        var serviceCharge = service_fee_amount.toString().toInt()


        when(storeSubtotal){
            in 0..500 -> delivery_fee.text = "KES 100"
            in 501..1000 -> delivery_fee.text = "KES 120"
            in 1001..2000 -> delivery_fee.text = "KES 150"
            in 2001..5000 -> delivery_fee.text = "KES 180"
            else  -> delivery_fee.text = "KES 200"
        }
//        Calculating service charge
        serviceCharge = 5*storeSubtotal/100
        service_fee_amount.text = "KES "+serviceCharge


        val mpesaAmount = wallet_cardview.toString().toInt()
        val amount = total_amount.toString().toInt()

        if (amount.toBigDecimal() >= mpesaAmount.toBigDecimal()){
            place_order_button_disabled.isGone
            place_order_button.isVisible
        }
    }

}

