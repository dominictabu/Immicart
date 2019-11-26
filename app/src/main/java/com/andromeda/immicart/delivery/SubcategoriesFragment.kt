package com.andromeda.immicart.delivery


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Display
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.andromeda.immicart.R
import com.andromeda.immicart.networking.ImmicartAPIService
import com.andromeda.immicart.networking.Model
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_subcategories.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_CATEGORY_ID = "ARG_CATEGORY_ID"
private const val ARG_PARAM2 = "param2"



/**
 * A simple [Fragment] subclass.
 * Use the [SubcategoriesFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */

val immicartAPIService_ by lazy {
    ImmicartAPIService.create()
}

class SubcategoriesFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var categoryId: Int? = null
    private var param2: String? = null

    private  var viewModel: ProductsViewModel? = null
    lateinit var cartItems: List<DeliveryCart>
    private var TAG = "SubcategoriesFragment"

    var disposable: Disposable? = null

    var categoryRecyclerAdapter: CategoryRecyclerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            categoryId = it.getInt(ARG_CATEGORY_ID)
//            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_subcategories, container, false)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


        viewModel = activity?.let {
            ViewModelProviders.of(it).get(ProductsViewModel::class.java)
        }



        viewModel?.allDeliveryItems()?.observe(this, Observer { items ->

            Log.d(TAG, "CartItems: $items")
            items?.let {
                cartItems = it
                val cartIteemsNumber = it.size
                badge.text = cartIteemsNumber.toString()
                Log.d(TAG, "CartItems Length: ${items.count()}")

                categoryRecyclerAdapter?.updateItems(it as ArrayList<DeliveryCart>)

            }
        })


        viewModel?.categoryId?.observe(this, Observer { id ->
            retrieveCategories(id)

        })


//        categoryId?.let { retrieveCategories(it) }

        cart_frame_layout.setOnClickListener {

            startActivity(Intent(activity!!,DeliveryCartActivity::class.java))
        }

        myBackIcon.setOnClickListener {
            findNavController().popBackStack()
        }



    }

    fun intializeRecycler(categories: List<Model.Category_>) {

        val linearLayoutManager = LinearLayoutManager(activity!!, RecyclerView.VERTICAL, false)
        products_items_recycler.setNestedScrollingEnabled(false);

        products_items_recycler.setLayoutManager(linearLayoutManager)
        categoryRecyclerAdapter = CategoryRecyclerAdapter(categories, activity!!, { cartItem : DeliveryCart, newQuantity: Int -> cartItemClicked(cartItem, newQuantity)}, {category: Model.Category_ -> viewAll(category)})
        products_items_recycler.setAdapter(categoryRecyclerAdapter)

//        By default setNestedScrollingEnabled works only after API-21.
//
//            You can use ViewCompat.setNestedScrollingEnabled(recyclerView, false); to disable nested scrolling for
//            before and after API-21(Lollipop). Link to documentation.

    }

    private fun cartItemClicked(cartItem : DeliveryCart, newQuantity: Int) {
//        Toast.makeText(this, "Clicked: ${cartItem.name}", Toast.LENGTH_LONG).show()
        //TODO Change Quantity
        Log.d(TAG, "newQuantity : $newQuantity , $cartItem ")


        if (com.andromeda.immicart.delivery.cartItems.contains(cartItem)) {
            Log.d(TAG, "CartItems contain the item")
            viewModel?.updateQuantity(cartItem._id, newQuantity)

        } else {
            Log.d(TAG, "CartItems DO NOT  contain the item")

            viewModel?.insert(cartItem)
        }

    }

    fun retrieveCategories(id: Int) {
        val retrofitResponse = immicartAPIService_.categories

        Log.d(TAG, "retrieve Categories called")
        disposable =
            immicartAPIService_.getCategoryProducts(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result ->

                        Log.d(TAG, "Results: $result")

                        result?.let {
                            Log.d(TAG, "Categories: $it")

                            val categories = it.children
                            val categoryName = it.name
                            category_name.text = categoryName

                            if(categories?.size!! > 0) {
                                val categoriesData: ArrayList<Model.Category_> = ArrayList()
                                categories.forEach {
                                    val category = Model.Category_(it._id, it.name, null, null, it.link)
                                    categoriesData.add(category)
                                }
                                initializeSubCategoryRecyclerView(categoriesData)
                                intializeRecycler(categoriesData)

                            } else {

                            }
                            categoryRecyclerAdapter?.updateItems(cartItems as ArrayList<DeliveryCart>)
                        }

                    },

                    { error ->
                        //                        showError(error.message)
                    }
                )
    }

    fun viewAll(category: Model.Category_) {

        val subcategoriesFragment = SubcategoriesFragment.newInstance(category._id)

        (activity!! as ProductsPageActivity).performFragmnetTransaction(subcategoriesFragment)

    }


    fun initializeSubCategoryRecyclerView(subCategories: ArrayList<Model.Category_>) {
        val linearLayoutManager = LinearLayoutManager(activity!!, RecyclerView.HORIZONTAL, false)
        subcategory_items_recycler.setNestedScrollingEnabled(false);

        subcategory_items_recycler.setLayoutManager(linearLayoutManager)
        val categoryRecyclerAdapter = SubCategoryRecyclerAdapter(subCategories = subCategories)
        subcategory_items_recycler.setAdapter(categoryRecyclerAdapter)
    }



    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SubcategoriesFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(categoryId: Int) =
            SubcategoriesFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_CATEGORY_ID, categoryId)
//                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
