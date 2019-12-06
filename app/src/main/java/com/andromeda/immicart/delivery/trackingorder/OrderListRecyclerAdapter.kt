package com.andromeda.immicart.delivery.trackingorder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.andromeda.immicart.R
import com.andromeda.immicart.delivery.PlaceOrder
import com.andromeda.immicart.networking.Model
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_order.view.*


class OrderListRecyclerAdapter(val orders : ArrayList<PlaceOrder>) : RecyclerView.Adapter<OrderListRecyclerAdapter.OrderListRecyclerViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderListRecyclerViewHolder {
        return OrderListRecyclerViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_order, parent, false))
    }

    override fun getItemCount(): Int {
        return orders.size
    }

    override fun onBindViewHolder(holder: OrderListRecyclerViewHolder, position: Int) {

        if(orders.size > 0) {
            val order = orders[position]
            holder.bindItem(order)

        }
    }


    inner class OrderListRecyclerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItem(order: PlaceOrder) {
            itemView.store_name.text = order.store?.name
//            itemView.products_single_liner.text = order
            itemView.total_price.text = order.storeFee.toString()
            Glide.with(itemView.context).load(order.store?.image_url).into(itemView.store_image)


            itemView.setOnClickListener {

            }

        }

    }
}