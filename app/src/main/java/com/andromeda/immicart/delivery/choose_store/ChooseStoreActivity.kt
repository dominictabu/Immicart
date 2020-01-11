package com.andromeda.immicart.delivery.choose_store

import android.Manifest
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.andromeda.immicart.R
import com.andromeda.immicart.delivery.authentication.StoreListRecyclerAdapter
import com.firebase.geofire.GeoFire
import com.firebase.geofire.GeoLocation
import com.firebase.geofire.GeoQuery
import com.firebase.geofire.GeoQueryEventListener
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.android.synthetic.main.activity_choose_store.*
import kotlinx.android.synthetic.main.content_choose_store.*
import java.util.*

class ChooseStoreActivity : AppCompatActivity() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var mLocationPermissionGranted : Boolean = false;
    private var locationRequestCode : Int = 1011;

    private var TAG = "ChooseStoreActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_store)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)


        getCurrentLocation()

        closeBtn.setOnClickListener {
            finish()
        }
    }


    fun getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // reuqest for permission
            mLocationPermissionGranted = false;

            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                locationRequestCode
            );

        }

        mLocationPermissionGranted = true;
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                // Got last known location. In some rare situations this can be null.

                location?.let {

                    val latLng = LatLng(it.latitude, it.longitude)

                    getClosestStores(latLng, radius_)


                }
            }



    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: kotlin.Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)


        when(requestCode) {
            1000 -> {
                if (grantResults.size > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    mLocationPermissionGranted = true;

                    getCurrentLocation();
                } else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                }
            }

        }
    }




    var radius_ = 1.0
    var timesCalled = 0
    var storesFound = false
    var storesAround: ArrayList<String> = ArrayList()
    fun getClosestStores(customerLocation: LatLng, radius: Double) {
        loading_view.show()

//        activityIndicator(true)
        val ref = FirebaseDatabase.getInstance().getReference("store_location");
//        val ref = FirebaseDatabase.getInstance().getReference("current_store_order_locations");
        val geoFire =  GeoFire(ref);

        val geoQuery : GeoQuery = geoFire.queryAtLocation(GeoLocation(customerLocation.latitude, customerLocation.longitude), radius);

        geoQuery.addGeoQueryEventListener(object: GeoQueryEventListener {
            override fun onGeoQueryReady() {

                Log.d(TAG, "onGeoQueryReady")
                if(!storesFound) {
                    radius_++

                    val latLng =  LatLng(-1.122, 36.134)
                    getClosestStores(latLng, radius_)
                } else {
                    getAllDocs(storesAround)
                }

            }

            override fun onKeyEntered(key: String?, location: GeoLocation?) {
//                activityIndicator(false)

                storesFound = true
                Log.d(TAG, "Radius: $radius")
                timesCalled++
                Log.d(TAG, "timesCalled: $timesCalled")
                Log.d(TAG, "GeoLocation: ${location.toString()}")
                Log.d(TAG, "Key found: $key")
                if(!storesAround.contains(key.toString()) && storesAround.size < 10) {
                    storesAround.add(key.toString())

                }

            }

            override fun onKeyMoved(key: String?, location: GeoLocation?) {
//                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                Log.d(TAG, "onKeyMoved")

            }

            override fun onKeyExited(key: String?) {
//                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                Log.d(TAG, "onKeyExited")

            }

            override fun onGeoQueryError(error: DatabaseError?) {
//                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                Log.d(TAG, "onGeoQueryError $error")

            }

        })

    }


    fun getAllDocs(storeIDs : ArrayList<String>) {
        // [START get_multiple_all]

        val stores = ArrayList<Store>()
        val db = FirebaseFirestore.getInstance();

        val collectionPath = "stores"
        db.collection(collectionPath)
            .whereIn("orderID", storeIDs)
            .get().addOnSuccessListener {
            loading_view.hide()

            Log.d(TAG, "Stores size" + it!!.size())
            for (document in it) {
                Log.d(TAG, document.id + " => " + document.data)
                val map = document.data
                val name = map["name"] as String?
                val logoUrl = map["logoUrl"] as String?
                val county = map["county"] as String?
                val address = map["address"] as String?
                val latLng = map["latLng"] as String?
                val number_of_reviews = map["number_of_reviews"] as Long?
                val i = number_of_reviews?.toInt()

                val avg_rating = map["avg_rating"] as Double?
                val _averageRating = avg_rating!!.toFloat()


                val store = Store(document.id, name, logoUrl, county, address, latLng, i!!, _averageRating, true)

                stores.add(store)
            }

            val linearLayoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
            recycler_store.setLayoutManager(linearLayoutManager)

            val storeAdapter = StoreListRecyclerAdapter(stores, {store: Store -> selectStore(store)})

            recycler_store.setAdapter(storeAdapter)


        }
    }


    fun selectStore(store: Store) {

    }

}
