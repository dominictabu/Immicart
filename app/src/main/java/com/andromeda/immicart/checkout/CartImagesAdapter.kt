package com.andromeda.immicart.checkout

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.andromeda.immicart.R
import com.andromeda.immicart.Scanning.persistence.Cart
import com.andromeda.immicart.delivery.DeliveryCart
import com.andromeda.immicart.delivery.trackingorder.DeliveryCart_
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_checkout_cart_item.view.*
import kotlinx.android.synthetic.main.item_product_image.view.*
import java.text.DecimalFormat

class CartImagesAdapter(val items : List<String>, val context: Context) : RecyclerView.Adapter<ViewHolder>() {

    // Gets the number of animals in the list
    override fun getItemCount(): Int {
        return items.size
    }

    // Inflates the item views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_product_image, parent, false))
    }

    // Binds each animal in the ArrayList to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Glide.with(context).load(items.get(position)).into(holder.product_image)
    }
}

class ViewHolder (view: View) : RecyclerView.ViewHolder(view) {
    // Holds the TextView that will add each animal to
    val product_image = view.product_image
}

class CartItemsAdapter(val items : List<Cart>, val context: Context) : RecyclerView.Adapter<CartItemsAdapterViewHolder>() {

    // Gets the number of animals in the list
    override fun getItemCount(): Int {
        return items.size
    }

    // Inflates the item views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartItemsAdapterViewHolder {
        return CartItemsAdapterViewHolder(LayoutInflater.from(context).inflate(R.layout.item_checkout_cart_item, parent, false))
    }

    // Binds each animal in the ArrayList to a view
    override fun onBindViewHolder(holder: CartItemsAdapterViewHolder, position: Int) {
        holder.bindItem(items.get(position))
    }
}

class CartItemsAdapterViewHolder (view: View) : RecyclerView.ViewHolder(view) {
    // Holds the TextView that will add each animal to
    val product_image = view.product_image
    fun bindItem(cartItem: Cart) {
        val price_ = cartItem.quantity * cartItem.price
        val formatter = DecimalFormat("#,###,###");
        val priceFormattedString = formatter.format(price_.toInt());

        itemView.price_tv.text = "KES $priceFormattedString"
        itemView.description_product.text = cartItem.name
        itemView.quantity_tv.text = cartItem.quantity.toString()



    }

}

class DeliveryCartItemsAdapter(val items : List<DeliveryCart_>, val context: Context) : RecyclerView.Adapter<DeliveryCartItemsAdapterViewHolder>() {

    // Gets the number of animals in the list
    override fun getItemCount(): Int {
        return items.size
    }

    // Inflates the item views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeliveryCartItemsAdapterViewHolder {
        return DeliveryCartItemsAdapterViewHolder(LayoutInflater.from(context).inflate(R.layout.item_checkout_cart_item, parent, false))
    }

    // Binds each animal in the ArrayList to a view
    override fun onBindViewHolder(holder: DeliveryCartItemsAdapterViewHolder, position: Int) {
        holder.bindItem(items.get(position))
    }
}

class DeliveryCartItemsAdapterViewHolder (view: View) : RecyclerView.ViewHolder(view) {
    // Holds the TextView that will add each animal to
    val product_image = view.product_image
    fun bindItem(cartItem: DeliveryCart_) {
        val price_ = cartItem.quantity!! * cartItem.offerPrice!!
        val formatter = DecimalFormat("#,###,###");
        val priceFormattedString = formatter.format(price_.toInt());

        Glide.with(itemView.context).load(cartItem.image_url).into(itemView.scanned_product_image)

        itemView.price_tv.text = "KES $priceFormattedString"
        itemView.description_product.text = cartItem.name
        itemView.quantity_tv.text = cartItem.quantity.toString()

    }

}

class DeliveryCartItemsAdapter_(val items : List<DeliveryCart>, val context: Context) : RecyclerView.Adapter<DeliveryCartItemsAdapterViewHolder_>() {

    // Gets the number of animals in the list
    override fun getItemCount(): Int {
        return items.size
    }

    // Inflates the item views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeliveryCartItemsAdapterViewHolder_ {
        return DeliveryCartItemsAdapterViewHolder_(LayoutInflater.from(context).inflate(R.layout.item_checkout_cart_item, parent, false))
    }

    // Binds each animal in the ArrayList to a view
    override fun onBindViewHolder(holder: DeliveryCartItemsAdapterViewHolder_, position: Int) {
        holder.bindItem(items.get(position))
    }
}

class DeliveryCartItemsAdapterViewHolder_ (view: View) : RecyclerView.ViewHolder(view) {
    // Holds the TextView that will add each animal to
    val product_image = view.product_image
    fun bindItem(cartItem: DeliveryCart) {
        val price_ = cartItem.quantity!! * cartItem.offerPrice!!
        val formatter = DecimalFormat("#,###,###");
        val priceFormattedString = formatter.format(price_.toInt());
        Glide.with(itemView.context).load(cartItem.image_url).into(itemView.scanned_product_image)

        itemView.price_tv.text = "KES $priceFormattedString"
        itemView.description_product.text = cartItem.name
        itemView.quantity_tv.text = cartItem.quantity.toString()



    }

}