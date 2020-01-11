package com.andromeda.immicart.delivery


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager

import com.andromeda.immicart.R
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_last_category.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [LastCategoryFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class LastCategoryFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    val db = FirebaseFirestore.getInstance();
    private var cartItems: List<DeliveryCart> = ArrayList()

    private var param2: String? = null
    private lateinit var lastVisibleSnapShot: DocumentSnapshot
    private  lateinit var viewModel: ProductsViewModel
    private lateinit var storeId: String
    private lateinit var productsAdapter: ProductsAdapter

    private var TAG = "LastCategoryFragment"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_last_category, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(activity!!).get(ProductsViewModel::class.java)

        val gridLayoutManager = GridLayoutManager(activity!!,2 )

//        products_items_recycler.setNestedScrollingEnabled(false);

        products_items_recycler.setLayoutManager(gridLayoutManager)
        productsAdapter = ProductsAdapter(activity!!, { cartItem : DeliveryCart, newQuantity: Int -> cartItemClicked(cartItem, newQuantity)})

        products_items_recycler.setAdapter(productsAdapter)

        viewModel.currentStores().observe(activity!!, Observer {
            it?.let {
                if(it.size > 0) {
                    Log.d(TAG, "Stores size more than 0")
                    val store = it[0]
                    storeId = store.key
                    getProducts()
                } else {
                    Log.d(TAG, "Stores size 0")

                }


            }
        })




        viewModel?.allDeliveryItems()?.observe(this, Observer { items ->

            Log.d(TAG, "CartItems: $items")
            items?.let {
                cartItems = it
                val cartIteemsNumber = it.size
                badge.text = cartIteemsNumber.toString()
                Log.d(TAG, "CartItems Length: ${items.count()}")

//                categoryRecyclerAdapter?.updateItems(it as ArrayList<DeliveryCart>)

            }
        })

        viewModel.categoryLastChild.observe(this, Observer { subcategory ->
            category_name.text = subcategory.name


        })




    }


    fun initializeRecycler() {
        val gridLayoutManager = GridLayoutManager(activity!!,2 )

//        products_items_recycler.setNestedScrollingEnabled(false);

        products_items_recycler.setLayoutManager(gridLayoutManager)
        val productsAdapter = ProductsAdapter(activity!!, { cartItem : DeliveryCart, newQuantity: Int -> cartItemClicked(cartItem, newQuantity)})

        products_items_recycler.setAdapter(productsAdapter)
    }

    private fun cartItemClicked(cartItem : DeliveryCart, newQuantity: Int) {
//        Toast.makeText(this, "Clicked: ${cartItem.name}", Toast.LENGTH_LONG).show()
        //TODO Change Quantity
        Log.d(TAG, "newQuantity : $newQuantity , $cartItem ")


        if (cartItems.contains(cartItem)) {
            Log.d(TAG, "CartItems contain the item")
            viewModel?.updateQuantity(cartItem.key, newQuantity)

        } else {
            Log.d(TAG, "CartItems DO NOT  contain the item")

            viewModel?.insert(cartItem)
        }

    }


    fun getProducts() {
        val collectionPath = "stores/" + storeId + "/offers"
        val products = FirebaseFirestore.getInstance().collection(collectionPath)
//            .whereEqualTo("categoryOne", category)
            .limit(10)

        var productsArray: ArrayList<DeliveryCart> = ArrayList()
        products.get().addOnSuccessListener { documentSnapshots ->
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
            productsAdapter.updateList(productsArray, storeId)
        }
    }

        companion object {
            /**
             * Use this factory method to create a new instance of
             * this fragment using the provided parameters.
             *
             * @param param1 Parameter 1.
             * @param param2 Parameter 2.
             * @return A new instance of fragment LastCategoryFragment.
             */
            // TODO: Rename and change types and number of parameters
            @JvmStatic
            fun newInstance(param1: String, param2: String) =
                LastCategoryFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
        }
    }

