package com.andromeda.immicart.delivery.checkout


import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.andromeda.immicart.R
import com.andromeda.immicart.checkout.DeliveryCartItemsAdapter_
import com.andromeda.immicart.delivery.DeliveryCart
import com.andromeda.immicart.delivery.Utils.MyDatabaseUtil
import com.andromeda.immicart.delivery.delivery_location.PickDeliveryAddressActivity
import com.andromeda.immicart.delivery.furniture.PaymentMethod
import com.andromeda.immicart.delivery.furniture.PaymentMethodsActivity
import com.andromeda.immicart.delivery.persistence.DeliveryLocation
import com.andromeda.immicart.networking.Model
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_delivery_details_furnituire.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.DecimalFormat

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [DeliveryDetailsFurnituireFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class DeliveryDetailsFurnituireFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var viewModel: DeliveryCartViewModel
    private var storeSubtotal: Int? = 0
    private var totals: Int? = null
    private  var deliveryLocation: DeliveryLocation? = null

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
        return inflater.inflate(R.layout.fragment_delivery_details_furnituire, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProviders.of(activity!!).get(DeliveryCartViewModel::class.java)
        viewModel.allDeliveryItems().observe(this, androidx.lifecycle.Observer { items ->
            items?.let {
                loadItems(it)
                number_of_items_vertical_top_rv?.text = "${it.size} Items"
                var total = 0
                it.forEach {
                    val quantity = it.quantity
                    val unitPrice = it.offerPrice.toInt()
                    val subtotal = quantity * unitPrice
                    total += subtotal
                }
                storeSubtotal= total

            }
        })
        getCurrentDeliveryLocation()

        change_delivery_location?.setOnClickListener {
            startActivity(Intent(activity!!, PickDeliveryAddressActivity::class.java))
        }

        select_address?.setOnClickListener {
            startActivity(Intent(activity!!, PickDeliveryAddressActivity::class.java))
        }

        selected_payments?.setOnClickListener {
            startActivityForResult(Intent(activity!!, PaymentMethodsActivity::class.java), METHOD_REQUEST_CODE)
        }

        select_payments?.setOnClickListener {
            startActivityForResult(Intent(activity!!, PaymentMethodsActivity::class.java), METHOD_REQUEST_CODE)

        }

        }


    val METHOD = "METHOD"
    val METHOD_REQUEST_CODE = 1001
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == METHOD_REQUEST_CODE && data!= null) {
            val method = data.getSerializableExtra(METHOD) as PaymentMethod
            select_payments?.visibility = View.GONE
            selected_payments?.visibility = View.VISIBLE
            method_name?.text = method.name
            Glide.with(requireActivity()).load(method.link).into(method_image)

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
                    select_address?.visibility = View.GONE
                    address_ll?.visibility = View.VISIBLE
                    address_one?.text = location.name
                    address_two?.text = location.address

//                    getDeliveryCharges()

//                    currentStore?.let {
//                        val from_name = it.storeName!!
//                        val storeLatLng = convertLatLng(it.storeLatLng!!)
//                        val from_lat = storeLatLng.latitude
//                        val from_lon = storeLatLng.longitude
//
//                        val locationLatLng = convertLatLng(location.latLng!!)
//                        val to_lat = locationLatLng.latitude
//                        val to_lon = locationLatLng.longitude
//                    }

//                        makeSendyDeliveryRequest(from_name, from_lat, from_lon, location.name!!, to_lat, to_lon)


                } else {
                    totalWithoutDeliveryLoc()
                    address_ll?.visibility = View.GONE
                    select_address?.visibility = View.VISIBLE
                    Log.d(TAG, "location  null")

                }
            }

        })
    }

    fun totalWithoutDeliveryLoc() {

        delivery_fee?.text = "KES $deliveryFee"



        val formatter = DecimalFormat("#,###,###");

        val totalCost = storeSubtotal!!!! + deliveryFee!!
        Log.d(TAG, "totalCost : $totalCost")
        val totalCostFormattedString = formatter.format(totalCost);
        total_text_view?.text = "KES $totalCostFormattedString"
        totals = totalCost
    }

    fun loadItems(cartItems : List<DeliveryCart>) {
        val linearLayoutManager = LinearLayoutManager(activity!!, RecyclerView.VERTICAL, false)

        _cart_items_recycler?.layoutManager = linearLayoutManager
        val deliveryCartItemsAdapter  = DeliveryCartItemsAdapter_(cartItems,activity!!)
        _cart_items_recycler?.adapter = deliveryCartItemsAdapter
        _cart_items_recycler?.isNestedScrollingEnabled = false
    }


    private var deliveryFee: Int? = 0
    private  var TAG: String = "DeliveryDetailsFragFur"

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

//                val serviceCharge = 0.05* storeSubtotal!!
//                serviceFee = serviceCharge.toInt()
//                service_fee_amount.text = "KES "+ serviceCharge.toInt()
                val formatter = DecimalFormat("#,###,###");

                val totalCost = storeSubtotal!! + deliveryFee!!
                Log.d(TAG, "totalCost : $totalCost")
                val totalCostFormattedString = formatter.format(totalCost);
                total_text_view?.text = "KES $totalCostFormattedString"
                totals = totalCost.toInt()

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
         * @return A new instance of fragment DeliveryDetailsFurnituireFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            DeliveryDetailsFurnituireFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
