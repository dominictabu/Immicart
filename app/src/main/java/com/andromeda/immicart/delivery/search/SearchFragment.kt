package com.andromeda.immicart.delivery.search


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
import com.andromeda.immicart.delivery.*
import com.andromeda.immicart.delivery.checkout.DeliveryCartActivity
import com.andromeda.immicart.delivery.search.visionSearch.LiveBarcodeScanningActivity
import com.google.firebase.firestore.FirebaseFirestore
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_search.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SearchFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class SearchFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    var disposable: Disposable? = null
    private var TAG = "SearchFragment"
    val db = FirebaseFirestore.getInstance();
    private lateinit var viewModel: ProductsViewModel

    private var cartItems: List<DeliveryCart> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
//            param1 = it.getString(ARG_PARAM1)
//            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(activity!!).get(ProductsViewModel::class.java)

//        search_view_search_fragment.requestFocusFromTouch();
        search_view_search_fragment?.requestFocus()

//        search_view_search_fragment.onFocusChangeListener(object : View.OnFocusChangeListener {
//            override fun onFocusChange(v: View?, hasFocus: Boolean) {
//                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//            }
//
//        })


        viewModel.currentStores().observe(activity!!, Observer {
            it?.let {
                if(it.size > 0) {
                    Log.d(TAG, "Stores size more than 0")
                    val store = it[0]
                    val storeId = store.key
                    val storeName = store.name
                    search_view_search_fragment.queryHint = "Search $storeName"
                    getCategories(storeId)



                } else {
                    Log.d(TAG, "Stores size 0")

                }


            }
        })

        viewModel.allDeliveryItems().observe(activity!!, Observer { items ->

            Log.d(TAG, "CartItems: $items")
            items?.let {
                cartItems = it
                val cartIteemsNumber = it.size
                badge?.text = cartIteemsNumber.toString()
                Log.d(TAG, "CartItems Length: ${items.count()}")

//                categoryRecyclerAdapter?.updateItems(it as ArrayList<DeliveryCart>)

            }
        })

        cart_frame_layout.setOnClickListener {

            startActivity(Intent(activity!!, DeliveryCartActivity::class.java))
        }


        barcode_search?.setOnClickListener {
            startActivity(Intent(activity!!, LiveBarcodeScanningActivity::class.java))

        }





//        retrieveCategories()
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

//                            intializeRecycler(it)
                        }

                    },

                    { error ->
                        //                        showError(error.message)
                    }
                )
    }

    fun getCategories(storeId: String) {
        val collectionPath = "stores/" + storeId + "/categories"
        val first = db.collection(collectionPath)
            .limit(10)

        val categories = ArrayList<__Category__>()

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
                    categories.add(category)

                    Log.d(TAG, "Category: $category")

//                    val serviceFee = document.data.

                }

                intializeRecycler(categories)


            }


    }


    fun searchTerm(category: __Category__) {

        viewModel.setSearchWord(category.name!!)
//        findNavController().navigate(R.id.action_searchFragment_to_searchResultsFragment)
        startActivity(Intent(requireActivity(), SearchResultsActivity::class.java))

    }



    fun intializeRecycler(category: List<__Category__>) {


        val linearLayoutManager = LinearLayoutManager(activity!!, RecyclerView.VERTICAL, false)
        recycler_items_search.setNestedScrollingEnabled(false);

        recycler_items_search.setLayoutManager(linearLayoutManager)
        val searchSuggestionsAdapter = SearchSuggestionsAdapter(
            category as ArrayList<__Category__>,
            { category: __Category__ -> searchTerm(category) })
        recycler_items_search.setAdapter(searchSuggestionsAdapter)
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SearchFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() =
            SearchFragment().apply {

            }
    }
}
