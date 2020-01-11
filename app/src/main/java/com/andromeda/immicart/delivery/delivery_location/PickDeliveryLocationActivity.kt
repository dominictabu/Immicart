package com.andromeda.immicart.delivery.delivery_location

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.andromeda.immicart.R

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import java.util.*
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.android.synthetic.main.content_pick_delivery_location.*
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper
import kotlin.collections.ArrayList
import android.os.ResultReceiver
import android.location.Geocoder
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

//import androidx.car.cluster.navigation.LatLng


class PickDeliveryLocationActivity : AppCompatActivity(),
    PlacesAutoCompleteAdapter.OnItemClickListener {


    override fun OnItemClick(place: com.andromeda.immicart.delivery.delivery_location.Place?) {

        place?.let {
            latestPlace = it
        }
        address_one.text = place?.name
        address_two.text = place?.address
    }


    var AUTOCOMPLETE_REQUEST_CODE = 1
    val TAG = "EnterLocationAct"
    private lateinit var resultReceiver: AddressResultReceiver

    private var lastKnownLocation: Location? = null
    private lateinit var pickDeliveryLocationViewModel: PickDeliveryLocationViewModel
    private lateinit var latestPlace: com.andromeda.immicart.delivery.delivery_location.Place
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var mLocationPermissionGranted : Boolean = false;
    private var locationRequestCode : Int = 1000;


    private lateinit var placesClient: PlacesClient
    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase))
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.content_pick_delivery_location)

        setSupportActionBar(toolbar)

        if (supportActionBar != null) {
            toolbar_title.text = "Change Location"
            supportActionBar!!.setDisplayShowTitleEnabled(false);

            supportActionBar!!.setHomeButtonEnabled(true)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        }
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        pickDeliveryLocationViewModel = ViewModelProviders.of(this).get(PickDeliveryLocationViewModel::class.java)

        pickDeliveryLocationViewModel.allDeliveryLocations().observe(this, androidx.lifecycle.Observer {

            it?.let {

                if(it.size > 0) {

                    val place = it[0]

                    address_one.text = place.name
                    address_two.text = place.address
//                    latestPlace = place
                }

            }

        })

        // check permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // reuqest for permission
            mLocationPermissionGranted = false;

            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                locationRequestCode);

        } else {
            Log.d(TAG, "onMApInitialized Called: Location Permisiin");
            mLocationPermissionGranted = true;
        }


        enter_location_searchview.setIconifiedByDefault(false)
        Handler().postDelayed({
            //doSomethingHere
            enter_location_searchview.clearFocus()

        }, 1000)
        enter_location_searchview.queryHint = "Type new location"

        // Initialize place API
        if (!Places.isInitialized()) {
            Places.initialize(this, getString(R.string.google_maps_key));
        }

        // Create a new Places client instance.
        placesClient = Places.createClient(this)

        // Create a new token for the autocomplete session. Pass this to FindAutocompletePredictionsRequest,
        // and once again when the user makes a selection (for example when calling fetchPlace()).
        val token = AutocompleteSessionToken.newInstance()
        enter_location_searchview.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Use the builder to create a FindAutocompletePredictionsRequest.
                val request = FindAutocompletePredictionsRequest.builder()
                    //.setLocationRestriction(bounds)
                    .setCountry("KE")
                    .setSessionToken(token)
                    .setQuery(newText)
                    .build()

                placesClient.findAutocompletePredictions(request).addOnSuccessListener { response ->

                    val placesArrayList: ArrayList<com.andromeda.immicart.delivery.delivery_location.Place> = ArrayList()
                    for (prediction in response.autocompletePredictions) {

                        prediction.placeId

                        Log.d(TAG, "AutoComplete Predictions: $prediction." )

                        Log.i(TAG, "Place ID: ${prediction.placeId}")

                        val placeID : String = prediction.placeId

                        Log.i(TAG, "Prediction: " + prediction)

                        Log.i(TAG, "FullText: " + prediction.getFullText(null).toString())

                        val fullText = prediction.getFullText(null).toString()
                        val locations: List<String> = fullText.split(",")

                        if(locations.size > 0) {
                            val place = locations[0]
                            Log.d(TAG, "Place: $place")
                            val removedString = locations.drop(1)
                            Log.d(TAG, "removedString: $removedString")

                            val address = removedString.joinToString(", ")
                            Log.d(TAG, "address: $address")

                            val placeItem = Place(
                                placeID,
                                place,
                                address,
                                fullText
                            )
                            placesArrayList.add(placeItem)

                        }

                    }

                    initializePlacesAutoCompleteRecycler(placesArrayList)
                }.addOnFailureListener { exception ->
                    if (exception is ApiException) {
                        Log.e(TAG, "Place not found: " + exception.statusCode)
                    }
                }
                return true
            }

        })

//        // Set the fields to specify which types of place data to
//        // return after the user has made a selection.
//        val fields = Arrays.asList(Place.Field.ID, Place.Field.NAME)
//        card_view_current_location.setOnClickListener {
//            // Start the autocomplete intent.
//            val intent = Autocomplete.IntentBuilder(
//                AutocompleteActivityMode.FULLSCREEN, fields)
//                .setTypeFilter(TypeFilter.CITIES)
//                .setCountry("KE")
//
//                .build(this)
//            startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE)
//        }


        save_location_button.setOnClickListener {
            pickDeliveryLocationViewModel.deleteAllDeliveryLocations()
//            pickDeliveryLocationViewModel.insertDeliveryLocation(latestPlace)
            retrievePlace(latestPlace.placeID)
        }


    }

    fun retrievePlace(placeId: String) {
        val placeFields : List<Place.Field>  = arrayListOf(Place.Field.LAT_LNG, Place.Field.ADDRESS_COMPONENTS)
        placesClient.fetchPlace(FetchPlaceRequest.newInstance(placeId,placeFields )).addOnSuccessListener {

            val place = it.place
            Log.d(TAG, "Retrieved Place: $place")
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
    fun getCurrentLocation() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // reuqest for permission
            mLocationPermissionGranted = false;

            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                locationRequestCode);

        } else {
            Log.d(TAG, "onMApInitialized Called: Location Permisiin");
            mLocationPermissionGranted = true;
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    // Got last known location. In some rare situations this can be null.

                    if (!Geocoder.isPresent()) {
                        Toast.makeText(this@PickDeliveryLocationActivity,
                            R.string.no_geocoder_available,
                            Toast.LENGTH_LONG).show()
                        return@addOnSuccessListener
                    }

                    location?.let {

                        lastKnownLocation = it
                        startIntentService(it)
                    }
                }
        }

    }


    private fun startIntentService(location: Location) {

        val intent = Intent(this, FetchAddressIntentService::class.java).apply {
            putExtra(FetchAddressIntentService.Constants.RECEIVER, resultReceiver)
            putExtra(FetchAddressIntentService.Constants.LOCATION_DATA_EXTRA, location)
        }
        startService(intent)
    }



    fun initializePlacesAutoCompleteRecycler(places : ArrayList<com.andromeda.immicart.delivery.delivery_location.Place>) {
        val linearLayoutManager = LinearLayoutManager(this)
        recycler_items.layoutManager = linearLayoutManager

        val ATTRS = intArrayOf(android.R.attr.listDivider)

//        val a = this.obtainStyledAttributes(ATTRS)
//        val divider = a.getDrawable(0)
//        val inset = resources.getDimensionPixelSize(R.dimen.locations)
//        val insetDivider = InsetDrawable(divider, inset, 0, 0, 0)
//        a.recycle()
//        val dividerItemDecoration = DividerItemDecoration(recycler_items.getContext(),
//                linearLayoutManager.orientation)
//        dividerItemDecoration.setDrawable(insetDivider)

//        recycler_items.addItemDecoration(dividerItemDecoration)

        val placesAutoCompleteAdapter = PlacesAutoCompleteAdapter(
            places,
            this@PickDeliveryLocationActivity,
            this@PickDeliveryLocationActivity
        )

        recycler_items.adapter = placesAutoCompleteAdapter

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

    }

    internal inner class AddressResultReceiver(handler: Handler) : ResultReceiver(handler) {

        override fun onReceiveResult(resultCode: Int, resultData: Bundle?) {

            // Display the address string
            // or an error message sent from the intent service.
             val addressOutput = resultData?.getString(FetchAddressIntentService.Constants.RESULT_DATA_KEY) ?: ""
//            TODO displayAddressOutput()

            // Show a toast message if an address was found.
            if (resultCode == FetchAddressIntentService.Constants.SUCCESS_RESULT) {
//                showToast(getString(R.string.address_found))

                lastKnownLocation?.let {

                }



            }

        }
    }


    fun postDeliveryAddress(address: String, latitude: String, longitude: String ) {
        val uid = FirebaseAuth.getInstance().uid
        uid?.let {
            val collectionPath = "customers/$uid/delivery_addresses"
            val db = FirebaseFirestore.getInstance();

            val addressObject = HashMap<String, Any>()
            addressObject.put("address", address)
//            addressObject.put("latLng", latLng)

            db.collection(collectionPath).add(addressObject).addOnSuccessListener {

            }

        }
    }

}
