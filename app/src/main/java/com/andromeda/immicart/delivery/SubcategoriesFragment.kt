package com.andromeda.immicart.delivery


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.andromeda.immicart.R
import com.andromeda.immicart.delivery.Utils.MyDatabaseUtil
import com.andromeda.immicart.delivery.checkout.DeliveryCartActivity
import com.andromeda.immicart.networking.ImmicartAPIService
import com.andromeda.immicart.networking.Model
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_subcategories.*
//import kotlinx.android.synthetic.main.fragment_subcategories.badge
import kotlinx.android.synthetic.main.fragment_subcategories.cart_frame_layout

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

private val _baseURL_ = "https://us-central1-immicart-2ca69.cloudfunctions.net/"

val immicartAPIService_ by lazy {
    ImmicartAPIService.create(_baseURL_)
}

class SubcategoriesFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var categoryId: String? = null
    private var param2: String? = null

    private  lateinit var viewModel: ProductsViewModel
    lateinit var cartItems: List<DeliveryCart>
    private var TAG = "SubcategoriesFragment"
    private var loading  = true

    var disposable: Disposable? = null
    private lateinit var storeId: String
    val db = FirebaseFirestore.getInstance();
    private lateinit var lastVisibleSnapShot: DocumentSnapshot
    val categories: ArrayList<__Category__> = ArrayList()

    var categoryRecyclerAdapter: CategoryTwoRecyclerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            categoryId = it.getString(ARG_CATEGORY_ID)
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


        viewModel = ViewModelProviders.of(activity!!).get(ProductsViewModel::class.java)

//        getCurrentStore()
//        viewModel.currentStores().observe(activity!!, Observer {
//            it?.let {
//                if(it.size > 0) {
//                    Log.d(TAG, "Stores size more than 0")
//                    val store = it[0]
//                    storeId = store.key
//                    getCategories()
//
//                } else {
//                    Log.d(TAG, "Stores size 0")
//
//                }
//
//
//            }
//        })




//        viewModel?.allDeliveryItems()?.observe(this, Observer { items ->
//
//            Log.d(TAG, "CartItems: $items")
//            items?.let {
//                cartItems = it
//                val cartIteemsNumber = it.size
//                badge.text = cartIteemsNumber.toString()
//                Log.d(TAG, "CartItems Length: ${items.count()}")
//
//                categoryRecyclerAdapter?.updateItems(it as ArrayList<DeliveryCart>)
//
//            }
//        })


        viewModel?.categoryParent?.observe(this, Observer { category ->
            categoryId = category.key
            category_name.text = category.name


//            retrieveCategories(id)
            categoryId?.let {
//                getSubCategories(categoryId!!)
                getCurrentStore()

            }

        })

//        nested_scrolling_view.getViewTreeObserver().addOnScrollChangedListener(object : ViewTreeObserver.OnScrollChangedListener {
//            override fun onScrollChanged() {
//                nested_scrolling_view?.let {
//
//                    val view = nested_scrolling_view.getChildAt(nested_scrolling_view.getChildCount() - 1)
//
//                    val diff = (view.getBottom() - (nested_scrolling_view.getHeight() + nested_scrolling_view
//                        .getScrollY()));
//
//                    if (diff == 0) {
//                        // your pagination code
//                        lastVisibleSnapShot?.let {
//                            paginateToNext(lastVisibleSnapShot)
//
//                        }
//
//                    }
//                }
//            }
//
//        })


//        categoryId?.let { retrieveCategories(it) }

        cart_frame_layout?.setOnClickListener {

            startActivity(Intent(activity!!, DeliveryCartActivity::class.java))
        }

        myBackIcon?.setOnClickListener {
            findNavController().popBackStack()
        }



    }


    private fun getSubCategories(categoryKey: String) {
        val collectionPath = "stores/$storeId/categories/$categoryKey/subcategories"
        val first = db.collection(collectionPath)

        val subCategories = ArrayList<__Category__>()
        first.get()
            .addOnSuccessListener { documentSnapshots ->
                // ...

                // Get the last visible document

                for (document in documentSnapshots) {
                    Log.d(TAG, "${document.id} => ${document.data}")
                    val category__ = document.data as HashMap<String, Any>
                    val categoryName = category__["name"] as String
                    val hasChildren = category__["hasChildren"] as Boolean
                    val category = __Category__(document.id, categoryName, hasChildren)
//                    val category = document.toObject(__Category__::class.java)
                    subCategories.add(category)

                    Log.d(TAG, "Category: $category")

//                    val serviceFee = document.data.

                }

                initializeSubCategoryRecyclerView(subCategories)


            }

    }

    private fun getCategoriesTwo(categoryKey: String) {
        val collectionPath = "stores/$storeId/categories/$categoryKey/subcategories"
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


            }


    }

    private fun getCategories() {
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


            }


    }

    private fun intializeRecycler(categories: List<__Category__>) {

        val linearLayoutManager = LinearLayoutManager(activity!!, RecyclerView.VERTICAL, false)
        products_items_recycler.setNestedScrollingEnabled(false);

        products_items_recycler.setLayoutManager(linearLayoutManager)
        categoryRecyclerAdapter = CategoryTwoRecyclerAdapter(storeId,
            categories as ArrayList<__Category__>, activity!!, { cartItem : DeliveryCart, newQuantity: Int -> cartItemClicked(cartItem, newQuantity)}, { category: __Category__ -> viewAll(category)})


        products_items_recycler.setAdapter(categoryRecyclerAdapter)

        var pastVisiblesItems : Int
        var visibleItemCount: Int
        var totalItemCount : Int;

        products_items_recycler.addOnScrollListener(object: RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                Log.d(TAG, "Recycler view OnScrollListener onScrolled called")
                if (dy > 0) //check for scroll down
                {
                    Log.d(TAG, "RecyclerView Scrolling down")
                    visibleItemCount = linearLayoutManager.getChildCount();
                    Log.d(TAG, "RecyclerView Scrolling down")

                    totalItemCount = linearLayoutManager.getItemCount();
                    Log.d(TAG, "RecyclerView Scrolling down")

                    pastVisiblesItems = linearLayoutManager.findFirstVisibleItemPosition();
                    Log.d(TAG, "RecyclerView Scrolling down")


                    if (loading) {
                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                            loading = false;
                            Log.v("...", "Last Item Wow !");
                            //Do pagination.. i.e. fetch new data
                            paginateToNext(lastVisibleSnapShot)
                        }
                    }
                }
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                Log.d(TAG, "Recycler view OnScrollListener onScrollStateChanged called")

            }
        })


//
//        })
//        categoryRecyclerAdapter = CategoryRecyclerAdapter(storeId, categories, activity!!, { cartItem : DeliveryCart, newQuantity: Int -> cartItemClicked(cartItem, newQuantity)}, {category: Model.Category_ -> viewAll(category)})
//        products_items_recycler.setAdapter(categoryRecyclerAdapter)

//        By default setNestedScrollingEnabled works only after API-21.
//
//            You can use ViewCompat.setNestedScrollingEnabled(recyclerView, false); to disable nested scrolling for
//            before and after API-21(Lollipop). Link to documentation.

    }

    private fun paginateToNext(documentSnapshot: DocumentSnapshot) {

        val collectionPath = "stores/$storeId/categories/$categoryId/subcategories"

//        val collectionPath = "stores/" + storeId + "/categories"

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

    private fun retrieveCategories(id: Int) {
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
//                                initializeSubCategoryRecyclerView(categoriesData)
//                                intializeRecycler(categoriesData)

                            } else {

                            }
                            categoryRecyclerAdapter?.updateItems(cartItems as ArrayList<DeliveryCart>)
                        }

                    },

                    { error ->
                        //showError(error.message)
                    }
                )
    }

    private fun viewAll(category: __Category__) {

        viewModel.setCategoryChildOne(category)
        findNavController().navigate(R.id.action_subcategoriesFragment_to_subCategoryTwoFragment)

    }


    private fun initializeSubCategoryRecyclerView(subCategories: ArrayList<__Category__>) {

        val linearLayoutManager = LinearLayoutManager(activity!!, RecyclerView.HORIZONTAL, false)
        subcategory_items_recycler.setNestedScrollingEnabled(false);

        subcategory_items_recycler.setLayoutManager(linearLayoutManager)
        val categoryRecyclerAdapter = SubCategoryRecyclerAdapter(subCategories, {category : __Category__ -> viewAll(category)})
        subcategory_items_recycler.setAdapter(categoryRecyclerAdapter)
    }

    private fun getCurrentStore() {
        val userUID = FirebaseAuth.getInstance().uid
        val ref = MyDatabaseUtil.getDatabase().reference.child("customers/$userUID/current_store")

        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(p0: DataSnapshot) {
                val store = p0.getValue(CurrentStore::class.java)

                store?.let {
                    storeId = store.storeID as String
                    getCategoriesTwo(categoryId!!)
                    getSubCategories(categoryId!!)


                }
            }

        })

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
        fun newInstance(categoryId: String) =
            SubcategoriesFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_CATEGORY_ID, categoryId)
//                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
