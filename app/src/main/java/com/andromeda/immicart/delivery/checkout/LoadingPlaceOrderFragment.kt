package com.andromeda.immicart.delivery.checkout


import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController

import com.andromeda.immicart.R
import com.andromeda.immicart.delivery.CurrentStore
import com.andromeda.immicart.delivery.DeliveryCart
import com.andromeda.immicart.delivery.choose_store.Store
import com.andromeda.immicart.delivery.persistence.CurrentLocation
import com.andromeda.immicart.delivery.persistence.DeliveryLocation
import com.firebase.geofire.GeoFire
import com.firebase.geofire.GeoLocation
import com.firebase.geofire.GeoQueryDataEventListener
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.iid.FirebaseInstanceId
import com.google.type.LatLng
import kotlinx.android.synthetic.main.fragment_loading_place_order.*
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern
import kotlin.collections.HashMap

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [LoadingPlaceOrderFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class LoadingPlaceOrderFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var viewModel: DeliveryCartViewModel
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    private  var currentStore: CurrentStore? = null
    private  var trackingLink: String? = null
    private  var orderNumber: String? = null
    private var storeSubtotal: Float? = null
    private  var deliveryLocation: DeliveryLocation? = null
    private lateinit var deliveryDetails: DeliveryDetails
    private  var TAG: String = "LoadingPlaceOrderFrag"
    var cartItems: List<DeliveryCart> = ArrayList<DeliveryCart>()

    val PREF_NAME = "IS_PICKUP"
    val keyChannel = "IS_PICKUP"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
//        activity!!.window.statusBarColor = ContextCompat.getColor(activity!!, R.color.colorGreen)

        return inflater.inflate(R.layout.fragment_loading_place_order, container, false)
    }

//
//    override fun onStop() {
//        activity!!.window.statusBarColor = ContextCompat.getColor(activity!!, R.color.colorPrimary)
//        super.onStop()
//
//    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        database = FirebaseDatabase.getInstance().reference


        viewModel = ViewModelProviders.of(activity!!).get(DeliveryCartViewModel::class.java)

        auth = FirebaseAuth.getInstance()

        getCurrentStore()
        getCurrentDeliveryLocation()
//        viewModel.allDeliveryLocations().observe(activity!!, androidx.lifecycle.Observer {
//
//            it?.let {
//                if(it.size > 0) {
//                    val place = it[0]
//                    deliveryLocation = place
//
//                }
//
//            }
//
//        })


        viewModel.allDeliveryItems().observe(this, androidx.lifecycle.Observer { items ->
            // Update the cached copy of the words in the adapter.
            items?.let {
                //                cartItems = items
//                badge.text = it.count().toString()
//                adapter.setCartItems(it)
                cartItems = items

//                adapter.submitList(it)

                val images : ArrayList<String> = ArrayList()
                var total = 0
                it.forEach {
                    val quantity = it.quantity
                    val unitPrice = it.offerPrice.toInt()
                    val subtotal = quantity * unitPrice
                    total += subtotal
//                    images.add(it.image_url)
                }


                storeSubtotal= total.toFloat()


            }
        })


        viewModel.sendy_order_number.observe(this, androidx.lifecycle.Observer {

        })


//        viewModel.currentStores().observe(activity!!, androidx.lifecycle.Observer {
//            it?.let {
//                if(it.size > 0) {
//                    Log.d(TAG, "Stores size more than 0")
//                    currentStore = it[0]
//
//                    toolbar_title.text = "${currentStore.name}'s order"
//                    store_order.text = "Your ${currentStore.name}'s order"
//
//                } else {
//                    Log.d(TAG, "Stores size 0")
//
//                }
//            }
//        })

        viewModel.deliveryDetails.observe(activity!!, androidx.lifecycle.Observer {
            it?.let {
                deliveryDetails = it
            }
        })

        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w(TAG, "getInstanceId failed", task.exception)
                    return@OnCompleteListener
                }

                // Get new Instance ID token
                val token = task.result?.token

                Log.d(TAG, "Device Token: $token")
                token?.let {
                    checkOut(token)

                }


                // Log and toast
//                val msg = getString(R.string.msg_token_fmt, token)
//                Log.d(TAG, msg)
//                Toast.makeText(activity!!, "Token Retrieved", Toast.LENGTH_SHORT).show()
            })


    }

    override fun onStart() {
        super.onStart()
    }


    fun getToken(){

        // Get token
        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w(TAG, "getInstanceId failed", task.exception)
                    return@OnCompleteListener
                }

                // Get new Instance ID token
                val token = task.result?.token
                Log.d(TAG, "Device Token: $token")

                // Log and toast
//                val msg = getString(R.string.msg_token_fmt, token)
//                Log.d(TAG, msg)
//                Toast.makeText(activity!!, "Token Retrieved", Toast.LENGTH_SHORT).show()
            })

    }


    fun checkOut(token : String) {

        val collectionPath = "orders"
        var user = auth.currentUser?.uid;

//        var deliveryAddress: String = deliveryLocation

        var customerUID = auth.currentUser?.uid
        var storeFee: Float = 0f

        if (storeSubtotal != null) {
            storeFee = storeSubtotal!!
        }
        var serviceFee: Float = 50f
        var deliveryFee: Float = 100f
        var items: List<DeliveryCart> = cartItems
        var numberOfItems = 0
        items.forEach {
            numberOfItems += it.quantity
        }
        Log.d(TAG, "Number of items: $numberOfItems")

        val timeInMillis = System.currentTimeMillis();



//        val rating = ratingBar.getRating()
        val date: Date // your date
// Choose time zone in which you want to interpret your Date

        val dateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.ENGLISH)
        val nestedDateData = HashMap<String, Any>()
        val nestedOrderStatus = HashMap<String, Boolean>()
        val nestedUserData = HashMap<String, Boolean>()


        nestedOrderStatus.put("assigned", false)
        nestedOrderStatus.put("shopping", false)
        nestedOrderStatus.put("delivering", false)
        nestedOrderStatus.put("completed", false)

        val cal = Calendar.getInstance()
        cal.getTime()
        val year = cal.get(Calendar.YEAR)
        val month = cal.get(Calendar.MONTH)
        val day = cal.get(Calendar.DAY_OF_YEAR)

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
        val nestedDeliveryMode = HashMap<String, Any>()

        deliveryDetails?.let {
            nestedDeliveryTimeData.put("dayOfDelivery", it.dayOfDelivery)
            nestedDeliveryTimeData.put("timeOfDelivery", it.timeOfDelivery)
            nestedDeliveryTimeData.put("yearOfDelivery", it.year)

            val timeInInt = covertDeliveryTimeToInt(it.timeOfDelivery)
            Log.d(TAG, "time in Int $timeInInt")
            nestedDeliveryTimeData.put("timeOfDeliveryInInt", timeInInt)

        }


        deliveryLocation?.let {

            val latLng = convertLatLng(it.latLng!!)
            val lat = latLng.latitude
            val lon = latLng.longitude

            nestedDeliveryAddressData.put("Name", it.name!!)
            nestedDeliveryAddressData.put("Address", it.address!!)
            nestedDeliveryAddressData.put("PlaceFullText", it.placeFullText!!)
            nestedDeliveryAddressData.put("LatLng", it.latLng!!)
            nestedDeliveryAddressData.put("longitude", lon)
            nestedDeliveryAddressData.put("latitude", lat)
        }

        deliveryDetails?.let {

            nestedChargesData.put("storeSubtotal", it.storeSubtotal)
            nestedChargesData.put("serviceFee", it.serviceFee)
            nestedChargesData.put("deliveryFee", it.deliveryFee)
            orderMap.put("mobileNumber", it.phone)

        }

        if(currentStore != null) {
            nestedStoreData.put("key", currentStore?.storeID!!)
            nestedStoreData.put("logoURL", currentStore?.storeLogo!!)
            nestedStoreData.put("latLng", currentStore?.storeLatLng!!)
            nestedStoreData.put("name", currentStore?.storeName!!)
            nestedStoreData.put("address", currentStore?.storeAddress!!)
        }

        val db = FirebaseFirestore.getInstance();

        val doc = db.collection(collectionPath).document()
        val key = doc.id

        val shared = activity!!.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        val channel = (shared.getBoolean(keyChannel, false));
        if(channel) {
            nestedDeliveryMode.put("deliveryMode", 0)   // 0 represents Pick Up
            nestedDeliveryMode.put("isDelivery", false)
            nestedDeliveryMode.put("isPickUp", true)
        } else {
            nestedDeliveryMode.put("deliveryMode", 1)  // 1 represents Delivery
            nestedDeliveryMode.put("isDelivery", true)
            nestedDeliveryMode.put("isPickUp", false)
        }

        customerUID?.let {
            orderMap.put("customerUID", customerUID)
        }
        orderMap.put("orderID", key)
        orderMap.put("deviceToken", token)
        orderMap.put("items", items)
        orderMap.put("numberOfItems", numberOfItems)
        orderMap.put("datePosted", datePosted)
        orderMap.put("timeInMillisCreated", timeInMillis)
        orderMap.put("orderAccepted", false)
        orderMap.put("deliveryTime", nestedDeliveryTimeData)
        orderMap.put("payments", nestedChargesData)
        orderMap.put("deliveryAddress", nestedDeliveryAddressData)
        orderMap.put("store", nestedStoreData)
        orderMap.put("orderStatus", nestedOrderStatus)
        orderMap.put("deliveryMode", nestedDeliveryMode)

        val timeInInt = covertDeliveryTimeToInt(deliveryDetails.timeOfDelivery)

        db.collection(collectionPath).document(key).set(orderMap).addOnSuccessListener(OnSuccessListener {
            Toast.makeText(activity!!, "Successfully Placed order.",
                Toast.LENGTH_LONG).show()
            Log.d(TAG, "Success DocumentReference: ${key}")
            findNavController().navigate(R.id.action_loadingPlaceOrderFragment_to_successOrderPlacementFragment)


//            val key = it.id
//            val ref = FirebaseDatabase.getInstance().getReference("current_store_order_locations")
//            val dateRef = FirebaseDatabase.getInstance().getReference("current_store_order_locations/${deliveryDetails.year}/${deliveryDetails.dayOfDelivery}/$timeInInt")
//            val geoFireStore =  GeoFire(dateRef);
//            currentStore?.let {
//
//                val geoFireRef = geoFireStore.databaseReference
//                val latLng = convertLatLng(it.storeLatLng!!)
//                val lat = latLng.latitude
//                val lon = latLng.longitude
//
//                geoFireStore.setLocation(key,  GeoLocation(lat, lon), object : GeoFire.CompletionListener {
//                    override fun onComplete(geoKey: String?, error: DatabaseError?) {
//                        geoKey?.let {
//
//                            val geoCoderPoint = GeoCoderPoint(key, items.size,  deliveryDetails.dayOfDelivery,timeInInt,  deliveryDetails.timeOfDelivery, deliveryLocation?.latLng!!, currentStore?.storeName!!,
//                                currentStore?.storeLatLng!!, deliveryDetails.deliveryFee)
//                            geoFireRef.child(it).child("d").setValue(geoCoderPoint)
//
//                        }
//
//                    }
//                })
//
//            }

//            val storeInfo = storeInfo(currentStore?.storeName!!, currentStore?.storeAddress!!, currentStore?.storeLogo!!)
//            val deliveryInfo = deliveryInfo(deliveryLocation?.name!!, deliveryLocation?.address!!)

//            deliveryLocation?.let {
//
//                val ref = FirebaseDatabase.getInstance().getReference("customer_order_locations")
//                val geoFire =  GeoFire(ref);
//
//                val latLng = convertLatLng(it.latLng!!)
//                val lat = latLng.latitude
//                val lon = latLng.longitude
//
//                val storelatlng = convertLatLng(currentStore?.storeLatLng!!)
//
//                val deliveryLocLatLng = locLatLng(lat, lon)
//                val storeLocLatLng = locLatLng(storelatlng.latitude, storelatlng.longitude)
//
//                val post = Post(key, customerUID, currentStore?.storeID!!, deliveryLocLatLng, storeLocLatLng, deliveryDetails.timeOfDelivery, timeInInt, false, false, numberOfItems, deliveryDetails.deliveryFee, storeInfo,deliveryInfo)
//                writeNewPost(post)


//                geoFire.setLocation(key,  GeoLocation(lat, lon), object : GeoFire.CompletionListener {
//                    override fun onComplete(key: String?, error: DatabaseError?) {
//
//                    }
//
//                })
//
//                val geoQuery = geoFireStore.queryAtLocation( GeoLocation(lat, lon), 10.0);
//                geoQuery.addGeoQueryDataEventListener(object : GeoQueryDataEventListener {
//                    override fun onGeoQueryReady() {
//                        Log.d(TAG, " addGeoQueryDataEventListener onGeoQueryReady")
//                    }
//
//                    override fun onDataExited(dataSnapshot: DataSnapshot?) {
//                        Log.d(TAG, " addGeoQueryDataEventListener onDataExited : $dataSnapshot")
//
//                    }
//
//                    override fun onDataChanged(dataSnapshot: DataSnapshot?, location: GeoLocation?) {
//                        Log.d(TAG, " addGeoQueryDataEventListener onDataChanged ${dataSnapshot?.value} : location: $location ")
//                    }
//
//                    override fun onDataEntered(dataSnapshot: DataSnapshot?, location: GeoLocation?) {
//                        Log.d(TAG, " addGeoQueryDataEventListener onDataEntered ${dataSnapshot?.value} : location: $location ")
//                    }
//
//                    override fun onDataMoved(dataSnapshot: DataSnapshot?, location: GeoLocation?) {
//                        Log.d(TAG, " addGeoQueryDataEventListener onDataMoved ${dataSnapshot?.value} : location: $location ")
//                    }
//
//                    override fun onGeoQueryError(error: DatabaseError?) {
//                        Log.d(TAG, " addGeoQueryDataEventListener onGeoQueryError $error")
//                    }
//
//                })

//            }


        }).addOnFailureListener {
            Log.d(TAG, "Exception: ${it.toString()}")
        }

    }

    private fun writeNewPost(post : Post) {
        // Create new post at /user-posts/$userid/$postid and at
        // /posts/$postid simultaneously
        val key = database.child("orders/${deliveryDetails.year}/${deliveryDetails.dayOfDelivery}/${post.timeOfDeliveryInInt}").push().key
        if (key == null) {
            Log.w(TAG, "Couldn't get push key for posts")
            return
        }

        val postValues = post.toMap()

        val childUpdates = HashMap<String, Any>()
        childUpdates["orders/${deliveryDetails.year}/${deliveryDetails.dayOfDelivery}/${post.timeOfDeliveryInInt}/$key"] = postValues

        database.updateChildren(childUpdates).addOnSuccessListener {
            findNavController().navigate(R.id.action_loadingPlaceOrderFragment_to_successOrderPlacementFragment)
        }
    }

    fun convertLatLng(latlng : String) : LatLng {
        var from_lat_lng : String? = null
        val m : Matcher = Pattern.compile("\\(([^)]+)\\)").matcher(latlng);
        while(m.find()) {
            from_lat_lng = m.group(1) ;
        }
        val gpsVal = (from_lat_lng?.split(","));
        val lat : Double = (gpsVal?.get(0))!!.toDouble();
        val lon : Double = (gpsVal?.get(1))!!.toDouble();

        val latLng = LatLng.newBuilder().setLatitude(lat).setLongitude(lon).build()

        return  latLng
    }

    fun covertDeliveryTimeToInt(time: String): Int {

        if (time.equals(activity!!.getResources().getString(R.string.from9am))) {
            return 9

        } else if (time.equals(activity!!.getResources().getString(R.string.from10am))) {
            return 10

        } else if (time.equals(activity!!.getResources().getString(R.string.from11am))) {
            return 11

        } else if (time.equals(activity!!.getResources().getString(R.string.from12pm))) {
            return 12

        } else if (time.equals(activity!!.getResources().getString(R.string.from1pm))) {
            return 13

        } else if (time.equals(activity!!.getResources().getString(R.string.from2pm))) {
            return 14

        } else if (time.equals(activity!!.getResources().getString(R.string.from3pm))) {
            return 15

        } else if (time.equals(activity!!.getResources().getString(R.string.from4pm))) {
            return 16

        } else if (time.equals(activity!!.getResources().getString(R.string.from5pm))) {
            return 17

        } else if (time.equals(activity!!.getResources().getString(R.string.from6pm))) {
            return 18

        } else if (time.equals(activity!!.getResources().getString(R.string.from7pm))) {
            return 19

        } else if (time.equals(activity!!.getResources().getString(R.string.from8pm))) {
            return 20

        } else {
            return -1

        }
    }


    fun getCurrentDeliveryLocation() {
        val userUID = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().reference.child("customers/$userUID").child("delivery_locations/current_location")

        ref.addValueEventListener(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
//                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(p0: DataSnapshot) {
                val location = p0.getValue(DeliveryLocation::class.java)

                if(location != null) {
                    deliveryLocation = location
                    Log.d(TAG, "location NOT null")

                } else {
                    Log.d(TAG, "location  null")


                }
            }

        })

    }

    fun getCurrentStore() {
        val userUID = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().reference.child("customers/$userUID/current_store")

        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(p0: DataSnapshot) {
                val store = p0.getValue(CurrentStore::class.java)
                Log.d(TAG, "Store $store")
                currentStore = store

                store?.let {

                    toolbar_title.text = "${store.storeName}'s order"
                    store_order.text = "Your ${store.storeName}'s order"
                }
            }
        })
    }



    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment LoadingPlaceOrderFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            LoadingPlaceOrderFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
