package com.andromeda.immicart.delivery.checkout


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
import com.andromeda.immicart.delivery.DeliveryCart
import com.andromeda.immicart.delivery.choose_store.Store
import com.andromeda.immicart.delivery.persistence.CurrentLocation
import com.firebase.geofire.GeoFire
import com.firebase.geofire.GeoLocation
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
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
    private lateinit var currentStore: Store
    private var storeSubtotal: Float? = null
    private lateinit var deliveryLocation: CurrentLocation
    private  var TAG: String = "LoadingPlaceOrderFrag"
    var cartItems: List<DeliveryCart> = ArrayList<DeliveryCart>()


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

        viewModel = ViewModelProviders.of(activity!!).get(DeliveryCartViewModel::class.java)

        auth = FirebaseAuth.getInstance()

        viewModel.allDeliveryLocations().observe(activity!!, androidx.lifecycle.Observer {

            it?.let {
                if(it.size > 0) {
                    val place = it[0]
                    deliveryLocation = place

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


        viewModel.currentStores().observe(activity!!, androidx.lifecycle.Observer {
            it?.let {
                if(it.size > 0) {
                    Log.d(TAG, "Stores size more than 0")
                    currentStore = it[0]

                    toolbar_title.text = "${currentStore.name}'s order"
                    store_order.text = "Your ${currentStore.name}'s order"

                } else {
                    Log.d(TAG, "Stores size 0")

                }
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
                Toast.makeText(activity!!, "Token Retrieved", Toast.LENGTH_SHORT).show()
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
                Toast.makeText(activity!!, "Token Retrieved", Toast.LENGTH_SHORT).show()
            })

    }


    fun checkOut(token : String) {

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
        val nestedOrderStatus = HashMap<String, Boolean>()

        nestedOrderStatus.put("assigned", false)
        nestedOrderStatus.put("shopping", false)
        nestedOrderStatus.put("delivering", false)
        nestedOrderStatus.put("completed", false)

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




        val db = FirebaseFirestore.getInstance();

        val doc = db.collection(collectionPath).document()
        val key = doc.id


        orderMap.put("customerUID", customerUID)
        orderMap.put("orderID", key)
        orderMap.put("deviceToken", token)
        orderMap.put("mobileNumber", "0796026997")
        orderMap.put("items", items)
        orderMap.put("datePosted", datePosted)
        orderMap.put("timeInMillisCreated", timeInMillis)
        orderMap.put("orderAccepted", false)
        orderMap.put("deliveryTime", nestedDeliveryTimeData)
        orderMap.put("payments", nestedChargesData)
        orderMap.put("deliveryAddress", nestedDeliveryAddressData)
        orderMap.put("store", nestedStoreData)
        orderMap.put("orderStatus", nestedOrderStatus)

//
        db.collection(collectionPath).document(key).set(orderMap).addOnSuccessListener(OnSuccessListener {
            Toast.makeText(activity!!, "Successfully Placed order.",
                Toast.LENGTH_LONG).show()
            Log.d(TAG, "Success DocumentReference: ${key}")

//            val key = it.id



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

            findNavController().navigate(R.id.action_loadingPlaceOrderFragment_to_successOrderPlacementFragment)


        }).addOnFailureListener {
            Log.d(TAG, "Exception: ${it.toString()}")
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
