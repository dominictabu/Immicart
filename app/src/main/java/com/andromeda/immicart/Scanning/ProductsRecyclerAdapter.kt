package com.andromeda.immicart.Scanning


import android.content.Context
import android.graphics.Point
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.andromeda.immicart.R
import com.andromeda.immicart.Scanning.persistence.Cart
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_scanned_product.view.*
import android.R.id.icon2
import android.util.Log
import android.widget.ImageButton






 class ProductsRecyclerAdapter(val addQuantityClickListener: (Cart, Int) -> Unit, val removeItemClickListener: (Cart) -> Unit): ListAdapter<Cart, ProductsRecyclerAdapter.ViewHolder>(ProductsDiffCallback())  {
     private val TAG = "ProductsRecyclerAdapter"

     lateinit var changeCartItemNumberPopUp: PopupWindow

     lateinit var mRecyclerView: RecyclerView


     override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductsRecyclerAdapter.ViewHolder{

            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_scanned_product, parent, false)
            //inflate your layout and pass it to view holder
            return ViewHolder(view)

    }


    override fun onBindViewHolder(holder: ProductsRecyclerAdapter.ViewHolder, position: Int) {

//        val _clickListener : (Cart) -> Unit = clicklistener()
        holder.bind(getItem(position), addQuantityClickListener, removeItemClickListener)
//        holder.itemView.remove_item.setOnClickListener {
//            notifyItemRemoved(position)
//        }


    }

     override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
         super.onAttachedToRecyclerView(recyclerView)

         mRecyclerView = recyclerView
     }

     internal fun setCartItems(cartItems: List<Cart>) {
         submitList(cartItems)
         mRecyclerView.scrollToPosition(itemCount - 1);

     }

     override fun submitList(list: List<Cart>?) {
         super.submitList(list)

     }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(cart: Cart, clickListener: (Cart, Int) -> Unit, removeItemClickListener: (Cart) -> Unit) {
            itemView.description_product.text = cart.name
            itemView.quantity_tv.text = cart.quantity.toString()

            val price = cart.quantity * cart.price

            itemView.price_tv.text = "KES " + price.toString()

            Glide.with(itemView.context).load(cart.image_url).into(itemView.scanned_product_image)

            itemView.quantity_tv.setOnClickListener {
                val currentQuantity = itemView.quantity_tv.text
                val location = IntArray(2)
                itemView.quantity_tv!!.getLocationOnScreen(location)
                val x = location[0]
                val y = location[1]
                val point = Point(x, y)
                showChangeCartItemNumberPopUp(itemView.context, point, currentQuantity as String, itemView.quantity_tv, addQuantityClickListener, cart)
//                val newQuantity = (itemView.quantity_tv.text).toString().toInt()
//                Log.d(TAG, "newQuantity textView: ${newQuantity}")



//                clickListener(cart, newQuantity)
            }

            itemView.remove_item.setOnClickListener{
                removeItemClickListener(cart)
            }
        }
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
}
        class ProductsDiffCallback : DiffUtil.ItemCallback<Cart>() {
            override fun areItemsTheSame(oldItem: Cart, newItem: Cart): Boolean {
                return oldItem?._id == newItem?._id
            }

            override fun areContentsTheSame(oldItem: Cart, newItem: Cart): Boolean {
                return oldItem == newItem
            }
        }