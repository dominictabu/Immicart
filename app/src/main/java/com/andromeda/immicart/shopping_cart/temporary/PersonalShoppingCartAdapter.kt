package com.andromeda.immicart.shopping_cart.temporary

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.andromeda.immicart.R
import kotlinx.android.synthetic.main.content_payment.view.*
import kotlinx.android.synthetic.main.personal_cart_item.view.*

class PersonalShoppingCartAdapter(var context: Context, var cartItems: List<PersonalCart>) :
    RecyclerView.Adapter<PersonalShoppingCartAdapter.ViewHolder>() {

    /*
    * Later, we need to move this to the binding library
    * And apply MVVM architecture
    * */

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layout = LayoutInflater.from(context).inflate(R.layout.personal_cart_item, parent, false)
        return ViewHolder(layout)
    }

    override fun getItemCount(): Int = cartItems.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItem(cartItems[position])

    }


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bindItem(cartItem: PersonalCart) {

            itemView.itemName.text = cartItem.itemName
            itemView.itemDescription.text = cartItem.description

//            TODO update the names?
            itemView.itemPrice.text = cartItem.discountedPrice
            itemView.cancelledPrice.text = cartItem.sellingPrice

//            How to access an edittext

//            itemView.etAmount.text = cartItem.amount

        }

    }
}