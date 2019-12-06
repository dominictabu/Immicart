package com.andromeda.immicart.delivery

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.andromeda.immicart.R
import com.andromeda.immicart.networking.ImmicartAPIService
import com.andromeda.immicart.networking.Model
import com.facebook.shimmer.ShimmerFrameLayout
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.item_category.view.*
import kotlinx.android.synthetic.main.item_product_in_cart.view.*
import java.text.DecimalFormat

import android.graphics.Paint
import android.graphics.Point
import android.graphics.drawable.BitmapDrawable
import android.view.Gravity
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.item_product.view.*
import kotlinx.android.synthetic.main.item_product.view.normal_price
import kotlinx.android.synthetic.main.item_product.view.offer_price
import kotlinx.android.synthetic.main.item_product.view.product_name
import kotlinx.android.synthetic.main.item_product.view.product_picture
import kotlinx.android.synthetic.main.item_search_suggestion.view.*
import kotlinx.android.synthetic.main.item_subcategory.view.*


val _immicartAPIService by lazy {
    ImmicartAPIService.create()
}
class ProductsAdapter (val context: Context, val addQuantityClickListener: (DeliveryCart, Int) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var categoriesPr: ArrayList<__Product__> = ArrayList()
    lateinit var changeCartItemNumberPopUp: PopupWindow

    private var TAG = "ProductsAdapter"


    fun updateList( items : ArrayList<__Product__>) {
        categoriesPr = items
        notifyDataSetChanged()

    }
    // Gets the number of animals in the list
    override fun getItemCount(): Int {
            return categoriesPr!!.size

    }

    // Inflates the item views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        when (viewType) {
            R.layout.item_product -> return NotInCartItemsAdapterViewHolder(LayoutInflater.from(context).inflate(R.layout.item_product, parent, false))
            R.layout.item_product_in_cart -> return CartItemsAdapterViewHolder(LayoutInflater.from(context).inflate(R.layout.item_product_in_cart, parent, false))
            else -> { // Note the block
                return CartItemsAdapterViewHolder(LayoutInflater.from(context).inflate(R.layout.item_product, parent, false))            }
        }

    }

    // Binds each animal in the ArrayList to a view
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        if (getItemViewType(position) == R.layout.item_product) {
            val viewHolder = holder as NotInCartItemsAdapterViewHolder
            viewHolder.bindItem(categoriesPr.get(position))

        } else if (getItemViewType(position) == R.layout.item_product_in_cart) {
            val viewHolder = holder as CartItemsAdapterViewHolder
            viewHolder.bindItem(categoriesPr.get(position))
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

            val isInCart = categoriesPr.get(position).isInCart
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

    inner class CartItemsAdapterViewHolder (view: View) : RecyclerView.ViewHolder(view) {
        // Holds the TextView that will add each animal to
        val product_image = view.product_picture
        val offer_price = view.offer_price
        val normal_price = view.normal_price
//        val amount_off = view.save_amt
        val product_name = view.product_name
        fun bindItem(cartItem: __Product__) {

            val deliveryCart = DeliveryCart(cartItem.key, cartItem.barcode, cartItem.name, cartItem.offerPrice,cartItem.normalPrice, cartItem.quantity, cartItem.image_url)
            normal_price.setPaintFlags(normal_price.getPaintFlags() or Paint.STRIKE_THRU_TEXT_FLAG)

            val price_ =  cartItem.offerPrice

            val formatter = DecimalFormat("#,###,###");
            val priceFormattedString = formatter.format(price_.toInt());

            Glide.with(itemView.context).load(cartItem.image_url).into(product_image)

            itemView.setOnClickListener {
                val intent = Intent(itemView.context, ProductDetailActivity::class.java)
                intent.putExtra(PRODUCT_ID, cartItem.key)
                itemView.context.startActivity(intent)
            }
            offer_price.text = "KES $priceFormattedString"
            product_name.text = cartItem.name
//        itemView.quantity_tv.text = cartItem.quantity.toString()

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
//        val amount_off = view.save_amt
        val product_name = view.product_name
        fun bindItem(cartItem: __Product__) {

            val deliveryCart = DeliveryCart(cartItem.key, cartItem.barcode, cartItem.name, cartItem.offerPrice, cartItem.normalPrice, cartItem.quantity, cartItem.image_url)
            normal_price.setPaintFlags(normal_price.getPaintFlags() or Paint.STRIKE_THRU_TEXT_FLAG)

            val price_ =  cartItem.offerPrice

            val formatter = DecimalFormat("#,###,###");
            val priceFormattedString = formatter.format(price_.toInt());

            Glide.with(itemView.context).load(cartItem.image_url).into(product_image)

            itemView.setOnClickListener {
                val intent = Intent(itemView.context, ProductDetailActivity::class.java)
                intent.putExtra(PRODUCT_ID, cartItem.key)
                itemView.context.startActivity(intent)
            }
            offer_price.text = "KES $priceFormattedString"
            product_name.text = cartItem.name
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


class CategoryRecyclerAdapter(val storeId: String, val categories: List<__Category__>,  val context: Context, val addQuantityClickListener: (DeliveryCart, Int) -> Unit, val clickListener: (__Category__) -> Unit ): RecyclerView.Adapter<CategoryRecyclerAdapter.CategoryViewHolder>() {

    private val TAG = "CategoryRecyclerAdapter"
    var  deliveryCartItems: ArrayList<DeliveryCart> = ArrayList()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        return CategoryViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_category, parent, false))
    }

    fun updateItems( items : ArrayList<DeliveryCart>) {
        deliveryCartItems = items
        notifyDataSetChanged()

    }
//     fun retrieveCategoryProducts(id: Int, shimmerFrameLayout: ShimmerFrameLayout, adapter: ProductsAdapter) {
//
//         shimmerFrameLayout.stopShimmerAnimation()
//         shimmerFrameLayout.visibility = View.GONE
//         deliveryCartItems.forEach {
//
//         }
//
//
//
//         val retrofitResponse = _immicartAPIService.getCategoryProducts(id)
//        immicartAPIService.getCategoryProducts(id)
//            .subscribeOn(Schedulers.io())
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribe(
//                { result ->
//
//                    Log.d(TAG, "Retrofit Products Results $result")
//
//
//                    result.products?.let {
//
//                        Log.d(TAG, "Product Results $it")
//                        shimmerFrameLayout.stopShimmerAnimation()
//                        shimmerFrameLayout.visibility = View.GONE
//
//                        var listOfProducts = it as ArrayList<Model.Product_>
//
//                        it.forEach {
//                            val product = it
//                            val index = listOfProducts.indexOf(product)
//
//                            deliveryCartItems.forEach {
//                                if(product._id == it._id) {
//                                    product.isInCart = true
//                                    val product__ = Model.Product_(product._id, product.barcode, product.name, product.price, product.quantity, product.image_url, product.id_link, product.code_link, true)
//                                    listOfProducts.set(index, product__)
//
//
//                                }
//                            }
//                        }
//                        adapter.updateList(listOfProducts)
//
//                    }
//
//                },
//
//                { error ->
//                    Log.d(TAG, "Retrofit Error: ${error.message}")
//
//                    //                        showError(error.message)
//                }
//            )
//
//    }

    fun getCategoryProducts(category : String,  shimmerFrameLayout: ShimmerFrameLayout, adapter: ProductsAdapter) {
        val collectionPath = "stores/" + storeId + "/offers"
        val products = FirebaseFirestore.getInstance().collection(collectionPath)
//            .whereEqualTo("categoryOne", category)
            .limit(10)

        var productsArray: ArrayList<__Product__> = ArrayList()
        products.get().addOnSuccessListener { documentSnapshots  ->
            for (document in documentSnapshots) {
                val offer = document.data as HashMap<String, Any>
                val productName = offer["name"] as String
                val deadline = offer["deadline"] as String
                val normalPrice : String = offer["normal_price"] as String
                val offerPrice = offer["offer_price"] as String
                val category = offer["categoryOne"]
                var barcode = offer["barcode"] as String?
                val fileURL = offer["imageUrl"] as String

                val intOfferPrice = offerPrice.toInt()
                val intNormalPrice = normalPrice.toInt()

                if (barcode == null) {
                    barcode = "Not Set"
                }

                val deliveryCart = DeliveryCart(document.id, barcode, productName, intOfferPrice, intNormalPrice, 1, fileURL)
                val product = __Product__(document.id, barcode, productName, intOfferPrice, intNormalPrice, 1, fileURL, false)
                productsArray.add(product)
//                deliveryCartItems.forEach {
//                    if(it.key == deliveryCart.key) {
//                        val product = __Product__(document.id, barcode, productName, intOfferPrice, intNormalPrice, 1, fileURL, true)
//                        productsArray.add(product)
//
//                    } else {
//                        val product = __Product__(document.id, barcode, productName, intOfferPrice, intNormalPrice, 1, fileURL, false)
//                        productsArray.add(product)
//                    }
//                }

                }
            adapter.updateList(productsArray)

            shimmerFrameLayout.stopShimmerAnimation()
            shimmerFrameLayout.visibility = View.GONE

        }
    }


    fun addProductItems(deliveryItems : List<DeliveryCart>, products: List<Model.Product_>) {


    }

    inner class CategoryViewHolder(view : View) : RecyclerView.ViewHolder(view) {

        fun bindItems(category: __Category__) {
            itemView.category_tv.text = category.name

            val parentShimmerLayout = itemView.parentShimmerLayout
            parentShimmerLayout.startShimmerAnimation()
            val categoryId = category.key
            val linearLayoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)

            val recyclerView = itemView.productsRecyclerView
            recyclerView.setNestedScrollingEnabled(false);

            recyclerView.visibility = View.VISIBLE
            recyclerView.setLayoutManager(linearLayoutManager)
            val productsAdapter = ProductsAdapter(context, addQuantityClickListener)
            recyclerView.adapter = productsAdapter
            getCategoryProducts(category.name!!,itemView.parentShimmerLayout, productsAdapter )
//            retrieveCategoryProducts(categoryId, itemView.parentShimmerLayout, productsAdapter)

            itemView.view_all_tv.setOnClickListener {
                clickListener(category)
            }

        }

    }


    override fun getItemCount(): Int {
        return categories.size

    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bindItems(categories.get(position))
    }

}


class SubCategoryRecyclerAdapter(val subCategories : ArrayList<Model.Category_>) : RecyclerView.Adapter<SubCategoryRecyclerAdapter.SubCategoryViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubCategoryViewHolder {
        return SubCategoryViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_subcategory, parent, false))
    }

    override fun getItemCount(): Int {
        return subCategories.size
    }

    override fun onBindViewHolder(holder: SubCategoryViewHolder, position: Int) {

        if(subCategories.size > 0) {
            val subCategory = subCategories[position]
            holder.itemView.subcategory_tv.text = subCategory.name

        }
    }


    inner class SubCategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }
}


class SearchSuggestionsAdapter(val suggestions: ArrayList<Model.Category_>) : RecyclerView.Adapter<SearchSuggestionsAdapter.SearchSuggestionsViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchSuggestionsViewHolder {
        return SearchSuggestionsViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_search_suggestion, parent, false))
    }

    override fun getItemCount(): Int {
        return suggestions.size
    }

    override fun onBindViewHolder(holder: SearchSuggestionsViewHolder, position: Int) {
        if(suggestions.size > 0) {
            val subCategory = suggestions[position]
            holder.itemView.search_sugg_text.text = subCategory.name

        }
    }

    inner class SearchSuggestionsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }
}


