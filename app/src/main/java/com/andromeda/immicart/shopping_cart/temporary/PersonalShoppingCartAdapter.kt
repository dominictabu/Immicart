package com.andromeda.immicart.shopping_cart.temporary

//import kotlinx.android.synthetic.main.content_payment.view.
import android.content.Context
import android.graphics.Paint
import android.graphics.Point
import android.graphics.drawable.BitmapDrawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.andromeda.immicart.R
import com.andromeda.immicart.Scanning.persistence.Cart
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.personal_cart_item.view.*
import java.text.DecimalFormat


class PersonalShoppingCartAdapter(var context: Context, val changeQuantityClickListener: (Cart, Int) -> Unit,  val removeItemClickListener: (Cart) -> Unit) :
     ListAdapter<Cart, PersonalShoppingCartAdapter.ViewHolder>(ProductsDiffCallback()) {

    lateinit var changeCartItemNumberPopUp: PopupWindow

    /*
    * Later, we need to move this to the binding library
    * And apply MVVM architecture
    * */

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layout = LayoutInflater.from(context).inflate(R.layout.personal_cart_item, parent, false)
        return ViewHolder(layout)
    }

//    override fun getItemCount(): Int = cartItems.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItem(getItem(position), removeItemClickListener)

    }

    internal fun showChangeCartItemNumberPopUp(context: Context, p: Point, currentQuantity: String, quantityTv: TextView, clickListener: (Cart, Int) -> Unit, cart: Cart) {
        // Inflate the popup_layout.xml
        //        LinearLayout viewGroup = (LinearLayout) context.findViewById(R.id.);
        val layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val layout = layoutInflater.inflate(R.layout.add_to_cart_pop_up, null)

        // Creating the PopupWindow
        changeCartItemNumberPopUp = PopupWindow(context)
        changeCartItemNumberPopUp.setContentView(layout)
        changeCartItemNumberPopUp.setWidth(LinearLayout.LayoutParams.WRAP_CONTENT)
        changeCartItemNumberPopUp.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT)
        changeCartItemNumberPopUp.setFocusable(true)
        changeCartItemNumberPopUp.setElevation(20f)


        // Some offset to align the popup a bit to the left, and a bit down, relative to button's position.
        //        int OFFSET_X = -20;
        //        int OFFSET_Y = 95;

        val OFFSET_X = 0
        val OFFSET_Y = 0

        // Clear the default translucent background
        changeCartItemNumberPopUp.setBackgroundDrawable(BitmapDrawable())

        // Displaying the popup at the specified location, + offsets.
        changeCartItemNumberPopUp.showAtLocation(layout, Gravity.NO_GRAVITY, p.x + OFFSET_X, p.y + OFFSET_Y)


        // Getting a reference to Close button, and close the popup when clicked.
        val add = layout.findViewById<View>(R.id.add_quantity) as ImageButton
        val quantityText = layout.findViewById<View>(R.id.quantityTv) as TextView
        quantityText.text = currentQuantity
        var currentQuantityInt: Int = currentQuantity.toInt()
        val removeBtn = layout.findViewById<View>(R.id.reduceQuantityBtn) as ImageButton

        if (currentQuantityInt <= 1) {
            (removeBtn as ImageButton).setImageResource(R.drawable.ic_delete_primary_color_24dp)

        } else {
            (removeBtn as ImageButton).setImageResource(R.drawable.ic_remove_primary_color_24dp)

        }

        add.setOnClickListener {

            currentQuantityInt++
            quantityText.text = currentQuantityInt.toString()
            (removeBtn as ImageButton).setImageResource(R.drawable.ic_remove_primary_color_24dp)

        }


        removeBtn.setOnClickListener {
            if (currentQuantityInt > 1) {
                currentQuantityInt--
                quantityText.text = currentQuantityInt.toString()

            } else {
                (removeBtn as ImageButton).setImageResource(R.drawable.ic_delete_primary_color_24dp)
                currentQuantityInt--
                changeCartItemNumberPopUp.dismiss()


            }

        }

        changeCartItemNumberPopUp.setOnDismissListener {
            quantityTv.text = currentQuantityInt.toString()
            clickListener(cart, currentQuantityInt)

        }


//         val newQuantity = (quantityTv.text).toString().toInt()
//         return currentQuantityInt



    }



    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bindItem(cartItem: Cart, removeItemClickListener: (Cart) -> Unit) {

            itemView.itemName.text = cartItem.name
            itemView.itemDescription.text = "2kg"

            val productImage = cartItem.image_url
//            TODO update the names?

            val Oldprice = cartItem.quantity * cartItem.price
            val _formatter = DecimalFormat("#,###,###");
            val OldpriceFormattedString = _formatter.format(Oldprice.toInt());
            itemView.itemOldPrice.text =  "KES " + OldpriceFormattedString
            itemView.itemOldPrice.paintFlags = itemView.itemOldPrice.getPaintFlags() or Paint.STRIKE_THRU_TEXT_FLAG

            val price_ = cartItem.quantity * cartItem.price
            val formatter = DecimalFormat("#,###,###");
            val priceFormattedString = formatter.format(price_.toInt());

            itemView.itemNewPrice.text = "KES " + priceFormattedString

            itemView.itemNumber.setText(cartItem.quantity.toString())
            Glide.with(itemView.context).load(productImage).into(itemView.cartImage)

            itemView.itemNumber.setOnClickListener {
                val currentQuantity = itemView.itemNumber.text
                val location = IntArray(2)
                itemView.itemNumber!!.getLocationOnScreen(location)
                val x = location[0]
                val y = location[1]
                val point = Point(x, y)
                showChangeCartItemNumberPopUp(
                    itemView.context,
                    point,
                    currentQuantity as String,
                    itemView.itemNumber,
                    changeQuantityClickListener,
                    cartItem
                )
//                val newQuantity = (itemView.quantity_tv.text).toString().toInt()
//                Log.d(TAG, "newQuantity textView: ${newQuantity}")

            }

                itemView.deleteText.setOnClickListener{
                    removeItemClickListener(cartItem)
                }


//            How to access an edittext

//            itemView.etAmount.text = cartItem.amount

        }

    }
}

class ProductsDiffCallback : DiffUtil.ItemCallback<Cart>() {
    override fun areItemsTheSame(oldItem: Cart, newItem: Cart): Boolean {
        return oldItem?._id == newItem?._id
    }

    override fun areContentsTheSame(oldItem: Cart, newItem: Cart): Boolean {
        return oldItem == newItem
    }
}