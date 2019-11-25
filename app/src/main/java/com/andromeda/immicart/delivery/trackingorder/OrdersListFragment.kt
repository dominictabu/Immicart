package com.andromeda.immicart.delivery.trackingorder


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.andromeda.immicart.R
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_orders_list.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

/**
 * A simple [Fragment] subclass.
 * Use the [OrdersListFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class OrdersListFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private lateinit var database: DatabaseReference
    private  val TAG = "OrdersListFragment"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
//            param1 = it.getString(ARG_PARAM1)
//            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_orders_list, container, false)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        database = FirebaseDatabase.getInstance().reference
        retrieveOrders()

    }


    fun retrieveOrders() {
        val ordersArray = arrayListOf<Order>()

        // My top posts by number of stars
        val myOrdersQuery = database.child("orders")
        myOrdersQuery.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (postSnapshot in dataSnapshot.children) {
                    // TODO: handle the post
                    val order = postSnapshot.getValue(Order::class.java)
                    order?.let {

                        ordersArray.add(it)
                    }

                }

                initializeRecyclerView(ordersArray)
            }


            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
                // ...
            }
        })

    }


    fun initializeRecyclerView(orders: ArrayList<Order>) {
        recycler_items_orders

        val linearLayoutManager = LinearLayoutManager(activity!!, RecyclerView.VERTICAL, false)
        recycler_items_orders.setNestedScrollingEnabled(false);

        recycler_items_orders.setLayoutManager(linearLayoutManager)
        val orderListRecyclerAdapter = OrderListRecyclerAdapter(orders)
        recycler_items_orders.setAdapter(orderListRecyclerAdapter)

    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment OrdersListFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            OrdersListFragment().apply {
                arguments = Bundle().apply {
//                    putString(ARG_PARAM1, param1)
//                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
