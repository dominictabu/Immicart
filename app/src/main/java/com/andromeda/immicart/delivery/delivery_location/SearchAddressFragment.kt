package com.andromeda.immicart.delivery.delivery_location


import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.os.ResultReceiver
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.andromeda.immicart.R
import com.andromeda.immicart.delivery.Utils.MyLocation
import com.andromeda.immicart.delivery.persistence.CurrentLocation
import com.andromeda.immicart.delivery.persistence.DeliveryLocation
import com.bumptech.glide.Glide
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_search_address.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SearchAddressFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class SearchAddressFragment : Fragment(), PlacesAutoCompleteAdapter.OnItemClickListener {

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    var AUTOCOMPLETE_REQUEST_CODE = 1
    val TAG = "EnterLocationAct"
    private lateinit var database: DatabaseReference

    private var lastKnownLocation: Location? = null
    private lateinit var pickDeliveryLocationViewModel: PickDeliveryLocationViewModel
    private lateinit var latestPlace: Place
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var mLocationPermissionGranted : Boolean = false;
    private var locationRequestCode : Int = 1000;
    private lateinit var placesClient: PlacesClient
    private lateinit var addressRecyclerAdapters : AddressRecyclerAdapters
    private lateinit var resultReceiver: AddressResultReceiver
    private var currentLocation: Location? = null
    private var allLocations: ArrayList<DeliveryLocation> = ArrayList()


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
        return inflater.inflate(R.layout.fragment_search_address, container, false)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        delivery_locations_addresses.visibility = View.VISIBLE
//        av_view.hide()
        database = FirebaseDatabase.getInstance().reference


        pickDeliveryLocationViewModel = ViewModelProviders.of(activity!!).get(PickDeliveryLocationViewModel::class.java)


        enter_location_searchview.setIconifiedByDefault(false)
        Handler().postDelayed({
            //doSomethingHere
            enter_location_searchview.clearFocus()

        }, 1000)
        enter_location_searchview.queryHint = "Type new location"

        // Initialize place API
        if (!Places.isInitialized()) {
            Places.initialize(activity!!, getString(R.string.google_maps_key));
        }

        // Create a new Places client instance.
        placesClient = Places.createClient(activity!!)
        getCurrentLocation()
        getAllLocations()
        addressRecyclerAdapters = AddressRecyclerAdapters({place: DeliveryLocation -> setCurrentLocation(place)})


        // Create a new token for the autocomplete session. Pass this to FindAutocompletePredictionsRequest,
        // and once again when the user makes a selection (for example when calling fetchPlace()).
        val token = AutocompleteSessionToken.newInstance()
        enter_location_searchview.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
//                av_view.show()
                // Use the builder to create a FindAutocompletePredictionsRequest.
                val request = FindAutocompletePredictionsRequest.builder()
                    //.setLocationRestriction(bounds)
                    .setCountry("KE")
                    .setSessionToken(token)
                    .setQuery(newText)
                    .build()

                placesClient.findAutocompletePredictions(request).addOnSuccessListener { response ->

                    val placesArrayList: ArrayList<Place> = ArrayList()
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

        go_back_button.setOnClickListener {
            activity!!.finish()
        }
    }

    fun setCurrentLocation(place: DeliveryLocation) {
        setCurrentLocInFirebase(place)

//        pickDeliveryLocationViewModel.deleteAllDeliveryLocations()
//        pickDeliveryLocationViewModel.insertCurrentDeliveryLocation(place)
    }

    override fun OnItemClick(place: Place?) {
        place?.let {

            enter_location_searchview?.clearFocus()
            retrievePlace(it)
        }
    }


    fun retrievePlace(place_: Place) {
        val placeFields : List<com.google.android.libraries.places.api.model.Place.Field>  = arrayListOf(com.google.android.libraries.places.api.model.Place.Field.LAT_LNG, com.google.android.libraries.places.api.model.Place.Field.ADDRESS_COMPONENTS)
        placesClient.fetchPlace(FetchPlaceRequest.newInstance(place_.placeID,placeFields )).addOnSuccessListener {

            val place = it.place
            val deliveryLocation = DeliveryLocation(place_.placeID, place_.name, place_.address,place_.placeFullText, place.latLng.toString())
            val currentDeliveryLocation = CurrentLocation(place_.placeID, place_.name, place_.address,place_.placeFullText, place.latLng.toString())
            setNewCurrentLocInFirebase(deliveryLocation)


//            pickDeliveryLocationViewModel.deleteAllDeliveryLocations()
//            pickDeliveryLocationViewModel.insertCurrentDeliveryLocation(currentDeliveryLocation)
//            pickDeliveryLocationViewModel.insertDeliveryLocation(deliveryLocation)
            Log.d(TAG, "Retrieved Place: $place")
        }
    }


    fun initializePlacesAutoCompleteRecycler(places : ArrayList<Place>) {
        delivery_locations_addresses.visibility = View.GONE
        recycler_items.visibility = View.VISIBLE
        val linearLayoutManager = LinearLayoutManager(activity!!)
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
            activity!!,
            this@SearchAddressFragment
        )

//        av_view.hide()


        recycler_items.adapter = placesAutoCompleteAdapter

    }

    val myLocation =  MyLocation();


    override fun onPause() {
        super.onPause()
        myLocation.cancelTimer()

    }


    override fun onResume() {
        super.onResume()
        resultReceiver = AddressResultReceiver(Handler())

    }

    fun getCurrentLocation() {

        if (ActivityCompat.checkSelfPermission(
                activity!!,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(
                activity!!,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // reuqest for permission
            mLocationPermissionGranted = false;

            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                locationRequestCode
            );

        } else {
            Log.d(TAG, "onMApInitialized Called: Location Permisiin");
            mLocationPermissionGranted = true;
//            fusedLocationClient.lastLocation
//                .addOnSuccessListener { location: Location? ->
//                    // Got last known location. In some rare situations this can be null.
//
//                    if (!Geocoder.isPresent()) {
//                        Toast.makeText(activity!!,
//                            R.string.no_geocoder_available,
//                            Toast.LENGTH_LONG).show()
//                        return@addOnSuccessListener
//                    }
//
//                    location?.let {
//
//                        lastKnownLocation = it
//                        startIntentService(it)
//                    }
//                }

            val locationResult = object : MyLocation.LocationResult() {
                override fun gotLocation(location: Location?) {
                    if (!Geocoder.isPresent()) {
                        Toast.makeText(
                            activity!!,
                            R.string.no_geocoder_available,
                            Toast.LENGTH_LONG
                        ).show()
                        return
                    }

                    location?.let {

                        lastKnownLocation = it
                        startIntentService(it)
                    }


                };
            }
            myLocation.getLocation(activity, locationResult);
        }
    }

    private fun startIntentService(location: Location) {

        val intent = Intent(activity, FetchAddressIntentService::class.java).apply {
            putExtra(FetchAddressIntentService.Constants.RECEIVER, resultReceiver)
            putExtra(FetchAddressIntentService.Constants.LOCATION_DATA_EXTRA, location)
        }
        activity!!.startService(intent)
    }



        internal inner class AddressResultReceiver(handler: Handler) : ResultReceiver(handler) {

//        private var mReceiver: ResultReceiver


        override fun onReceiveResult(resultCode: Int, resultData: Bundle?) {

            Log.d(TAG, "onReceiveResult called")

            // Display the address string
            // or an error message sent from the intent service.

            val addressOutput = resultData?.getString(FetchAddressIntentService.Constants.RESULT_DATA_KEY) ?: ""

//            address = addressOutput
//            TODO displayAddressOutput()
            Log.d(TAG, "Address Output in Fragment: $addressOutput")

            // Show a toast message if an address was found.
            if (resultCode == FetchAddressIntentService.Constants.SUCCESS_RESULT) {
//                showToast(getString(R.string.address_found))

                lastKnownLocation?.let {
//                    current_location_address_two?.text = addressOutput

                    val placeID = it.latitude.toString() + it.longitude.toString()
                    val latLng = LatLng(it.latitude, it.longitude)
                     currentLocation = it
                    val deliveryLocation = DeliveryLocation(placeID, addressOutput, addressOutput,addressOutput, latLng.toString(), true)
                    if(!allLocations.contains(deliveryLocation)) {
                        allLocations.add(0, deliveryLocation)
                        addressRecyclerAdapters.set_DeliveryLocations(allLocations)
                    }



                }

            }

        }
    }


    fun getPreviousAddresses() {
        val userUID = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().reference.child("customers/$userUID/current_store")

        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(p0: DataSnapshot) {
                    val location = p0.getValue(DeliveryLocation::class.java)

                    location?.let {

                    }


            }

        })
    }

    fun intializeRecycler() {


        val linearLayoutManager = LinearLayoutManager(activity!!, RecyclerView.VERTICAL, false)
        linearLayoutManager.reverseLayout = true
        linearLayoutManager.stackFromEnd = true
        delivery_locations_addresses?.setNestedScrollingEnabled(false);

        delivery_locations_addresses?.setLayoutManager(linearLayoutManager)
        delivery_locations_addresses?.setAdapter(addressRecyclerAdapters)
    }


    fun setNewCurrentLocInFirebase(location: DeliveryLocation) {

        val storeMap = HashMap<String, Any>()
        storeMap.put("placeID", location.placeID!!)
        storeMap.put("name", location.name!!)
        storeMap.put("address", location.address!!)
        storeMap.put("placeFullText", location.placeFullText!!)
        storeMap.put("latLng", location.latLng!!)


        val userUID = FirebaseAuth.getInstance().uid
        val ref = database.child("customers/$userUID").child("delivery_locations/current_location")
        val refAll = database.child("customers/$userUID").child("delivery_locations/all_locations")
        ref.setValue(storeMap).addOnSuccessListener {
            refAll.push().setValue(location).addOnSuccessListener {
                Log.d(TAG, "Successfully Added")
                activity!!.finish()

            }

        }

    }

    fun setCurrentLocInFirebase(location: DeliveryLocation) {

        val storeMap = HashMap<String, Any>()
        storeMap.put("placeID", location.placeID!!)
        storeMap.put("name", location.name!!)
        storeMap.put("address", location.address!!)
        storeMap.put("placeFullText", location.placeFullText!!)
        storeMap.put("latLng", location.latLng!!)


        val userUID = FirebaseAuth.getInstance().uid
        val ref = database.child("customers/$userUID").child("delivery_locations/current_location")
        val refAll = database.child("customers/$userUID").child("delivery_locations/all_locations")
        ref.setValue(storeMap).addOnSuccessListener {
            activity!!.finish()

        }

    }

    fun getAllLocations() {

        val userUID = FirebaseAuth.getInstance().uid
        val refAll = database.child("customers/$userUID").child("delivery_locations/all_locations")

        refAll.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(p0: DataSnapshot) {
                for(snap in p0.children) {
                    val location = snap.getValue(DeliveryLocation::class.java)

                    location?.let {
                        allLocations.add(it)



                    }
                }
                initializePastLocationsRecycler()
                addressRecyclerAdapters.set_DeliveryLocations(allLocations)

            }

        })

    }

    fun initializePastLocationsRecycler() {


        val linearLayoutManager = LinearLayoutManager(activity!!, RecyclerView.VERTICAL, false)
        linearLayoutManager.reverseLayout = false
        linearLayoutManager.stackFromEnd = false
        delivery_locations_addresses?.setNestedScrollingEnabled(false);

        delivery_locations_addresses?.setLayoutManager(linearLayoutManager)
        delivery_locations_addresses?.setAdapter(addressRecyclerAdapters)
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
                    Toast.makeText(activity!!, "Permission denied", Toast.LENGTH_SHORT).show();
                }
            }

        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SearchAddressFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SearchAddressFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
