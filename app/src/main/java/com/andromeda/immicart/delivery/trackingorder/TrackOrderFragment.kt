package com.andromeda.immicart.delivery.trackingorder


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.andromeda.immicart.R
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import android.graphics.Bitmap
import androidx.core.content.ContextCompat
import android.graphics.drawable.Drawable
import com.google.android.gms.maps.model.BitmapDescriptor
import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.net.Uri
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.andromeda.immicart.checkout.CartImagesAdapter
import com.andromeda.immicart.checkout.DeliveryCartItemsAdapter
import com.andromeda.immicart.delivery.DeliveryCart
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_track_order.*
import java.text.DecimalFormat
import java.util.regex.Pattern


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM_ORDER_ID = "param1"

/**
 * A simple [Fragment] subclass.
 * Use the [TrackOrderFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class TrackOrderFragment : Fragment() , OnMapReadyCallback {

//    var cartItems: List<DeliveryCart> = ArrayList<DeliveryCart>()

    // TODO: Rename and change types of parameters
    private var orderID: String? = null
    private lateinit var ordersViewModel: OrdersViewModel

    private lateinit var map : GoogleMap
    private lateinit var mapView : MapView
    val db = FirebaseFirestore.getInstance()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            orderID = it.getString(ARG_PARAM_ORDER_ID)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_track_order, container, false)

        mapView = view.findViewById(R.id.mapView)
        mapView?.onCreate(savedInstanceState);
        mapView?.getMapAsync(this);

        return view

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        ordersViewModel = ViewModelProviders.of(activity!!).get(OrdersViewModel::class.java)

        ordersViewModel.selectedOrderID.observe(activity!!, Observer {
            getOrder(it)
        })


        call_shopper.setOnClickListener {
            makeCall("0796026997")
        }

        recycler_ll.visibility = View.VISIBLE
        cart_items_ll.visibility = View.GONE

        expand_button.setOnClickListener {
            recycler_ll.visibility = View.GONE
            cart_items_ll.visibility = View.VISIBLE
        }

        collapse_button.setOnClickListener {
            recycler_ll.visibility = View.VISIBLE
            cart_items_ll.visibility = View.GONE
//            loadItems()
        }

        go_back_button.setOnClickListener {
            activity?.let {
                activity!!.finish()
            }
        }
    }



    fun makeCall(mobileNumber: String) {
        val intent : Intent =  Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:$mobileNumber"));
        startActivity(intent);
    }

    override fun onMapReady(googleMap: GoogleMap?) {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.

        googleMap?.let {

            map = googleMap;
            map.getUiSettings().setMyLocationButtonEnabled(false);

//            getOrder()

        }

    }


    fun loadImages(images: List<String>) {
        val linearLayoutManager = LinearLayoutManager(activity!!, RecyclerView.HORIZONTAL, false)
        cart_recycler_view.layoutManager = linearLayoutManager
        val cartImagesAdapter  = CartImagesAdapter(images,activity!!)
        cart_recycler_view.adapter = cartImagesAdapter
    }

    fun loadItems(cartItems : List<DeliveryCart_>) {
        val linearLayoutManager = LinearLayoutManager(activity!!, RecyclerView.VERTICAL, false)

        cart_items_recycler.layoutManager = linearLayoutManager
        val deliveryCartItemsAdapter  = DeliveryCartItemsAdapter(cartItems,activity!!)
        cart_items_recycler.adapter = deliveryCartItemsAdapter
    }


    fun getOrder(orderID : String) {

        orderID?.let {

        db.collection("orders").document(orderID!!)
            .addSnapshotListener { snapshot, e ->


                snapshot?.let {
                    val order = snapshot.toObject(OrderObject::class.java)

                    order?.let {

                        val latLng = it.deliveryAddress?.LatLng
                        latLng?.let {

                            val latLong = convertStringToLatLng(latLng)
                            map.addMarker(
                                MarkerOptions().position(
                                    latLong
                                )
                                    .icon(
                                        bitmapDescriptorFromVector(
                                            activity!!,
                                            R.drawable.ic_green_icon
                                        )
                                    )
                            )

                            map.moveCamera(
                                CameraUpdateFactory.newLatLngZoom(
                                    latLong, 14F
                                )
                            )
                        }


                        val items = it.items
                        val images: ArrayList<String> = ArrayList()

                        items?.let {
                            number_of_items_vertical_rv.text = "${it.size} Items"
                            nuberOfItemsTitle.text = "${it.size} Items"

                            it.forEach {
                                images.add(it.image_url!!)
                            }
                            loadImages(images)
                            loadItems(it)

                            var total = 0
                            it.forEach {
                                val deliveryItem = it
                                it.quantity?.let {
                                    deliveryItem.offerPrice?.let {
                                        val quantity = deliveryItem.quantity!!
                                        val unitPrice = deliveryItem.offerPrice!!.toInt()
                                        val subtotal = quantity * unitPrice
                                        total += subtotal
                                    }
                                }

                            }
                            val formatter = DecimalFormat("#,###,###");
                            val totalFormattedString = formatter.format(total);

                            store_subtotal.setText("KES " + totalFormattedString)


                        }

                        order.store?.let {
                            supermarket_name?.text = "${it.name} subtotal"
                        }



                        order.deliveryAddress?.let {
                            address_tv.text = it.PlaceFullText
                        }

                        order.orderStatus?.let {
                            showOrderProgress(it)
                        }


                    }
                }
            }

        }

    }


    fun showOrderProgress(orderStatus: OrderStatus) {

        if (orderStatus.assigned!! && orderStatus.shopping!! && orderStatus.delivering!! && orderStatus.completed!!) {

            //Completed
            llorderassigned.visibility = View.GONE
            llordershopping.visibility = View.GONE
            llorderdelivering.visibility = View.GONE
            llordercompleted.visibility = View.VISIBLE

        } else if (orderStatus.assigned && orderStatus.shopping!! && orderStatus.delivering!! && !orderStatus.completed!!) {

            //Delivering
            llorderassigned.visibility = View.GONE
            llordershopping.visibility = View.GONE
            llorderdelivering.visibility = View.VISIBLE
            llordercompleted.visibility = View.GONE

        } else if (orderStatus.assigned && orderStatus.shopping!! && !orderStatus.delivering!! && !orderStatus.completed!!) {
            //Shopping
            llorderassigned.visibility = View.GONE
            llordershopping.visibility = View.VISIBLE
            llorderdelivering.visibility = View.GONE
            llordercompleted.visibility = View.GONE

        } else if (orderStatus.assigned && !orderStatus.shopping!! && !orderStatus.delivering!! && !orderStatus.completed!!) {
            //Assigned
            llorderassigned.visibility = View.VISIBLE
            llordershopping.visibility = View.GONE
            llorderdelivering.visibility = View.GONE
            llordercompleted.visibility = View.GONE

        } else if (!orderStatus.assigned && !orderStatus.shopping!! && !orderStatus.delivering!! && !orderStatus.completed!!) {
            llorderassigned.visibility = View.GONE
            llordershopping.visibility = View.GONE
            llorderdelivering.visibility = View.VISIBLE
            llordercompleted.visibility = View.GONE

        }

        }

    private fun bitmapDescriptorFromVector(context: Context, vectorResId: Int): BitmapDescriptor {
        val vectorDrawable = ContextCompat.getDrawable(context, vectorResId)
        vectorDrawable!!.setBounds(0, 0, vectorDrawable.intrinsicWidth, vectorDrawable.intrinsicHeight)
        val bitmap =
            Bitmap.createBitmap(vectorDrawable.intrinsicWidth, vectorDrawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        vectorDrawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }


    fun convertStringToLatLng(latLng: String) : LatLng {
        var from_lat_lng: String? = null
        val m = Pattern.compile("\\(([^)]+)\\)").matcher(latLng)
        while (m.find()) {
            from_lat_lng = m.group(1)
        }
        val gpsVal = from_lat_lng!!.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val lat = java.lang.Double.parseDouble(gpsVal[0])
        val lon = java.lang.Double.parseDouble(gpsVal[1])
        return  LatLng(lat, lon)
    }


    override fun onResume() {
        mapView.onResume()
        super.onResume()
    }


    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment TrackOrderFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String) =
            TrackOrderFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM_ORDER_ID, param1)
                }
            }
    }
}
