package com.andromeda.immicart.delivery

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.andromeda.immicart.R
import com.andromeda.immicart.delivery.account.MyAccountActivity
import com.andromeda.immicart.delivery.checkout.DeliveryCartActivity
import com.andromeda.immicart.delivery.choose_store.ChooseStoreActivity
import com.andromeda.immicart.delivery.delivery_location.PickDeliveryAddressActivity
import com.andromeda.immicart.networking.ImmicartAPIService
import com.bumptech.glide.Glide
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.appbar.AppBarLayout
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.iid.FirebaseInstanceId
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.products_fragment.*


val immicartAPIService by lazy {
    ImmicartAPIService.create()
}


class ProductsFragment : Fragment() {
    private var cartItems: List<DeliveryCart> = ArrayList()


    private var TAG = "ProductsFragment"

    var disposable: Disposable? = null

    private lateinit var lastVisibleSnapShot: DocumentSnapshot
    private var loading = true;

    val categories: ArrayList<__Category__> = ArrayList()

    private val mShimmerViewContainer: ShimmerFrameLayout? = null

    var categoryRecyclerAdapter: CategoryRecyclerAdapter? = null
    companion object {
        fun newInstance() = ProductsFragment()
    }


    val db = FirebaseFirestore.getInstance();


    private lateinit var viewModel: ProductsViewModel
    private lateinit var storeId: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.products_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProviders.of(activity!!).get(ProductsViewModel::class.java)
//        viewModel.allDeliveryItems().observe(activity!!, Observer { items ->
//
//            Log.d(TAG, "CartItems: $items")
//            items?.let {
//                cartItems = it
//                val cartIteemsNumber = it.size
//                badge?.text = cartIteemsNumber.toString()
//                Log.d(TAG, "CartItems Length: ${items.count()}")
//                categoryRecyclerAdapter?.updateItems(cartItems as ArrayList<DeliveryCart>)
//
//
//
//            }
//        })



        app_bar.addOnOffsetChangedListener(object : AppBarLayout.OnOffsetChangedListener {
            override fun onOffsetChanged(appBarLayout: AppBarLayout?, verticalOffset: Int) {

                if (Math.abs(verticalOffset) == appBarLayout?.getTotalScrollRange()) {
                    // Collapsed

                    store_name?.setTextColor(resources.getColor(R.color.colorGreen_))
                    store_name?.setCompoundDrawablesWithIntrinsicBounds( 0, 0, R.drawable.ic_keyboard_arrow_down_green_24dp, 0);
                    Glide.with(activity!!).load(R.drawable.ic_account_circle_green_24dp).into(myAccountIcon)
                } else if (verticalOffset == 0) {
                    store_name?.setTextColor(resources.getColor(android.R.color.white))
                        store_name?.setCompoundDrawablesWithIntrinsicBounds( 0, 0, R.drawable.ic_keyboard_arrow_down_white_24dp, 0);
                        Glide.with(activity!!).load(R.drawable.ic_account_circle_white_24dp).into(myAccountIcon)

                        // Expanded

                } else {
                    // Somewhere in between
                }
            }

        })

        viewModel.currentStores().observe(activity!!, Observer {
            it?.let {
                if(it.size > 0) {
                    Log.d(TAG, "Stores size more than 0")
                    val store = it[0]
                    storeId = store.key
                    store_name?.text = store.name
                    search_hint?.text = "Search ${store.name}"
                    Glide.with(activity!!).load(store.logoUrl).into(profile_image)
                    getCategories()



                } else {
                    Log.d(TAG, "Stores size 0")

                }


            }
        })


        viewModel.allDeliveryLocations().observe(activity!!, androidx.lifecycle.Observer {

            it?.let {
                if(it.size > 0) {
                    val place = it[0]

                    address_one?.text = place.name
                    address_two?.text = place.address

                }

            }

        })

        nested_scrolling_view.getViewTreeObserver().addOnScrollChangedListener(object : ViewTreeObserver.OnScrollChangedListener {
            override fun onScrollChanged() {
                nested_scrolling_view?.let {

                    val view = nested_scrolling_view.getChildAt(nested_scrolling_view.getChildCount() - 1)

                    val diff = (view.getBottom() - (nested_scrolling_view.getHeight() + nested_scrolling_view
                        .getScrollY()));

                    if (diff == 0) {
                        // your pagination code
                        lastVisibleSnapShot?.let {
                            paginateToNext(lastVisibleSnapShot)

                        }

                    }
                }
            }

        })


//        retrieveCategories()
        // TODO: Use the ViewModel


        cart_frame_layout?.setOnClickListener {

            startActivity(Intent(activity!!, DeliveryCartActivity::class.java))
        }

        myAccountIcon?.setOnClickListener {
            startActivity(Intent(activity!!, MyAccountActivity::class.java))

        }

        address_ll?.setOnClickListener {
            startActivity(Intent(activity!!, PickDeliveryAddressActivity::class.java))

        }

        navigatetosearch?.visibility = View.VISIBLE
        navigatetosearch?.setOnClickListener {
            activity?.let {
                (activity!! as ProductsPageActivity).navigateToSearch()
            }
        }

        getToken()
        createNotificationChannel()


        store_name.setOnClickListener {
            startActivity(Intent(activity!!, ChooseStoreActivity::class.java))

        }



    }

    fun navigateToSubCategories(categoryId: String) {
        viewModel.setCategoryId(categoryId)
    }


    fun intializeRecycler(categories: List<__Category__>) {

        val linearLayoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        category_items_recycler?.setNestedScrollingEnabled(false);

        category_items_recycler?.setLayoutManager(linearLayoutManager)
        categoryRecyclerAdapter = CategoryRecyclerAdapter(storeId,
            categories as ArrayList<__Category__>, activity!!, { cartItem : DeliveryCart, newQuantity: Int -> cartItemClicked(cartItem, newQuantity)}, { category: __Category__ -> viewAll(category)})


        category_items_recycler?.setAdapter(categoryRecyclerAdapter)
//
//        var pastVisiblesItems : Int
//        var visibleItemCount: Int
//        var totalItemCount : Int;
//
//        category_items_recycler.addOnScrollListener(object: RecyclerView.OnScrollListener() {
//
//            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
//                Log.d(TAG, "Recycler view OnScrollListener onScrolled called")
//                if(dy > 0) //check for scroll down
//                {
//                    Log.d(TAG, "RecyclerView Scrolling down")
//                    visibleItemCount = linearLayoutManager.getChildCount();
//                    Log.d(TAG, "RecyclerView Scrolling down")
//
//                    totalItemCount = linearLayoutManager.getItemCount();
//                    Log.d(TAG, "RecyclerView Scrolling down")
//
//                    pastVisiblesItems = linearLayoutManager.findFirstVisibleItemPosition();
//                    Log.d(TAG, "RecyclerView Scrolling down")
//
//
//                    if (loading)
//                    {
//                        if ( (visibleItemCount + pastVisiblesItems) >= totalItemCount)
//                        {
//                            loading = false;
//                            Log.v("...", "Last Item Wow !");
//                            //Do pagination.. i.e. fetch new data
//                            paginateToNext(lastVisibleSnapShot)
//                        }
//                    }
//                }
//            }
//
//            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
//                super.onScrollStateChanged(recyclerView, newState)
//                Log.d(TAG, "Recycler view OnScrollListener onScrollStateChanged called")
//
//            }
//
//        })

//        By default setNestedScrollingEnabled works only after API-21.
//
//            You can use ViewCompat.setNestedScrollingEnabled(recyclerView, false); to disable nested scrolling for
//            before and after API-21(Lollipop). Link to documentation.
    }


    fun viewAll(category: __Category__) {

//        val subcategoriesFragment = SubcategoriesFragment.newInstance(category._id)
//
//        (activity!! as ProductsPageActivity).performFragmnetTransaction(subcategoriesFragment)

        viewModel.setCategoryId(category.key!!)
        viewModel.setCategoryParent(category)

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


//    fun getOffers() {
//
//        val db = FirebaseFirestore.getInstance()
//        val collectionPath = "stores/"  + id +  "/offers"
//
//        val offers : ArrayList<Offer> = ArrayList()
//        db.collection(collectionPath)
//            .get()
//            .addOnSuccessListener { documents ->
//
//                Log.d(TAG, "Offers: " + documents.documents)
//
//                for (document in documents) {
//
//                    val offer = document.data as HashMap<String, Any>
//                    val productName = offer["name"] as String?
//                    val deadline = offer["deadline"] as String
//                    val normalPrice : String = offer["normal_price"] as String
//                    val offerPrice = offer["offer_price"] as String
//                    val category = offer["category"]
//                    val barcode = offer["barcode"]
//                    val fileURL = offer["imageUrl"] as String?
//
//
//
//
//
//                    val _offer = Offer(fileURL, productName, normalPrice, offerPrice)
//
//
//                    offers.add(_offer)
//
//
//                    Log.d(TAG, "${document.id} => ${document.data}")
//                }
//                loading_view.visibility = View.GONE
//
//                val offersRecyclerAdapter = OffersRecyclerAdapter(offers, activity)
//                val linearLayoutManager : LinearLayoutManager = LinearLayoutManager(activity)
//                offers_recycler_view.layoutManager = linearLayoutManager
//                offers_recycler_view.adapter = offersRecyclerAdapter
//                Log.w(TAG, "Starting Recycler view: ")
//
//
//            }
//            .addOnFailureListener { exception ->
//                Log.w(TAG, "Error getting documents: ", exception)
//            }
//    }


//    fun retrieveCategories() {
//        val retrofitResponse = immicartAPIService.categories
//
//        Log.d(TAG, "retrieve Categories called")
//        disposable =
//            immicartAPIService.categories
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(
//                    { result ->
//
//                        Log.d(TAG, "Results: $result")
//
//                        result?.let {
//                            Log.d(TAG, "Categories: $it")
//
//                            intializeRecycler(it)
//                            categoryRecyclerAdapter?.updateItems(cartItems as ArrayList<DeliveryCart>)
//                        }
//
//                    },
//
//                    { error ->
////                        showError(error.message)
//                    }
//                )
//    }


    fun getCategories() {
        val collectionPath = "stores/" + storeId + "/categories"
        val first = db.collection(collectionPath)
            .limit(3)
        categories.clear()

        first.get()
            .addOnSuccessListener { documentSnapshots ->
                // ...

                // Get the last visible document
                lastVisibleSnapShot = documentSnapshots.documents[documentSnapshots.size() - 1]

                for (document in documentSnapshots) {
                    Log.d(TAG, "${document.id} => ${document.data}")
                    val category__ = document.data as HashMap<String, Any>
                    val categoryName = category__["name"] as String
                    val hasChildren = category__["hasChildren"] as Boolean
                    val category = __Category__(document.id, categoryName, hasChildren)
//                    val category = document.toObject(__Category__::class.java)
                    categories.add(category)

                    Log.d(TAG, "Category: $category")

//                    val serviceFee = document.data.

                }

                intializeRecycler(categories)
                viewModel.allDeliveryItems().observe(activity!!, Observer { items ->
                    Log.d(TAG, "CartItems: $items")
                    items?.let {
                        cartItems = it
                        val cartIteemsNumber = it.size
                        badge?.text = cartIteemsNumber.toString()
                        Log.d(TAG, "CartItems Length: ${items.count()}")
                        categoryRecyclerAdapter?.updateItems(cartItems as ArrayList<DeliveryCart>)
                    }

                })
                loading_view?.visibility = View.GONE
                coordinator_layout?.visibility = View.VISIBLE

            }

    }


    fun paginateToNext(documentSnapshot: DocumentSnapshot) {
        val collectionPath = "stores/" + storeId + "/categories"

        val next = db.collection(collectionPath)
            .startAfter(documentSnapshot)
            .limit(3)

        val categoriesArray = ArrayList<__Category__>()

        next.get()
            .addOnSuccessListener { documentSnapshots ->
                // Get the last visible document
                if(documentSnapshots.size() > 0) {
                    lastVisibleSnapShot = documentSnapshots.documents[documentSnapshots.size() - 1]

                    for (document in documentSnapshots) {
                        Log.d(TAG, "${document.id} => ${document.data}")
                        val category__ = document.data as HashMap<String, Any>
                        val categoryName = category__["name"] as String
                        val hasChildren = category__["hasChildren"] as Boolean
                        val category = __Category__(document.id, categoryName, hasChildren)
//                    val category = document.toObject(__Category__::class.java)
                        categoriesArray.add(category)

                        Log.d(TAG, "Category: $category")

//                    val serviceFee = document.data.

                    }

                    val insertIndex = categories.size
                    categories.addAll(insertIndex, categoriesArray);
                    categoryRecyclerAdapter?.notifyItemRangeInserted(insertIndex, categoriesArray.size);



                }





            }




    }

    fun getCategoryProducts(category : String) {
        val collectionPath = "stores/" + storeId + "/offers"
        val products = db.collection(collectionPath)
            .whereEqualTo("categoryOne", category)
            .limit(10)

        products.get().addOnSuccessListener { documentSnapshots  ->
            for (document in documentSnapshots) {
                val offer = document.data as HashMap<String, Any>
                val productName = offer["name"] as String
                val deadline = offer["deadline"] as String
                val normalPrice : String = offer["normal_price"] as String
                val offerPrice = offer["offer_price"] as String
                val category = offer["categoryOne"] as String
                val barcode = offer["barcode"] as String
                val fileURL = offer["imageUrl"] as String

                val intOfferPrice = offerPrice.toInt()
                val intNormalPrice = normalPrice.toInt()

                val deliveryCart = DeliveryCart(document.id, barcode, productName,category,  intOfferPrice, intNormalPrice, 1, fileURL)
            }

        }
    }

    fun getToken() {
        // Get token
        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w(TAG, "getInstanceId failed", task.exception)
                    return@OnCompleteListener
                }

                // Get new Instance ID token
                val token = task.result?.token
                Log.d(TAG, "Device Token: $token")

                // Log and toast
//                val msg = getString(R.string.msg_token_fmt, token)
//                Log.d(TAG, msg)
                Toast.makeText(activity!!, "Token Retrieved", Toast.LENGTH_SHORT).show()
            })
    }







    override fun onPause() {
        super.onPause()
        disposable?.dispose()

    }

    private fun cartItemClicked(cartItem : DeliveryCart, newQuantity: Int) {
//        Toast.makeText(this, "Clicked: ${cartItem.name}", Toast.LENGTH_LONG).show()
        //TODO Change Quantity
        Log.d(TAG, "newQuantity : $newQuantity , $cartItem ")

        cartItems.forEach {
            if(it.key == cartItem.key) {
                Log.d(TAG, "CartItems contain the item")
                viewModel.updateQuantity(cartItem.key, newQuantity)
                return
            }
        }

            Log.d(TAG, "CartItems DO NOT  contain the item")
            viewModel.insert(cartItem)

    }


    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name)
            val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(getString(R.string.default_notification_channel_id), name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                activity!!.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

}
