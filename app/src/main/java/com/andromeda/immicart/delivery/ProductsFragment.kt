package com.andromeda.immicart.delivery

import android.content.Intent
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.andromeda.immicart.R
import com.andromeda.immicart.checkout.CartImagesAdapter
import com.andromeda.immicart.delivery.trackingorder.MapsActivity
import com.andromeda.immicart.delivery.trackingorder.TrackOrderMapActivity
import com.andromeda.immicart.networking.ImmicartAPIService
import com.andromeda.immicart.networking.Model
import com.bumptech.glide.Glide
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.appbar.AppBarLayout
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.products_fragment.*



val immicartAPIService by lazy {
    ImmicartAPIService.create()
}

lateinit var cartItems: List<DeliveryCart>

class ProductsFragment : Fragment() {


    private var TAG = "ProductsFragment"

    var disposable: Disposable? = null


    private val mShimmerViewContainer: ShimmerFrameLayout? = null

    var categoryRecyclerAdapter: CategoryRecyclerAdapter? = null
    companion object {
        fun newInstance() = ProductsFragment()
    }

    private lateinit var viewModel: ProductsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.products_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProviders.of(activity!!).get(ProductsViewModel::class.java)
        viewModel.allDeliveryItems().observe(this, Observer { items ->

            Log.d(TAG, "CartItems: $items")
            items?.let {
                cartItems = it
                val cartIteemsNumber = it.size
                badge.text = cartIteemsNumber.toString()
                Log.d(TAG, "CartItems Length: ${items.count()}")

                categoryRecyclerAdapter?.updateItems(it as ArrayList<DeliveryCart>)

            }
        })


        app_bar.addOnOffsetChangedListener(object : AppBarLayout.OnOffsetChangedListener {
            override fun onOffsetChanged(appBarLayout: AppBarLayout?, verticalOffset: Int) {

                if (Math.abs(verticalOffset) == appBarLayout?.getTotalScrollRange()) {
                    // Collapsed

                    store_name.setTextColor(resources.getColor(R.color.colorGreen_))
                    store_name.setCompoundDrawablesWithIntrinsicBounds( 0, 0, R.drawable.ic_keyboard_arrow_down_green_24dp, 0);
                    Glide.with(activity!!).load(R.drawable.ic_account_circle_green_24dp).into(myAccountIcon)
                } else if (verticalOffset == 0) {
                    store_name.setTextColor(resources.getColor(android.R.color.white))
                    store_name.setCompoundDrawablesWithIntrinsicBounds( 0, 0, R.drawable.ic_keyboard_arrow_down_white_24dp, 0);
                    Glide.with(activity!!).load(R.drawable.ic_account_circle_white_24dp).into(myAccountIcon)

                    // Expanded
                } else {
                    // Somewhere in between
                }
            }

        })



        retrieveCategories()
        // TODO: Use the ViewModel


        cart_frame_layout.setOnClickListener {

            startActivity(Intent(activity!!,DeliveryCartActivity::class.java))
        }

        myAccountIcon.setOnClickListener {
            startActivity(Intent(activity!!, TrackOrderMapActivity::class.java))

        }

        delivery_address.setOnClickListener {
            startActivity(Intent(activity!!, PickDeliveryLocationActivity::class.java))

        }

//        store_name.setOnClickListener {
//            startActivity(Intent(activity!!, PickDeliveryLocationActivity::class.java))
//
//        }


    }

    fun navigateToSubCategories(categoryId: Int) {
        viewModel.setCategoryId(categoryId)
    }


    fun intializeRecycler(categories: List<Model.Category_>) {

        val linearLayoutManager = LinearLayoutManager(activity!!, RecyclerView.VERTICAL, false)
        category_items_recycler.setNestedScrollingEnabled(false);

        category_items_recycler.setLayoutManager(linearLayoutManager)
        categoryRecyclerAdapter = CategoryRecyclerAdapter(categories, activity!!, { cartItem : DeliveryCart, newQuantity: Int -> cartItemClicked(cartItem, newQuantity)}, {category: Model.Category_ -> viewAll(category)})
        category_items_recycler.setAdapter(categoryRecyclerAdapter)

//        By default setNestedScrollingEnabled works only after API-21.
//
//            You can use ViewCompat.setNestedScrollingEnabled(recyclerView, false); to disable nested scrolling for
//            before and after API-21(Lollipop). Link to documentation.
    }


    fun viewAll(category: Model.Category_) {

//        val subcategoriesFragment = SubcategoriesFragment.newInstance(category._id)
//
//        (activity!! as ProductsPageActivity).performFragmnetTransaction(subcategoriesFragment)

        viewModel.setCategoryId(category._id)

//        val options = navOptions {
//            anim {
//                enter = R.anim.slide_in_right
//                exit = R.anim.slide_out_left
//                popEnter = R.anim.slide_in_left
//                popExit = R.anim.slide_out_right
//            }
//        }

        findNavController().navigate(R.id.go_to_subcategories)


    }


    fun retrieveCategories() {
        val retrofitResponse = immicartAPIService.categories

        Log.d(TAG, "retrieve Categories called")
        disposable =
            immicartAPIService.categories
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result ->

                        Log.d(TAG, "Results: $result")

                        result?.let {
                            Log.d(TAG, "Categories: $it")

                            intializeRecycler(it)
                            categoryRecyclerAdapter?.updateItems(cartItems as ArrayList<DeliveryCart>)
                        }

                    },

                    { error ->
//                        showError(error.message)
                    }
                )
    }

    override fun onPause() {
        super.onPause()
        disposable?.dispose()

    }

    private fun cartItemClicked(cartItem : DeliveryCart, newQuantity: Int) {
//        Toast.makeText(this, "Clicked: ${cartItem.name}", Toast.LENGTH_LONG).show()
        //TODO Change Quantity
        Log.d(TAG, "newQuantity : $newQuantity , $cartItem ")


        if (cartItems.contains(cartItem)) {
            Log.d(TAG, "CartItems contain the item")
            viewModel.updateQuantity(cartItem._id, newQuantity)

        } else {
            Log.d(TAG, "CartItems DO NOT  contain the item")

            viewModel.insert(cartItem)
        }

    }

}
