package com.andromeda.immicart.delivery.checkout


import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.andromeda.immicart.R
import com.andromeda.immicart.checkout.CartImagesAdapter
import com.andromeda.immicart.checkout.DeliveryCartItemsAdapter
import com.andromeda.immicart.checkout.DeliveryCartItemsAdapter_
import com.andromeda.immicart.delivery.*
import com.andromeda.immicart.delivery.Utils.MyDatabaseUtil
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
import com.andromeda.immicart.delivery.persistence.DeliveryLocation
import com.andromeda.immicart.delivery.wallet.MPESAActivity
import com.andromeda.immicart.networking.ImmicartAPIService
import com.andromeda.immicart.networking.Model
import com.bumptech.glide.Glide
import com.firebase.geofire.GeoFire
import com.firebase.geofire.GeoLocation
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.database.*
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.maps.android.SphericalUtil
import io.github.douglasjunior.androidSimpleTooltip.SimpleTooltip
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern
import kotlin.collections.ArrayList
import kotlin.collections.HashMap




class DeliveryDetailsFragment : Fragment() {

    private lateinit var viewModel: DeliveryCartViewModel
    private lateinit var auth: FirebaseAuth
    private  var deliveryLocation: DeliveryLocation? = null
    private  var currentStore: CurrentStore? = null
    private var storeSubtotal: Int? = 0
    private var serviceFee: Int? = null
    private var deliveryFee: Int? = 100
    private var totals: Int? = null
    private var credits: Int = 0
    private var phoneNumber: String? = null
    private var deliveryTime: String? = null
    private  var TAG: String = "DeliveryDetailsFragment"
    var cartItems: List<DeliveryCart> = ArrayList<DeliveryCart>()

    var creditsEnough : Boolean = false


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_delivery_details, container, false)

        return view
    }

    fun getCurrentStore() {
        val userUID = FirebaseAuth.getInstance().uid
        val ref =  MyDatabaseUtil.getDatabase().reference.child("customers/$userUID/current_store")

        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(p0: DataSnapshot) {
                val store = p0.getValue(CurrentStore::class.java)
                Log.d(TAG, "Store $store")
                currentStore = store

                store?.let {
                    toolbar_title?.text = "${store?.storeName}'s order"
                    store_subtotal_title?.text = "${store.storeName}'s subtotal"
                    getDeliveryCharges()

                }
            }
        })
    }

    fun validatePhoneNumber(phone : String) : Boolean {
        return android.util.Patterns.PHONE.matcher(phone).matches() && ((phone.startsWith("07") && phone.length == 10) || (phone.startsWith("2547") && phone.length == 12) ||  (phone.startsWith("+2547") && phone.length == 13));
    }

    val PREF_NAME = "IS_PICKUP"
    val keyChannel = "IS_PICKUP"
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(activity!!).get(DeliveryCartViewModel::class.java)

        getCurrentStore()

//        viewModel.currentStores().observe(activity!!, androidx.lifecycle.Observer {
//            it?.let {
//                if(it.size > 0) {
//                    Log.d(TAG, "Stores size more than 0")
//                    currentStore = it[0]
//                    toolbar_title.text = "${currentStore?.name}'s order"
//                } else {
//                    Log.d(TAG, "Stores size 0")
//
//                }
//            }
//        })

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

                storeSubtotal= total
                if(storeSubtotal!! <= 500) {
                    tipAmount = 50
                } else {
                    tipAmount = 100

                }

                val formatter = DecimalFormat("#,###,###");
                val totalFormattedString = formatter.format(total);

                store_subtotal?.setText("KES " + totalFormattedString)
                totalWithoutDeliveryLoc()

//                when(storeSubtotal){
//                    in 0..500 -> deliveryFee = 100
//                    in 501..1000 -> deliveryFee = 120
//                    in 1001..2000 -> deliveryFee = 150
//                    in 2001..5000 -> deliveryFee = 180
//                    else  -> deliveryFee = 200
//                }
//                delivery_fee?.text = "KES $deliveryFee"
//
//                val serviceCharge = 0.05* storeSubtotal!!
//                serviceFee = serviceCharge.toInt()
//                service_fee_amount.text = "KES "+ serviceCharge.toInt()
//
//                val totalCost = storeSubtotal!! + serviceFee!! + deliveryFee!!
//                totals = totalCost
//                val totalCostFormattedString = formatter.format(totalCost);
//                total_text_view?.text = "KES $totalCostFormattedString"
            }
        })

        getCurrentDeliveryLocation()
//        viewModel.allDeliveryLocations().observe(activity!!, androidx.lifecycle.Observer {
//
//            it?.let {
//                if(it.size > 0) {
//                    val place = it[0]
//                    deliveryLocation = place
//                    getDeliveryCharges()
//
//                    address_one.text = place.name
//                    address_two.text = place.address
//                }
//            }
//        })



        recycler_ll?.visibility = View.VISIBLE
        cart_items_ll?.visibility = View.GONE


        val shared = activity!!.getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        val channel = (shared.getBoolean(keyChannel, false));
        val radioBtnPickUp =  activity!!.findViewById<RadioButton>(R.id.button_pick_up);
        val radioBtnDelivery =  activity!!.findViewById<RadioButton>(R.id.button_delivery);
        if(channel) {
            delivery_time_textView?.text = "Choose your Pick up time"
            radioBtnPickUp?.isChecked = true
            discloser?.visibility = View.GONE
        } else {
            delivery_time_textView?.text = "Choose your delivery time"
            radioBtnPickUp?.isChecked = true
            discloser?.visibility = View.VISIBLE
        }

        segmented1?.setOnCheckedChangeListener(object: RadioGroup.OnCheckedChangeListener {
            override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {

                if(checkedId == R.id.button_delivery) {
                    delivery_time_textView?.text = "Choose your delivery time"
                    radioBtnDelivery?.isChecked = true
                    discloser?.visibility = View.VISIBLE
                    tip_title?.text = "Shopper Tip"

                } else {
                    delivery_time_textView?.text = "Choose your Pick up time"
                    radioBtnPickUp?.isChecked = true
                    discloser?.visibility = View.GONE
                    tip_title?.text = "Shopper Tip"

                }
            }
        })

        // Initialize Firebase Auth

        change_delivery_location?.setOnClickListener {
            startActivity(Intent(activity!!, PickDeliveryAddressActivity::class.java))
        }

        add_delivery_address_btn?.setOnClickListener {
            startActivity(Intent(activity!!, PickDeliveryAddressActivity::class.java))
        }

        place_order_button?.setOnClickListener {
//            findNavController().navigate(R.id.action_deliveryDetailsFragment_to_loadingPlaceOrderFragment)
            validateEntries()
        }

        expand_button?.setOnClickListener {
            recycler_ll.visibility = View.GONE
            cart_items_ll.visibility = View.VISIBLE
            number_of_items_vertical_rv.text = "${cartItems.size} Items"
            loadItems()
        }

        collapse_button?.setOnClickListener {
            recycler_ll.visibility = View.VISIBLE
            cart_items_ll.visibility = View.GONE
//            loadItems()
        }

        top_up_button?.setOnClickListener {
            startActivity(Intent(activity!!, MPESAActivity::class.java))
        }

        change_driver_tip?.setOnClickListener {
            startActivityForResult(Intent(activity!!, DriversTipActivity::class.java), TIP_REQUEST_CODE)

        }

        if (segmented2?.checkedRadioButtonId == R.id.button_deliver_today) {
            getDeliveryTimes()
        }

        val rightNow = Calendar.getInstance();
        val currentHourIn24Format = rightNow.get(Calendar.HOUR_OF_DAY);
        val radioBtn =  activity!!.findViewById<RadioButton>(R.id.button_deliver_today);
        val radioBtn2 =  activity!!.findViewById<RadioButton>(R.id.button_deliver_tomorrow);

        if(currentHourIn24Format > 20 ) {
            radioBtn.isEnabled = false
            radioBtn2.isChecked = true
            populateRadioButtons(R.array.from_9am)

        } else {
            radioBtn.isEnabled = true
            radioBtn.isChecked = true
            getDeliveryTimes()

        }
        segmented2?.setOnCheckedChangeListener(object: RadioGroup.OnCheckedChangeListener {
            override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {

                if(checkedId == R.id.button_deliver_tomorrow) {
                    populateRadioButtons(R.array.from_9am)
                } else {
                    getDeliveryTimes()
                }
            }
        })

        snapshotListeners()

        info_button?.setOnClickListener {
              SimpleTooltip.Builder(activity)
                    .anchorView(it)
                    .text("This fee helps support the Immicart platform and covers a broad range of operating costs including background checks, and customer support")
                    .gravity(Gravity.TOP)
                    .animated(false)
                    .transparentOverlay(true)
                    .build()
                    .show();
        }
    }

    fun totalWithoutDeliveryLoc() {

        delivery_fee?.text = "KES $deliveryFee"
        tip_amount?.text = "KES $tipAmount"


        val serviceCharge = 0.05* storeSubtotal!!
        serviceFee = serviceCharge.toInt()
        service_fee_amount.text = "KES "+ serviceCharge.toInt()
        val formatter = DecimalFormat("#,###,###");

        val totalCost = storeSubtotal!! + serviceFee!! + deliveryFee!! + tipAmount
        Log.d(TAG, "totalCost : $totalCost")
        val totalCostFormattedString = formatter.format(totalCost);
        total_text_view?.text = "KES $totalCostFormattedString"
        totals = totalCost
    }

    fun getDeliveryCharges() {
        Log.d(TAG, "getCurrentLocation called")
        deliveryLocation?.let {
            currentStore?.let {

                val storeLatLng = convertLatLng(currentStore!!.storeLatLng!!)
                Log.d(TAG, "Delivery storeLatLng $storeLatLng")

                val deliveryLatLng = convertLatLng(deliveryLocation!!.latLng!!)
                Log.d(TAG, "Delivery deliveryLatLng $deliveryLatLng")

                val distance = SphericalUtil.computeDistanceBetween(storeLatLng, deliveryLatLng)
                Log.d(TAG, "Delivery distance $distance")

                val distanceInKm = distance/1000
                deliveryFee = null
                if (distance < 1000) {
                    deliveryFee = 50 + (storeSubtotal!!*0.02).toInt()

                } else if (distance < 2000) {
                    deliveryFee = 100 + (storeSubtotal!!*0.02).toInt()

                } else if (distance < 3000) {
                    deliveryFee = 150 + (storeSubtotal!!*0.02).toInt()

                } else if (distance < 4000) {
                    deliveryFee = 200 + (storeSubtotal!!*0.02).toInt()

                } else if (distance < 5000) {
                    deliveryFee = 250 + (storeSubtotal!!*0.02).toInt()


                } else if (distance < 6000) {
                    deliveryFee = 300 + (storeSubtotal!!*0.02).toInt()


                } else if (distance < 7000) {
                    deliveryFee = 350 + (storeSubtotal!!*0.02).toInt()


                } else if (distance < 8000) {
                    deliveryFee = 400 + (storeSubtotal!!*0.02).toInt()


                } else if (distance < 9000) {
                    deliveryFee = 450 + (storeSubtotal!!*0.02).toInt()

                } else {
                    deliveryFee = 500 + (storeSubtotal!!*0.02).toInt()

                }

                tip_amount?.text = "KES $tipAmount"


                delivery_fee?.text = "KES $deliveryFee"

                val serviceCharge = 0.05* storeSubtotal!!
                serviceFee = serviceCharge.toInt()
                service_fee_amount.text = "KES "+ serviceCharge.toInt()
                val formatter = DecimalFormat("#,###,###");

                val totalCost = storeSubtotal!! + serviceFee!! + deliveryFee!! + tipAmount
                Log.d(TAG, "totalCost : $totalCost")
                val totalCostFormattedString = formatter.format(totalCost);
                total_text_view?.text = "KES $totalCostFormattedString"
                totals = totalCost

            }
        }

    }

    fun validateEntries() {

        Log.d(TAG, "validateEntries called")
        Log.d(TAG, "Totals $totals")
        Log.d(TAG, "Credits $credits")

        val phone = phone_number_edittext?.text.toString()
        if(deliveryLocation == null) {
            nested_scrolling_view?.post ( object : Runnable {
                override fun run() {
                    Log.d(TAG, "Runnable called : deliveryLocation  null")
                    nested_scrolling_view?.scrollTo(0, layoutLocation.top)
                    delivery_address_error?.visibility = View.VISIBLE
                }
            })

        } else if (TextUtils.isEmpty(phone)) {
            nested_scrolling_view?.post ( object : Runnable {
                override fun run() {
                    Log.d(TAG, "Runnable called : phone not entered")
                    nested_scrolling_view?.scrollTo(0, phone_cardview.top)
                    phone_error?.visibility = View.VISIBLE
                    delivery_address_error?.visibility = View.GONE

                }
            })

        }  else if (!validatePhoneNumber(phone)) {
            nested_scrolling_view?.post ( object : Runnable {
                override fun run() {
                    Log.d(TAG, "Runnable called : phone not entered")
                    nested_scrolling_view?.scrollTo(0, phone_cardview.top)
                    phone_error?.visibility = View.VISIBLE
                    delivery_address_error?.visibility = View.GONE
                    phone_error?.text = "Phone number isn't correct"
                }
            })
        }
        else if (deliveryTime == null) {
            nested_scrolling_view?.post ( object : Runnable {
                override fun run() {
                    Log.d(TAG, "Runnable called : delivery time not set")
                    nested_scrolling_view?.scrollTo(0, delivery_time_cardview.top)
                    delivery_time_error?.visibility = View.VISIBLE
                    phone_error?.visibility = View.GONE

                }
            })

        } else if ((totals?.toBigDecimal()!! < credits?.toBigDecimal()!!)) {
        nested_scrolling_view?.post ( object : Runnable {
            override fun run() {
                Log.d(TAG, "Runnable called : credits insufficient")
                nested_scrolling_view?.scrollTo(0, wallet_cardview.top)
                wallet_error?.visibility = View.VISIBLE
            }
        })


    } else {
            getDeliveryDetails()

        }

    }


    fun getDeliveryDetails() {
        val radioGrp =  activity!!.findViewById<RadioGroup>(R.id.delivery_time_radio_group);

        val rightNow = Calendar.getInstance();
        var currentDayOfTheYear = rightNow.get(Calendar.DAY_OF_YEAR);
        var currentYear = rightNow.get(Calendar.YEAR);
        Log.d(TAG, "Year $currentYear")

        val phone = phone_number_edittext.text.toString()
        if (segmented2?.checkedRadioButtonId == R.id.button_deliver_tomorrow) {
            currentDayOfTheYear++
        }
        val radioButton = activity!!.findViewById<RadioButton>(radioGrp.checkedRadioButtonId)
        val timeOfDelivery = radioButton.text.toString()
        val deliverDetails = DeliveryDetails(phone, currentYear, currentDayOfTheYear, timeOfDelivery, storeSubtotal!!, deliveryFee!!, serviceFee!!, totals!!)
        viewModel.setDeliveryDetails(deliverDetails)
        findNavController().navigate(R.id.action_deliveryDetailsFragment_to_loadingPlaceOrderFragment)
    }

    fun amountEnough() : Int {


        var deliveryFee : Int

        when(storeSubtotal){
            in 0..500 -> deliveryFee = 100
            in 501..1000 -> deliveryFee = 120
            in 1001..2000 -> deliveryFee = 150
            in 2001..5000 -> deliveryFee = 180
            else  -> deliveryFee = 200
        }
//        Calculating service charge
        val serviceCharge = (0.05 * storeSubtotal!!).toInt()
        val total = deliveryFee + serviceCharge + storeSubtotal!!
        val formatter = DecimalFormat("#,###,###");
        val totalFormattedString = formatter.format(total);
        total_text_view?.setText("KES " + totalFormattedString)
        return total
    }

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
//            storeFee = storeSubtotal!!
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

//        deliveryLocation?.let {
//
//        nestedDeliveryAddressData.put("Name", deliveryLocation.name!!)
//        nestedDeliveryAddressData.put("Address", deliveryLocation.address!!)
//        nestedDeliveryAddressData.put("PlaceFullText", deliveryLocation.placeFullText!!)
//        nestedDeliveryAddressData.put("LatLng", deliveryLocation.latLng!!)
//      }

        nestedChargesData.put("storeSubtotal", storeFee)
        nestedChargesData.put("serviceFee", serviceFee)
        nestedChargesData.put("deliveryFee", deliveryFee)

//        nestedStoreData.put("key", currentStore.key)
//        nestedStoreData.put("logoURL", currentStore.logoUrl)
//        nestedStoreData.put("latLng", currentStore.latlng)
//        nestedStoreData.put("name", currentStore.name)
//        nestedStoreData.put("address", currentStore.address)

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
                val latLng = convertLatLng(it.storeLatLng!!)
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

                val latLng = convertLatLng(it.latLng!!)
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
            val lat : Double? = (gpsVal?.get(0))?.toDouble();
            val lon : Double? = (gpsVal?.get(1))?.toDouble();

        val latLng = LatLng(lat!!,lon!!)

        return  latLng

    }

    fun calculateDeliveryFee() {

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
                            credits = credit.toInt()
                            Log.d(TAG, "Credits : $credits")

                            wallet_credit?.text = "KES ${credit.toInt()}"

//                            val total = amountEnough()
//                            if (credit.toBigDecimal() >= total.toBigDecimal()) {
//                                top_up_button?.visibility = View.GONE
//                                creditsEnough = true
//                                Log.d(TAG, "Credits Enough")
//                            } else {
//                                top_up_button?.visibility = View.VISIBLE
//                                creditsEnough = false
//                                Log.d(TAG, "Credits NOT Enough")
//
//                            }
                        }
                    }
                    //TODO Show Error or success dialog
                } else {
                    Log.d(TAG, "$source data: null")
                }
            })
        }
    }

    fun getDeliveryTimes() {
        val rightNow = Calendar.getInstance();
        val currentHourIn24Format = rightNow.get(Calendar.HOUR_OF_DAY);
        val currentDayOfTheYear = rightNow.get(Calendar.DAY_OF_YEAR);

        Log.d(TAG, "Delivery Times: $currentHourIn24Format")
        Log.d(TAG, "Delivery Day of Year: $currentDayOfTheYear")

        if(currentHourIn24Format < 8 ) {
            populateRadioButtons(R.array.from_9am)

        } else if (currentHourIn24Format < 9) {
            populateRadioButtons(R.array.from_10am)

        } else if (currentHourIn24Format < 10) {
            populateRadioButtons(R.array.from_11am)

        }else if (currentHourIn24Format < 11) {
            populateRadioButtons(R.array.from_12pm)

        }else if (currentHourIn24Format < 12) {

            populateRadioButtons(R.array.from_1pm)

        }else if (currentHourIn24Format < 13) {
            populateRadioButtons(R.array.from_2pm)

        }else if (currentHourIn24Format < 14) {
            populateRadioButtons(R.array.from_3pm)

        }else if (currentHourIn24Format < 15) {
            populateRadioButtons(R.array.from_4pm)

        }else if (currentHourIn24Format < 16) {
            populateRadioButtons(R.array.from_5pm)

        }else if (currentHourIn24Format < 17) {
            populateRadioButtons(R.array.from_6pm)

        } else if (currentHourIn24Format < 18) {
            populateRadioButtons(R.array.from_7pm)

        }else if (currentHourIn24Format < 19) {
            populateRadioButtons(R.array.from_8pm)

        } else {
            button_deliver_tomorrow?.isChecked = true
            button_deliver_today?.isChecked = false
            segmented2?.getChildAt(0)?.setEnabled(false);
            populateRadioButtons(R.array.from_9am)
        }
    }

    fun populateRadioButtons(radioArray: Int) {
        val radioGrp =  activity!!.findViewById<RadioGroup>(R.id.delivery_time_radio_group);
        radioGrp.removeAllViews()

        //get string array from source
        val timeArray = getResources().getStringArray(radioArray);

        //create radio buttons
        for (i in 0 until timeArray.size) {
            val radioButton = RadioButton(activity!!)
            radioButton.text = timeArray[i]
            radioButton.id = i
            radioGrp.addView(radioButton)
        }

        radioGrp.setOnCheckedChangeListener(object : RadioGroup.OnCheckedChangeListener {
            override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
                val checkedRadioButtonId = radioGrp.checkedRadioButtonId
                val radioButton = activity!!.findViewById<RadioButton>(checkedId)
                deliveryTime = radioButton.text.toString()
                Toast.makeText(activity, radioButton.text, Toast.LENGTH_SHORT).show()
            }

        })

    }
    private val TIP_AMOUNT = "TIP_AMOUNT"
    private val TIP_REQUEST_CODE = 100
    private var tipAmount = 100

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if(requestCode == TIP_REQUEST_CODE && resultCode == RESULT_OK && data != null){
            // The Task returned from this call is always completed, no need to attach
            // a listener.

             tipAmount = data.getIntExtra(TIP_AMOUNT, 100)
            tip_amount?.text = "KES $tipAmount"
            getDeliveryCharges()


        }
    }

    fun getCurrentDeliveryLocation() {
        val userUID = FirebaseAuth.getInstance().uid
        val ref =  MyDatabaseUtil.getDatabase().reference.child("customers/$userUID").child("delivery_locations/current_location")

        delivery_fee?.visibility = View.GONE
        delivery_fee_charges?.visibility = View.VISIBLE

        ref.addValueEventListener(object: ValueEventListener {

            override fun onCancelled(p0: DatabaseError) {
//                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(p0: DataSnapshot) {
                delivery_fee?.visibility = View.GONE
                delivery_fee_charges?.visibility = View.VISIBLE
                val location = p0.getValue(DeliveryLocation::class.java)

                if(location != null) {
                    deliveryLocation = location
                    Log.d(TAG, "location NOT null")
                    add_delivery_address_ll?.visibility = View.GONE
                    address_ll?.visibility = View.VISIBLE
                    address_one?.text = location.name
                    address_two?.text = location.address

//                    getDeliveryCharges()

                    currentStore?.let {
                        val from_name = it.storeName!!
                        val storeLatLng = convertLatLng(it.storeLatLng!!)
                        val from_lat = storeLatLng.latitude
                        val from_lon = storeLatLng.longitude

                        val locationLatLng = convertLatLng(location.latLng!!)
                        val to_lat = locationLatLng.latitude
                        val to_lon = locationLatLng.longitude

                        makeSendyDeliveryRequest(from_name, from_lat, from_lon, location.name!!, to_lat, to_lon)

                    }
                } else {
                    totalWithoutDeliveryLoc()
                    address_ll?.visibility = View.GONE
                    add_delivery_address_ll?.visibility = View.VISIBLE
                    Log.d(TAG, "location  null")

                }
            }

        })
    }


    fun makeSendyDeliveryRequest(from_name: String, from_lat : Double, from_long : Double, to_name: String, to_lat : Double, to_long : Double) {
        val retrofitResponse = immicartAPIService.makeSendyDeliveryRequest(from_name,from_lat, from_long,to_name, to_lat,  to_long )

        retrofitResponse.enqueue(object : Callback<Model.SendyRequestResponse> {
            override fun onFailure(call: Call<Model.SendyRequestResponse>, t: Throwable) {
                Log.d(TAG, "makeSendyDeliveryRequest onFailure")
            }

            override fun onResponse(
                call: Call<Model.SendyRequestResponse>,
                response: Response<Model.SendyRequestResponse>
            ) {
                Log.d(TAG, "makeSendyDeliveryRequest onResponse : ${response.body()}")
                val deliveryFee_ = response.body()?.data?.amount
                deliveryFee = deliveryFee_?.toInt()
                val currency = response.body()?.data?.currency
                delivery_fee?.visibility = View.VISIBLE
                delivery_fee_charges?.visibility = View.GONE
                delivery_fee?.text = "$currency $deliveryFee_"

                val trackingLink = response.body()?.data?.tracking_link
                val orderNumber = response.body()?.data?.order_no

                trackingLink?.let {
                    viewModel.setTrackingLink(it)
                }

                orderNumber?.let {
                    viewModel.setOrderNumber(it)
                }

                val serviceCharge = 0.05* storeSubtotal!!
                serviceFee = serviceCharge.toInt()
                service_fee_amount.text = "KES "+ serviceCharge.toInt()
                val formatter = DecimalFormat("#,###,###");

                val totalCost = storeSubtotal!! + serviceFee!! + deliveryFee!! + tipAmount
                Log.d(TAG, "totalCost : $totalCost")
                val totalCostFormattedString = formatter.format(totalCost);
                total_text_view?.text = "KES $totalCostFormattedString"
                totals = totalCost.toInt()

            }
        })
    }
}

val baseURL = "https://us-central1-immicart-2ca69.cloudfunctions.net/"

val immicartAPIService by lazy {
    ImmicartAPIService.create(baseURL)
}
