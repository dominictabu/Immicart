package com.andromeda.immicart.delivery

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.andromeda.immicart.R

import android.content.Context
import android.content.Intent
import android.graphics.drawable.InsetDrawable
import android.os.Handler
import android.util.Log
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.TypeFilter
import java.util.*
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.gms.common.api.ApiException
import kotlinx.android.synthetic.main.content_pick_delivery_location.*
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper
import java.lang.reflect.Array
import kotlin.collections.ArrayList



class PickDeliveryLocationActivity : AppCompatActivity(), PlacesAutoCompleteAdapter.OnItemClickListener {
    override fun OnItemClick(place: com.andromeda.immicart.delivery.Place?) {

        place?.let {
            latestPlace = it

        }
        address_one.text = place?.name
        address_two.text = place?.address
    }


    var AUTOCOMPLETE_REQUEST_CODE = 1
    val TAG = "EnterLocationAct"

    private lateinit var pickDeliveryLocationViewModel: PickDeliveryLocationViewModel
    private lateinit var latestPlace: com.andromeda.immicart.delivery.Place


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

        pickDeliveryLocationViewModel = ViewModelProviders.of(this).get(PickDeliveryLocationViewModel::class.java)

        pickDeliveryLocationViewModel.allDeliveryLocations().observe(this, androidx.lifecycle.Observer {

            it?.let {

                if(it.size > 0) {

                    val place = it[0]

                    address_one.text = place.name
                    address_two.text = place.address
                    latestPlace = place
                }

            }

        })
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
        val placesClient = Places.createClient(this)

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

                    val placesArrayList: ArrayList<com.andromeda.immicart.delivery.Place> = ArrayList()
                    for (prediction in response.autocompletePredictions) {

                        Log.i(TAG, "Place ID: ${prediction.placeId}")

                        val placeID : String = prediction.placeId

                        Log.i(TAG, "Prdiction: " + prediction)

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

                            val placeItem = Place(placeID, place, address, fullText)
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

            pickDeliveryLocationViewModel.insertDeliveryLocation(latestPlace)
        }


    }



    fun initializePlacesAutoCompleteRecycler(places : ArrayList<com.andromeda.immicart.delivery.Place>) {
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

        val placesAutoCompleteAdapter = PlacesAutoCompleteAdapter(places, this@PickDeliveryLocationActivity, this@PickDeliveryLocationActivity)

        recycler_items.adapter = placesAutoCompleteAdapter

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

    }

}
