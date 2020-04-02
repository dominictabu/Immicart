package com.andromeda.immicart.delivery

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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.andromeda.immicart.R
import com.andromeda.immicart.networking.ImmicartAPIService
import com.andromeda.immicart.networking.Model
import com.bumptech.glide.Glide
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.item_category.view.*
import kotlinx.android.synthetic.main.item_product.view.*
import kotlinx.android.synthetic.main.item_product.view.normal_price
import kotlinx.android.synthetic.main.item_product.view.offer_price
import kotlinx.android.synthetic.main.item_product.view.product_name
import kotlinx.android.synthetic.main.item_product.view.product_picture
import kotlinx.android.synthetic.main.item_product_in_cart.view.*
import kotlinx.android.synthetic.main.item_search_suggestion.view.*
import kotlinx.android.synthetic.main.item_subcategory.view.*
import java.text.DecimalFormat

val baseURL = "https://us-central1-immicart-2ca69.cloudfunctions.net/"

val _immicartAPIService by lazy {
    ImmicartAPIService.create(baseURL)
}
class ProductsAdapter (val context: Context, val addQuantityClickListener: (DeliveryCart, Int) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var categoriesPr: ArrayList<DeliveryCart> = ArrayList()
    lateinit var changeCartItemNumberPopUp: PopupWindow
    lateinit var storeId_: String

    private var TAG = "ProductsAdapter"

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

    fun updateList( items : ArrayList<DeliveryCart>, storeId: String) {
        categoriesPr = items
        storeId_ = storeId
        notifyDataSetChanged()
        Log.d(TAG, "updateList called : Items : $items")

    }

    fun updateCartItems(items: ArrayList<DeliveryCart>) {

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
    private val STORE_ID = "STORE_ID"

    inner class CartItemsAdapterViewHolder (view: View) : RecyclerView.ViewHolder(view) {
        // Holds the TextView that will add each animal to
        val product_image = view.product_picture
        val offer_price = view.offer_price
        val normal_price = view.normal_price
        val amount_off = view.cart_save_amt
        val product_name = view.product_name
        fun bindItem(cartItem: DeliveryCart) {

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

            product_name.text = cartItem.name
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
        fun bindItem(cartItem: DeliveryCart) {

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


 class CategoryRecyclerAdapter(val storeId: String, val categories: ArrayList<__Category__>,  val context: Context, val addQuantityClickListener: (DeliveryCart, Int) -> Unit, val clickListener: (__Category__) -> Unit ): RecyclerView.Adapter<CategoryRecyclerAdapter.CategoryViewHolder>() {

    private val TAG = "CategoryRecyclerAdapter"
    var deliveryCartItems: ArrayList<DeliveryCart> = ArrayList()
    var isLoading = false
    var isMoreDataAvailable_ = false
     var productsArray: ArrayList<DeliveryCart> = ArrayList()

    fun setIsLoading(is_Loading: Boolean) {
        isLoading = is_Loading
    }

    public fun setMoreDataAvailable(moreDataAvailable: Boolean) {
        isMoreDataAvailable_ = moreDataAvailable;
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        return CategoryViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_category, parent, false))
    }

    fun updateItems(items: ArrayList<DeliveryCart>) {
        deliveryCartItems = items
        notifyDataSetChanged()

//        if(productsArray.size != 0) {
//            val changedIndex : Int? = null
//            productsArray.forEach {
//                val product = it
//                val index = productsArray.indexOf(product)
//                items.forEach {
//                    if(product.key == it.key) {
//                        product.isInCart = true
//                        val deliveryCart =
//                            DeliveryCart(it.key, it.barcode, it.name,it.category, it.offerPrice, it.normalPrice, it.quantity, it.image_url, true)
//                        productsArray.set(index, deliveryCart)
//                    }
//                }
//
//            }
//            adapter.updateList(productsArray, storeId)
//
//
//        }

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

//    fun getCategoryProducts(category: __Category__, shimmerFrameLayout: ShimmerFrameLayout, adapter: ProductsAdapter, cartItems: List<DeliveryCart>) {
//        val collectionPath = "stores/" + storeId + "/offers"
//        val products = FirebaseFirestore.getInstance().collection(collectionPath)
//            .whereEqualTo("categoryOne", category.name!!)
//            .limit(10)
//
//        Log.d(TAG, "Category : ${category.name}")
//
//        products.get().addOnSuccessListener { documentSnapshots ->
//            Log.d(TAG, "documentSnapshots : $documentSnapshots")
//
//            productsArray.clear()
//            for (document in documentSnapshots) {
//                val offer = document.data as HashMap<String, Any>
//                val productName = offer["name"] as String
//                val deadline = offer["deadline"] as String
//                val normalPrice: String = offer["normal_price"] as String
//                val offerPrice = offer["offer_price"] as String
//                val category = offer["categoryOne"] as String
//                var barcode = offer["barcode"] as String?
//                val fileURL = offer["imageUrl"] as String
//
//                val intOfferPrice = offerPrice.toInt()
//                val intNormalPrice = normalPrice.toInt()
//
//                if (barcode == null) {
//                    barcode = "Not Set"
//                }
//
//                val deliveryCart =
//                    DeliveryCart(document.id, barcode, productName,category, intOfferPrice, intNormalPrice, 1, fileURL)
//                val product =
//                    __Product__(document.id, barcode, productName,category, intOfferPrice, intNormalPrice, 1, fileURL, false)
//                productsArray.add(deliveryCart)
//                Log.d(TAG, "Product : $deliveryCart")
//                Log.d(TAG, "Product Array Size : ${productsArray.size}")
//
//            }
//
//            if(productsArray.size != 0) {
//                productsArray.forEach {
//                    val product = it
//                    val index = productsArray.indexOf(product)
//                    cartItems.forEach {
//                        if(product.key == it.key) {
//                            product.isInCart = true
//                            val deliveryCart =
//                                DeliveryCart(it.key, it.barcode, it.name,it.category, it.offerPrice, it.normalPrice, it.quantity, it.image_url, true)
//                            productsArray.set(index, deliveryCart)
//                        }
//                    }
//
//                }
//                adapter.updateList(productsArray, storeId)
//
//
//            } else {
////                val index = categories.indexOf(category)
//////                Log.d(TAG, )
////                categories.remove(category)
//////                categories.drop(index)
////                notifyItemRemoved(index)
//////                notifyItemRangeRemoved(index, 1)
//            }
//
//
////            adapter.setFoundItems(cartItems)
//
//            shimmerFrameLayout.stopShimmerAnimation()
//            shimmerFrameLayout.visibility = View.GONE
//
//        }
//    }
     fun getCategoryProducts(category: __Category__, shimmerFrameLayout: ShimmerFrameLayout, adapter: ProductsAdapter, cartItems: List<DeliveryCart>) {
         val collectionPath = "stores/" + storeId + "/offers"
         val products = FirebaseFirestore.getInstance().collection(collectionPath)
             .whereEqualTo("categoryOne", category.name!!)
             .limit(10)

         Log.d(TAG, "Category : ${category.name}")

         var productsArray: ArrayList<DeliveryCart> = ArrayList()
         products.get().addOnSuccessListener { documentSnapshots ->
             Log.d(TAG, "documentSnapshots : $documentSnapshots")

             for (document in documentSnapshots) {
                 val offer = document.data as HashMap<String, Any>
                 val productName = offer["name"] as String
                 val deadline = offer["deadline"] as String
                 val normalPrice: String = offer["normal_price"] as String
                 val offerPrice = offer["offer_price"] as String
                 val category = offer["categoryOne"] as String
                 var barcode = offer["barcode"] as String?
                 val fileURL = offer["imageUrl"] as String

                 val intOfferPrice = offerPrice.toInt()
                 val intNormalPrice = normalPrice.toInt()

                 if (barcode == null) {
                     barcode = "Not Set"
                 }

                 val deliveryCart =
                     DeliveryCart(document.id, barcode, productName,category, intOfferPrice, intNormalPrice, 1, fileURL)
                 val product =
                     __Product__(document.id, barcode, productName,category, intOfferPrice, intNormalPrice, 1, fileURL, false)
                 productsArray.add(deliveryCart)
                 Log.d(TAG, "Product : $deliveryCart")
                 Log.d(TAG, "Product Array Size : ${productsArray.size}")

             }

             if(productsArray.size != 0) {
                 productsArray.forEach {
                     val product = it
                     val index = productsArray.indexOf(product)
                     cartItems.forEach {
                         if(product.key == it.key) {
                             product.isInCart = true
                             val deliveryCart =
                                 DeliveryCart(it.key, it.barcode, it.name,it.category, it.offerPrice, it.normalPrice, it.quantity, it.image_url, true)
                             productsArray.set(index, deliveryCart)
                         }
                     }

                 }
                 adapter.updateList(productsArray, storeId)


             } else {
//                val index = categories.indexOf(category)
////                Log.d(TAG, )
//                categories.remove(category)
////                categories.drop(index)
//                notifyItemRemoved(index)
//                notifyItemRangeRemoved(index, 1)
             }


//            adapter.setFoundItems(cartItems)

             shimmerFrameLayout.stopShimmerAnimation()
             shimmerFrameLayout.visibility = View.GONE

         }
     }


    fun addProductItems(deliveryItems: List<DeliveryCart>, products: List<Model.Product_>) {


    }

    inner class CategoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {

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
            getCategoryProducts(category, itemView.parentShimmerLayout, productsAdapter, deliveryCartItems)
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
        //check for last item
//        if ((position >= itemCount - 1 && isLoading && isMoreDataAvailable_))
//            isLoading = false;
//            load();
//        }
    }
}



class CategoryTwoRecyclerAdapter(val storeId: String, val categories: ArrayList<__Category__>,  val context: Context, val addQuantityClickListener: (DeliveryCart, Int) -> Unit, val clickListener: (__Category__) -> Unit ): RecyclerView.Adapter<CategoryTwoRecyclerAdapter.CategoryViewHolder>() {

    private val TAG = "CategoryRecyclerAdapter"
    var deliveryCartItems: ArrayList<DeliveryCart> = ArrayList()
    var isLoading = false
    var isMoreDataAvailable_ = false

    fun setIsLoading(is_Loading: Boolean) {
        isLoading = is_Loading
    }

    public fun setMoreDataAvailable(moreDataAvailable: Boolean) {
        isMoreDataAvailable_ = moreDataAvailable;
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        return CategoryViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_category, parent, false))
    }

    fun updateItems(items: ArrayList<DeliveryCart>) {
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

    fun getCategoryProducts(category: __Category__, shimmerFrameLayout: ShimmerFrameLayout, adapter: ProductsAdapter, cartItems: List<DeliveryCart>) {
        val collectionPath = "stores/" + storeId + "/offers"
        val products = FirebaseFirestore.getInstance().collection(collectionPath)
            .whereEqualTo("categoryTwo", category.name!!)
            .limit(10)

        Log.d(TAG, "Category : ${category.name}")

        var productsArray: ArrayList<DeliveryCart> = ArrayList()
        products.get().addOnSuccessListener { documentSnapshots ->
            Log.d(TAG, "documentSnapshots : $documentSnapshots")

            for (document in documentSnapshots) {
                val offer = document.data as HashMap<String, Any>
                val productName = offer["name"] as String
                val deadline = offer["deadline"] as String
                val normalPrice: String = offer["normal_price"] as String
                val offerPrice = offer["offer_price"] as String
                val category = offer["categoryOne"] as String
                var barcode = offer["barcode"] as String?
                val fileURL = offer["imageUrl"] as String

                val intOfferPrice = offerPrice.toInt()
                val intNormalPrice = normalPrice.toInt()

                if (barcode == null) {
                    barcode = "Not Set"
                }

                val deliveryCart =
                    DeliveryCart(document.id, barcode, productName,category, intOfferPrice, intNormalPrice, 1, fileURL)
                val product =
                    __Product__(document.id, barcode, productName,category, intOfferPrice, intNormalPrice, 1, fileURL, false)
                productsArray.add(deliveryCart)
                Log.d(TAG, "Product : $deliveryCart")
                Log.d(TAG, "Product Array Size : ${productsArray.size}")

            }

            if(productsArray.size != 0) {
                productsArray.forEach {
                    val product = it
                    val index = productsArray.indexOf(product)
                    cartItems.forEach {
                        if(product.key == it.key) {
                            product.isInCart = true
                            val deliveryCart =
                                DeliveryCart(it.key, it.barcode, it.name,it.category, it.offerPrice, it.normalPrice, it.quantity, it.image_url, true)
                            productsArray.set(index, deliveryCart)
                        }
                    }

                }
                adapter.updateList(productsArray, storeId)


            } else {
//                val index = categories.indexOf(category)
////                Log.d(TAG, )
//                categories.remove(category)
////                categories.drop(index)
//                notifyItemRemoved(index)
//                notifyItemRangeRemoved(index, 1)
            }


//            adapter.setFoundItems(cartItems)

            shimmerFrameLayout.stopShimmerAnimation()
            shimmerFrameLayout.visibility = View.GONE

        }
    }


    fun addProductItems(deliveryItems: List<DeliveryCart>, products: List<Model.Product_>) {


    }

    inner class CategoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {

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
            getCategoryProducts(category, itemView.parentShimmerLayout, productsAdapter, deliveryCartItems)
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
        //check for last item
//        if ((position >= itemCount - 1 && isLoading && isMoreDataAvailable_))
//            isLoading = false;
//            load();
//        }
    }
}


class CategoryThreeRecyclerAdapter(val storeId: String, val categories: ArrayList<__Category__>,  val context: Context, val addQuantityClickListener: (DeliveryCart, Int) -> Unit, val clickListener: (__Category__) -> Unit ): RecyclerView.Adapter<CategoryThreeRecyclerAdapter.CategoryViewHolder>() {

    private val TAG = "CategoryRecyclerAdapter"
    var deliveryCartItems: ArrayList<DeliveryCart> = ArrayList()
    var isLoading = false
    var isMoreDataAvailable_ = false

    fun setIsLoading(is_Loading: Boolean) {
        isLoading = is_Loading
    }

    public fun setMoreDataAvailable(moreDataAvailable: Boolean) {
        isMoreDataAvailable_ = moreDataAvailable;
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        return CategoryViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_category, parent, false))
    }

    fun updateItems(items: ArrayList<DeliveryCart>) {
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

    fun getCategoryProducts(category: __Category__, shimmerFrameLayout: ShimmerFrameLayout, adapter: ProductsAdapter, cartItems: List<DeliveryCart>) {
        Log.d(TAG, "getCategoryProducts called")
        val collectionPath = "stores/" + storeId + "/offers"
        val products = FirebaseFirestore.getInstance().collection(collectionPath)
            .whereEqualTo("categoryThree", category.name!!)
            .limit(10)

        Log.d(TAG, "Category : ${category.name}")

        var productsArray: ArrayList<DeliveryCart> = ArrayList()
        products.get().addOnSuccessListener { documentSnapshots ->
            Log.d(TAG, "documentSnapshots : $documentSnapshots")

            for (document in documentSnapshots) {
                val offer = document.data as HashMap<String, Any>
                val productName = offer["name"] as String
                val deadline = offer["deadline"] as String
                val normalPrice: String = offer["normal_price"] as String
                val offerPrice = offer["offer_price"] as String
                val category = offer["categoryOne"] as String
                var barcode = offer["barcode"] as String?
                val fileURL = offer["imageUrl"] as String

                val intOfferPrice = offerPrice.toInt()
                val intNormalPrice = normalPrice.toInt()

                if (barcode == null) {
                    barcode = "Not Set"
                }

                val deliveryCart =
                    DeliveryCart(document.id, barcode, productName,category, intOfferPrice, intNormalPrice, 1, fileURL)
                val product =
                    __Product__(document.id, barcode, productName,category, intOfferPrice, intNormalPrice, 1, fileURL, false)
                productsArray.add(deliveryCart)
                Log.d(TAG, "Product : $deliveryCart")
                Log.d(TAG, "Product Array Size : ${productsArray.size}")

            }

            if(productsArray.size != 0) {
                productsArray.forEach {
                    val product = it
                    val index = productsArray.indexOf(product)
                    cartItems.forEach {
                        if(product.key == it.key) {
                            product.isInCart = true
                            val deliveryCart =
                                DeliveryCart(it.key, it.barcode, it.name,it.category, it.offerPrice, it.normalPrice, it.quantity, it.image_url, true)
                            productsArray.set(index, deliveryCart)
                        }
                    }

                }
                adapter.updateList(productsArray, storeId)


            } else {
//                val index = categories.indexOf(category)
////                Log.d(TAG, )
//                categories.remove(category)
////                categories.drop(index)
//                notifyItemRemoved(index)
//                notifyItemRangeRemoved(index, 1)
            }


//            adapter.setFoundItems(cartItems)

            shimmerFrameLayout.stopShimmerAnimation()
            shimmerFrameLayout.visibility = View.GONE

        }
    }


    fun addProductItems(deliveryItems: List<DeliveryCart>, products: List<Model.Product_>) {


    }

    inner class CategoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {

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
            getCategoryProducts(category, itemView.parentShimmerLayout, productsAdapter, deliveryCartItems)
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
        //check for last item
//        if ((position >= itemCount - 1 && isLoading && isMoreDataAvailable_))
//            isLoading = false;
//            load();
//        }
    }
}

    class SubCategoryRecyclerAdapter(val subCategories: ArrayList<__Category__>, val clickListener: (__Category__) -> Unit) :
        RecyclerView.Adapter<SubCategoryRecyclerAdapter.SubCategoryViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubCategoryViewHolder {
            return SubCategoryViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.item_subcategory,
                    parent,
                    false
                )
            )
        }

        override fun getItemCount(): Int {
            return subCategories.size
        }

        override fun onBindViewHolder(holder: SubCategoryViewHolder, position: Int) {

            if (subCategories.size > 0) {
                val subCategory = subCategories[position]
                holder.itemView.subcategory_tv.text = subCategory.name
                holder.itemView.setOnClickListener {
                    clickListener(subCategory)
                }

            }
        }


        inner class SubCategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        }
    }


    class SearchSuggestionsAdapter(val suggestions: ArrayList<__Category__>, val clickListener: (__Category__) -> Unit) :
        RecyclerView.Adapter<SearchSuggestionsAdapter.SearchSuggestionsViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchSuggestionsViewHolder {
            return SearchSuggestionsViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.item_search_suggestion,
                    parent,
                    false
                )
            )
        }

        override fun getItemCount(): Int {
            return suggestions.size
        }

        override fun onBindViewHolder(holder: SearchSuggestionsViewHolder, position: Int) {
            if (suggestions.size > 0) {
                val subCategory = suggestions[position]
                holder.itemView.search_sugg_text.text = subCategory.name
                holder.itemView.setOnClickListener {
                    clickListener(subCategory)
                }

            }
        }

        inner class SearchSuggestionsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        }
    }


