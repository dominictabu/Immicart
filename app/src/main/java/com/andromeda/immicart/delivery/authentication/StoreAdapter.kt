package com.andromeda.immicart.delivery.authentication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.andromeda.immicart.R
import com.andromeda.immicart.delivery.choose_store.Store
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_store_item.view.*
import java.util.*


class StoreListRecyclerAdapter(val stores: ArrayList<Store>, val storeItemCLickListener: (Store) -> Unit) : RecyclerView.Adapter<StoreListRecyclerAdapter.StoreListRecyclerViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoreListRecyclerViewHolder {
        return StoreListRecyclerViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_store_item, parent, false))
    }

    override fun getItemCount(): Int {
        return stores.size
    }

    override fun onBindViewHolder(holder: StoreListRecyclerViewHolder, position: Int) {

        if(stores.size > 0) {
            val order = stores[position]
            holder.bindItem(order)



        }
    }


    inner class StoreListRecyclerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItem(store: Store) {
            itemView.store.text = store.name
//            itemView.products_single_liner.text = order
            itemView.location.text = store.address
            Glide.with(itemView.context).load(store.logoUrl).into(itemView.store_image)


            itemView.setOnClickListener {
                storeItemCLickListener(store)


            }

        }

    }
}