package com.andromeda.immicart.delivery.delivery_location


import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager

import com.andromeda.immicart.R
import com.andromeda.immicart.delivery.persistence.CurrentLocation
import com.andromeda.immicart.delivery.persistence.DeliveryLocation
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
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

    private var lastKnownLocation: Location? = null
    private lateinit var pickDeliveryLocationViewModel: PickDeliveryLocationViewModel
    private lateinit var latestPlace: Place
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var mLocationPermissionGranted : Boolean = false;
    private var locationRequestCode : Int = 1000;
    private lateinit var placesClient: PlacesClient

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
            findNavController().popBackStack()
        }



    }

    override fun OnItemClick(place: Place?) {
        place?.let {

            retrievePlace(it)

        }
    }


    fun retrievePlace(place_: Place) {
        val placeFields : List<com.google.android.libraries.places.api.model.Place.Field>  = arrayListOf(com.google.android.libraries.places.api.model.Place.Field.LAT_LNG, com.google.android.libraries.places.api.model.Place.Field.ADDRESS_COMPONENTS)
        placesClient.fetchPlace(FetchPlaceRequest.newInstance(place_.placeID,placeFields )).addOnSuccessListener {

            val place = it.place
            val deliveryLocation = DeliveryLocation(place_.placeID, place_.name, place_.address,place_.placeFullText, place.latLng.toString())
            val currentDeliveryLocation = CurrentLocation(place_.placeID, place_.name, place_.address,place_.placeFullText, place.latLng.toString())
            pickDeliveryLocationViewModel.deleteAllDeliveryLocations()
            pickDeliveryLocationViewModel.insertCurrentDeliveryLocation(currentDeliveryLocation)
            pickDeliveryLocationViewModel.insertDeliveryLocation(deliveryLocation)
            findNavController().popBackStack()
            Log.d(TAG, "Retrieved Place: $place")
        }
    }


    fun initializePlacesAutoCompleteRecycler(places : ArrayList<Place>) {
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

        recycler_items.adapter = placesAutoCompleteAdapter

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
