package com.andromeda.immicart.delivery.trackingorder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.andromeda.immicart.R
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_order.view.*


class OrderListRecyclerAdapter(val orders : ArrayList<OrderObject>, val clicklistener : (OrderObject) -> Unit) : RecyclerView.Adapter<OrderListRecyclerAdapter.OrderListRecyclerViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderListRecyclerViewHolder {

        when (viewType) {
            R.layout.item_order -> return OrderListRecyclerViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.item_order,
                    parent,
                    false
                )
            )
            R.layout.item_order_completed -> return OrderListRecyclerViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.item_order_completed,
                    parent,
                    false
                )
            )
            else -> { // Note the block
                return OrderListRecyclerViewHolder(
                    LayoutInflater.from(parent.context).inflate(
                        R.layout.item_order,
                        parent,
                        false
                    )
                )
            }
        }
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
    override fun getItemViewType(position: Int): Int {

        val isCompleted = orders.get(position).orderStatus?.completed
        //categoriesPr.get(position).isInCart
        val isPickUp = orders.get(position).deliveryMode?.isPickUp
        val isDelivery = orders.get(position).deliveryMode?.isDelivery

        isDelivery?.let {

            isPickUp?.let {
                isCompleted?.let {

                    if (it && isPickUp!!) {
                        return R.layout.item_pick_up_completed_order

                    } else if (it && isDelivery!!) {
                        return R.layout.item_order_completed

                    } else if (!it && isPickUp!!) {
                        return R.layout.item_pickup_order
                    } else if (!it && isDelivery!!) {
                        return R.layout.item_order
                    }
                }
            }
        }

//        isCompleted?.let {
//            if(it) {
//                return R.layout.item_order_completed
//            } else {
//                return R.layout.item_order
//            }
//
//
//        }
        return -1

//        return R.layout.item_order

    }


    inner class OrderListRecyclerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItem(order: OrderObject) {
            itemView.store_name.text = order.store?.name
            itemView.products_single_liner.text = "${order?.items?.size} items"
            itemView.total_price.text = "KES ${order.payments?.storeSubtotal.toString()}"
            Glide.with(itemView.context).load(order.store?.logoURL).into(itemView.store_image)



            itemView.setOnClickListener {

                clicklistener(order)

            }

        }

    }
}