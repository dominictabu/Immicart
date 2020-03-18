package com.andromeda.immicart.delivery.search.algolia

import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.graphics.Point
import android.graphics.drawable.BitmapDrawable
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.algolia.instantsearch.helper.android.highlighting.toSpannedString
import com.andromeda.immicart.R
import com.andromeda.immicart.delivery.DeliveryCart
import com.andromeda.immicart.delivery.ProductDetailActivity
import com.andromeda.immicart.delivery.search.NetworkState
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_product.view.*
import kotlinx.android.synthetic.main.item_product.view.normal_price
import kotlinx.android.synthetic.main.item_product.view.offer_price
import kotlinx.android.synthetic.main.item_product.view.product_name
import kotlinx.android.synthetic.main.item_product.view.product_picture
import kotlinx.android.synthetic.main.item_product_in_cart.view.*
import java.text.DecimalFormat
import kotlin.math.roundToInt

var TAG = "ProductAdapter"

class ProductAdapter() : PagedListAdapter<Product, ProductViewHolder>(ProductAdapter) {

//    private var networkState: NetworkState? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_product, parent, false)

        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = getItem(position)
        Log.d(TAG, "onBindViewHolder Product $product and postion $position")
        if (product != null) holder.bind(product)
    }



    companion object : DiffUtil.ItemCallback<Product>() {

        override fun areItemsTheSame(
            oldItem: Product,
            newItem: Product
        ): Boolean {
            return oldItem::class == newItem::class
        }

        override fun areContentsTheSame(
            oldItem: Product,
            newItem: Product
        ): Boolean {
            return oldItem.name == newItem.name
        }
    }
}

class ProductViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

    fun bind(product: Product) {
        Log.d(TAG, "Holder called $product")
//        view.itemName.text = product.name
        view.product_name.text = product.highlightedName?.toSpannedString() ?: product.name
        view.offer_price.text = "KES ${product.price}"
        Glide.with(view.context).load(product.image).into(view.product_picture)
    }
}



class SearchProductAdapter (val storeId_: String,val context: Context, val addQuantityClickListener: (DeliveryCart, Int) -> Unit) :
    PagedListAdapter<DeliveryCartSearch, RecyclerView.ViewHolder>(SearchProductAdapter) {

    var categoriesPr: ArrayList<DeliveryCartSearch> = ArrayList()
    lateinit var changeCartItemNumberPopUp: PopupWindow
//    lateinit var storeId_: String

    private var TAG = "SearchProductAdapter"


//    //CArt Items
//    fun setFoundItems(items : List<DeliveryCart>) {
////        foundItems = items as ArrayList<DeliveryCart>
//
//        //Haha setting found to true
//        categoriesPr!!.filter { items.contains(it)}.forEach {
//            it.isInCart = true
//        }
//
//        notifyDataSetChanged()
//    }


    companion object : DiffUtil.ItemCallback<DeliveryCartSearch>() {

        override fun areItemsTheSame(
            oldItem: DeliveryCartSearch,
            newItem: DeliveryCartSearch
        ): Boolean {
            return oldItem::class == newItem::class
        }

        override fun areContentsTheSame(
            oldItem: DeliveryCartSearch,
            newItem: DeliveryCartSearch
        ): Boolean {
            return oldItem.key == newItem.key
        }
    }

    fun updateList(items : ArrayList<DeliveryCartSearch>) {
        categoriesPr = items
        notifyDataSetChanged()

    }

    fun updateCartItems(items: ArrayList<DeliveryCart>) {


    }

//    // Gets the number of animals in the list
//    override fun getItemCount(): Int {
//        return categoriesPr!!.size
//    }

    // Inflates the item views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        Log.d(TAG, "onCreateViewHolder called")

        when (viewType) {
            R.layout.item_product -> return NotInCartItemsAdapterViewHolder(LayoutInflater.from(context).inflate(R.layout.item_product, parent, false))
            R.layout.item_product_in_cart -> return CartItemsAdapterViewHolder(LayoutInflater.from(context).inflate(R.layout.item_product_in_cart, parent, false))
            else -> { // Note the block
                return CartItemsAdapterViewHolder(LayoutInflater.from(context).inflate(R.layout.item_product, parent, false))            }
        }

    }

    // Binds each animal in the ArrayList to a view
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        Log.d(TAG, "onBindViewHolder called")
        if (getItemViewType(position) == R.layout.item_product) {
            Log.d(TAG, "onBindViewHolder getItemViewType of type R.layout.item_product")

            val viewHolder = holder as NotInCartItemsAdapterViewHolder
            val product = getItem(position)
            product?.let {
                viewHolder.bindItem(it)
            }

        } else if (getItemViewType(position) == R.layout.item_product_in_cart) {
            Log.d(TAG, "onBindViewHolder getItemViewType of type R.layout.item_product_in_cart")
            val viewHolder = holder as CartItemsAdapterViewHolder
//            viewHolder.bindItem(categoriesPr.get(position))
            val product = getItem(position)
            product?.let {
                viewHolder.bindItem(it)
            }

        }

    }

    internal fun showChangeCartItemNumberPopUp(context: Context, p: Point, currentQuantity: String, quantityTv: TextView?, addQuantityClickListener: (DeliveryCart, Int) -> Unit, cart: DeliveryCart) {
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
            quantityTv?.text = currentQuantityInt.toString()
            cart.quantity = currentQuantityInt

            Log.d(TAG, "Pop up dismissed")
            addQuantityClickListener(cart, currentQuantityInt)
            Log.d(TAG, "addQuantityClickListener set.")

        }

//         val newQuantity = (quantityTv.text).toString().toInt()
//         return currentQuantityInt

    }


    override fun getItemViewType(position: Int): Int {

        Log.d(TAG, "getItemViewType called")

        val isInCart = getItem(position)?.isInCart
        //categoriesPr.get(position).isInCart

        isInCart?.let {
            if(isInCart!!) {
                return R.layout.item_product_in_cart
            } else {
                return R.layout.item_product
            }
        }

        return R.layout.item_product

    }

    private val PRODUCT_ID = "PRODUCT_ID"
    private val STORE_ID = "STORE_ID"

    inner class CartItemsAdapterViewHolder (view: View) : RecyclerView.ViewHolder(view) {

        // Holds the TextView that will add each animal to
        val product_image = view.product_picture
        val offer_price = view.offer_price
        val normal_price = view.normal_price
        val amount_off = view.cart_save_amt
        val product_name = view.product_name
        fun bindItem(cartItem: DeliveryCartSearch) {
            Log.d(TAG, "CartItemsAdapterViewHolder bindItem called")

            product_name.text = cartItem.highlightedName?.toSpannedString() ?: cartItem.name

            val deliveryCart = DeliveryCart(cartItem.key, cartItem.barcode, cartItem.name,cartItem.category, cartItem.offerPrice,cartItem.normalPrice, cartItem.quantity, cartItem.image_url)
            normal_price.setPaintFlags(normal_price.getPaintFlags() or Paint.STRIKE_THRU_TEXT_FLAG)

            val offerPrice_ =  cartItem.offerPrice
            val normalPrice_ = cartItem.normalPrice

            val save = normalPrice_ - offerPrice_

            val formatter = DecimalFormat("#,###,###");
            val saveFormattedString = formatter.format(save.toInt());
            val priceFormattedString = formatter.format(offerPrice_.toInt());
            val normalPriceFormattedString = formatter.format(normalPrice_.toInt());



            Glide.with(itemView.context).load(cartItem.image_url).into(product_image)

            itemView.setOnClickListener {
                val intent = Intent(itemView.context, ProductDetailActivity::class.java)
                intent.putExtra(PRODUCT_ID, cartItem.key)
                intent.putExtra(STORE_ID, storeId_)
                itemView.context.startActivity(intent)
            }
            amount_off.text = "Save KES $saveFormattedString"
            offer_price.text = "KES $priceFormattedString"
            normal_price.text = "KES $normalPriceFormattedString"

//            product_name.text = cartItem.name
            itemView.badge_qty.text = cartItem.quantity.toString()

            itemView.badge_qty.setOnClickListener {
                val currentQuantity = itemView.badge_qty.text

                val location = IntArray(2)
                itemView.badge_qty!!.getLocationOnScreen(location)
                val x = location[0]
                val y = location[1]
                val point = Point(x, y)
                showChangeCartItemNumberPopUp(
                    itemView.context,
                    point,
                    currentQuantity as String,
                    itemView.badge_qty,
                    addQuantityClickListener,
                    deliveryCart
                )
//                val newQuantity = (itemView.quantity_tv.text).toString().toInt()
//                Log.d(TAG, "newQuantity textView: ${newQuantity}")

            }

        }

    }

    inner class NotInCartItemsAdapterViewHolder (view: View) : RecyclerView.ViewHolder(view) {
        // Holds the TextView that will add each animal to
        val product_image = view.product_picture
        val offer_price = view.offer_price
        val normal_price = view.normal_price
        val amount_off = view.save_amt
        val product_name = view.product_name
        fun bindItem(cartItem: DeliveryCartSearch) {
            Log.d(TAG, "CartItemsAdapterViewHolder bindItem called")

            val deliveryCart = DeliveryCart(cartItem.key, cartItem.barcode, cartItem.name, cartItem.category,cartItem.offerPrice, cartItem.normalPrice, cartItem.quantity, cartItem.image_url)
            normal_price.setPaintFlags(normal_price.getPaintFlags() or Paint.STRIKE_THRU_TEXT_FLAG)

            val offerPrice_ =  cartItem.offerPrice
            val normalPrice_ = cartItem.normalPrice

            val save = normalPrice_ - offerPrice_

            val formatter = DecimalFormat("#,###,###");
            val saveFormattedString = formatter.format(save.toInt());
            val priceFormattedString = formatter.format(offerPrice_.toInt());
            val normalPriceFormattedString = formatter.format(normalPrice_.toInt());

            Glide.with(itemView.context).load(cartItem.image_url).into(product_image)

            itemView.setOnClickListener {
                val intent = Intent(itemView.context, ProductDetailActivity::class.java)
                intent.putExtra(PRODUCT_ID, cartItem.key)
                intent.putExtra(STORE_ID, storeId_)

                itemView.context.startActivity(intent)
            }

            amount_off.text = "Save KES $saveFormattedString"
            offer_price.text = "KES $priceFormattedString"
            normal_price.text = "KES $normalPriceFormattedString"
            product_name.text = cartItem.highlightedName?.toSpannedString() ?: cartItem.name
//        itemView.quantity_tv.text = cartItem.quantity.toString()

            itemView.addToCartBtn.setOnClickListener {
                val currentQuantity = "0"

                val location = IntArray(2)
                itemView.addToCartBtn!!.getLocationOnScreen(location)
                val x = location[0]
                val y = location[1]
                val point = Point(x, y)
                showChangeCartItemNumberPopUp(
                    itemView.context,
                    point,
                    currentQuantity as String,
                    null,
                    addQuantityClickListener,
                    deliveryCart
                )
//                val newQuantity = (itemView.quantity_tv.text).toString().toInt()
//                Log.d(TAG, "newQuantity textView: ${newQuantity}")

            }



        }

    }

}