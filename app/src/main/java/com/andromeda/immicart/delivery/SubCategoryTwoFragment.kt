package com.andromeda.immicart.delivery


import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import com.andromeda.immicart.delivery.checkout.DeliveryCartActivity
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.fragment_sub_category_two.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_CATEGORY_ID = "ARG_CATEGORY_ID"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SubCategoryTwoFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class SubCategoryTwoFragment : Fragment() {
    private var categoryId: String? = null
    private var param2: String? = null

    private  lateinit var viewModel: ProductsViewModel
    lateinit var cartItems: List<DeliveryCart>
    private var TAG = "SubcategoriesFragment"

    var disposable: Disposable? = null
    private lateinit var storeId: String
    val db = FirebaseFirestore.getInstance();
    private lateinit var lastVisibleSnapShot: DocumentSnapshot
    val categories: ArrayList<__Category__> = ArrayList()

    var categoryRecyclerAdapter: CategoryRecyclerAdapter? = null

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
        return inflater.inflate(R.layout.fragment_sub_category_two, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProviders.of(activity!!).get(ProductsViewModel::class.java)


        viewModel.currentStores().observe(activity!!, Observer {
            it?.let {
                if(it.size > 0) {
                    Log.d(TAG, "Stores size more than 0")
                    val store = it[0]
                    storeId = store.key
                    getCategories()



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

                categoryRecyclerAdapter?.updateItems(it as ArrayList<DeliveryCart>)

            }
        })



        viewModel.categoryParent.observe(this, Observer { category ->
            val parentCategoryKey = category.key
            //            retrieveCategories(id)
            viewModel.categoryChildOne.observe(this, Observer { subcategory ->

                val childCategoryKey = subcategory.key
                category_name.text = subcategory.name



                parentCategoryKey?.let {
                    childCategoryKey?.let {

                        getSubCategories(parentCategoryKey, childCategoryKey)

                    }
                }


                //            retrieveCategories(id)



            })


        })





//        categoryId?.let { retrieveCategories(it) }

        cart_frame_layout.setOnClickListener {

            startActivity(Intent(activity!!, DeliveryCartActivity::class.java))
        }

        myBackIcon.setOnClickListener {
            findNavController().popBackStack()
        }
    }


    fun getCategories() {
        val collectionPath = "stores/" + storeId + "/categories"
        val first = db.collection(collectionPath)
            .limit(3)

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


            }


    }



    fun getSubCategories(categoryKey: String, subCategoryKey: String) {
        val collectionPath = "stores/$storeId/categories/$categoryKey/subcategories/$subCategoryKey/subcategories"
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



    fun intializeRecycler(categories: List<__Category__>) {

        val linearLayoutManager = LinearLayoutManager(activity!!, RecyclerView.VERTICAL, false)
        products_items_recycler.setNestedScrollingEnabled(false);

        products_items_recycler.setLayoutManager(linearLayoutManager)
        categoryRecyclerAdapter = CategoryRecyclerAdapter(storeId, categories, activity!!, { cartItem : DeliveryCart, newQuantity: Int -> cartItemClicked(cartItem, newQuantity)}, {category: __Category__ -> viewAll(category)})


        products_items_recycler.setAdapter(categoryRecyclerAdapter)

    }

    private fun cartItemClicked(cartItem : DeliveryCart, newQuantity: Int) {
//        Toast.makeText(this, "Clicked: ${cartItem.name}", Toast.LENGTH_LONG).show()
        //TODO Change Quantity
        Log.d(TAG, "newQuantity : $newQuantity , $cartItem ")

        if (com.andromeda.immicart.delivery.cartItems.contains(cartItem)) {
            Log.d(TAG, "CartItems contain the item")
            viewModel?.updateQuantity(cartItem.key, newQuantity)

        } else {
            Log.d(TAG, "CartItems DO NOT  contain the item")
            viewModel?.insert(cartItem)
        }

    }

    fun viewAll(category: __Category__) {

        viewModel.setCategoryLastChild(category)
        findNavController().navigate(R.id.action_subCategoryTwoFragment_to_lastCategoryFragment)

    }


    fun initializeSubCategoryRecyclerView(subCategories: ArrayList<__Category__>) {
        val linearLayoutManager = LinearLayoutManager(activity!!, RecyclerView.HORIZONTAL, false)
        subcategory_items_recycler.setNestedScrollingEnabled(false);

        subcategory_items_recycler.setLayoutManager(linearLayoutManager)
        val categoryRecyclerAdapter = SubCategoryRecyclerAdapter(subCategories, {category: __Category__ -> viewAll(category)})
        subcategory_items_recycler.setAdapter(categoryRecyclerAdapter)
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SubCategoryTwoFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SubCategoryTwoFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_CATEGORY_ID, categoryId)
//                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
