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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.andromeda.immicart.R
import com.andromeda.immicart.delivery.Utils.MyLocation
import com.andromeda.immicart.delivery.persistence.CurrentLocation
import com.andromeda.immicart.delivery.persistence.DeliveryLocation
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.type.LatLng
import kotlinx.android.synthetic.main.fragment_confirm_address.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ConfirmAddressFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */

class ConfirmAddressFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var address: String? = null
    private lateinit var pickDeliveryLocationViewModel: PickDeliveryLocationViewModel
    private lateinit var latestPlace: Place
    private var mLocationPermissionGranted : Boolean = false;
    private var locationRequestCode : Int = 1000;
    private lateinit var addressRecyclerAdapters : AddressRecyclerAdapters
    var alldeliveryLocations: List<DeliveryLocation> = ArrayList()
    private lateinit var resultReceiver: AddressResultReceiver
    private var lastKnownLocation: Location? = null
    val TAG = "ConfirmAddressFragment"

    private lateinit var fusedLocationClient: FusedLocationProviderClient

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
        return inflater.inflate(R.layout.fragment_confirm_address, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        pickDeliveryLocationViewModel = ViewModelProviders.of(activity!!).get(PickDeliveryLocationViewModel::class.java)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity!!)


//        addressRecyclerAdapters = AddressRecyclerAdapters({place: CurrentLocation -> setCurrentLocation(place)})
        intializeRecycler()
//
//        pickDeliveryLocationViewModel.allOtherDeliveryLocations.observe(activity!!, Observer {
//
//            alldeliveryLocations = it
//            addressRecyclerAdapters.set_DeliveryLocations(it)
//
//
//        })

        pickDeliveryLocationViewModel.allDeliveryLocations.observe(activity!!, Observer {

            it?.let {

                if(it.size > 0) {

                    val place = it[0]
//                    addressRecyclerAdapters.set_CurrentLocation(place)


                }

            }

        })

        getCurrentLocation()

        add_location_button.setOnClickListener {
            findNavController().navigate(R.id.action_confirmAddressFragment2_to_searchAddressFragment2)
        }

        go_back_button.setOnClickListener {
            activity!!.finish()
        }

        current_location_ll.setOnClickListener {
            if( !radiobutton_current_location.isChecked) {
                radiobutton_current_location.isChecked = true

            }

            address?.let {
                val latLng = LatLng.newBuilder().setLatitude(lastKnownLocation!!.latitude).setLongitude(lastKnownLocation!!.longitude)
                val currentLocation = CurrentLocation("CURRENT_LOCATION", "Current Location", address, address, latLng.toString(), true)

                pickDeliveryLocationViewModel.deleteAllDeliveryLocations()
                pickDeliveryLocationViewModel.insertCurrentDeliveryLocation(currentLocation)
//                addressRecyclerAdapters.set_CurrentLocation(currentLocation)

            }

        }

        save_location_button.setOnClickListener {

            activity!!.finish()

        }


    }

    fun setCurrentLocation(place: CurrentLocation) {
        radiobutton_current_location.isChecked = false
        pickDeliveryLocationViewModel.deleteAllDeliveryLocations()
        pickDeliveryLocationViewModel.insertCurrentDeliveryLocation(place)
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


    fun getCurrentLocation() {

        if (ActivityCompat.checkSelfPermission(activity!!, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(activity!!, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // reuqest for permission
            mLocationPermissionGranted = false;

            requestPermissions( arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                locationRequestCode);

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
                        Toast.makeText(activity!!,
                            R.string.no_geocoder_available,
                            Toast.LENGTH_LONG).show()
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
    val myLocation =  MyLocation();

    override fun onPause() {
        super.onPause()
        myLocation.cancelTimer()

    }


    override fun onResume() {
        super.onResume()
        resultReceiver = AddressResultReceiver(Handler())

    }


    private fun startIntentService(location: Location) {

        val intent = Intent(activity, FetchAddressIntentService::class.java).apply {
            putExtra(FetchAddressIntentService.Constants.RECEIVER, resultReceiver)
            putExtra(FetchAddressIntentService.Constants.LOCATION_DATA_EXTRA, location)
        }
        activity!!.startService(intent)
    }

    fun intializeRecycler() {


        val linearLayoutManager = LinearLayoutManager(activity!!, RecyclerView.VERTICAL, false)
        linearLayoutManager.reverseLayout = true
        linearLayoutManager.stackFromEnd = true
        delivery_locations_addresses?.setNestedScrollingEnabled(false);

        delivery_locations_addresses?.setLayoutManager(linearLayoutManager)
        delivery_locations_addresses?.setAdapter(addressRecyclerAdapters)
    }

    internal inner class AddressResultReceiver(handler: Handler) : ResultReceiver(handler) {

//        private var mReceiver: ResultReceiver


        override fun onReceiveResult(resultCode: Int, resultData: Bundle?) {

            Log.d(TAG, "onReceiveResult called")

            // Display the address string
            // or an error message sent from the intent service.

            val addressOutput = resultData?.getString(FetchAddressIntentService.Constants.RESULT_DATA_KEY) ?: ""

            address = addressOutput
//            TODO displayAddressOutput()
            Log.d(TAG, "Address Output in Fragment: $addressOutput")

            // Show a toast message if an address was found.
            if (resultCode == FetchAddressIntentService.Constants.SUCCESS_RESULT) {
//                showToast(getString(R.string.address_found))

                lastKnownLocation?.let {
                    current_location_address_two?.text = addressOutput

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
         * @return A new instance of fragment ConfirmAddressFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ConfirmAddressFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
