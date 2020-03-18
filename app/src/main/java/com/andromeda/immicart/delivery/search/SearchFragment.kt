package com.andromeda.immicart.delivery.search


import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.NavHostFragment.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.andromeda.immicart.R
import com.andromeda.immicart.delivery.*
import com.andromeda.immicart.delivery.Utils.MyDatabaseUtil
import com.andromeda.immicart.delivery.checkout.DeliveryCartActivity
import com.andromeda.immicart.delivery.search.visionSearch.LiveBarcodeScanningActivity
import com.andromeda.immicart.delivery.search.visionSearch.imagelabeling.*
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.label.FirebaseVisionCloudImageLabelerOptions
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabel
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabeler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_search.*
import java.io.IOException
import kotlin.math.max

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
//        search_view_search_fragment?.requestFocus()

//        search_view_search_fragment.onFocusChangeListener(object : View.OnFocusChangeListener {
//            override fun onFocusChange(v: View?, hasFocus: Boolean) {
//                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//            }
//
//        })


//        viewModel.currentStores().observe(activity!!, Observer {
//            it?.let {
//                if(it.size > 0) {
//                    Log.d(TAG, "Stores size more than 0")
//                    val store = it[0]
//                    val storeId = store.key
//                    val storeName = store.name
//                    search_view_search_fragment.queryHint = "Search $storeName"
//                    getCategories(storeId)
//
//                } else {
//                    Log.d(TAG, "Stores size 0")
//
//                }
//
//
//            }
//        })

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

        cart_frame_layout?.setOnClickListener {

            startActivity(Intent(activity!!, DeliveryCartActivity::class.java))
        }


        barcode_search?.setOnClickListener {
            startActivity(Intent(activity!!, LiveBarcodeScanningActivity::class.java))

        }

        image_search?.setOnClickListener {
            startActivity(Intent(activity!!, ImageLabelingActivity::class.java))

        }
        search_ll?.setOnClickListener {
            val intent = Intent(requireActivity(), SearchResultsActivity::class.java)
            startActivity(intent)
        }

        getCurrentStore()

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

//        viewModel.setSearchWord(category.name!!)
////        findNavController().navigate(R.id.action_searchFragment_to_searchResultsFragment)
//        val intent = Intent(requireActivity(), SearchResultsActivity::class.java)
//        intent.putExtra(SERACH_TERM, category.name!!)
//        startActivity(intent)

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

        findNavController().navigate(R.id.action_searchFragment_to_subcategoriesFragment)

    }

    fun process(bitmap: Bitmap) {
        detectInVisionImage(
            null, /* bitmap */
            FirebaseVisionImage.fromBitmap(bitmap))
    }

    private val detector: FirebaseVisionImageLabeler by lazy {
        FirebaseVisionCloudImageLabelerOptions.Builder()
            .build().let { options ->
                FirebaseVision.getInstance().getCloudImageLabeler(options)
            }
    }
    fun detectInImage(image: FirebaseVisionImage): Task<List<FirebaseVisionImageLabel>> {
        return detector.processImage(image)
    }

    private fun detectInVisionImage(
        originalCameraImage: Bitmap?,
        image: FirebaseVisionImage) {
        detectInImage(image)
            .addOnSuccessListener { results ->

            }
            .addOnFailureListener { e -> e.stackTrace}
    }



    fun intializeRecycler(category: List<__Category__>) {


//        val linearLayoutManager = LinearLayoutManager(activity!!, RecyclerView.VERTICAL, false)
        recycler_items_search?.setNestedScrollingEnabled(false);
        recycler_items_search?.layoutManager = GridLayoutManager(requireContext(), 2)
        recycler_items_search?.isNestedScrollingEnabled = false

//        recycler_items_search.setLayoutManager(linearLayoutManager)
        val searchSuggestionsAdapter = SearchSuggestionsAdapter(
            category as ArrayList<__Category__>,
            { category: __Category__ -> searchTerm(category) })
        recycler_items_search?.setAdapter(searchSuggestionsAdapter)
    }

    companion object {

        private const val TAG = "SearchFragment"
        private const val SERACH_TERM = "SERACH_TERM"

        @JvmStatic
        fun newInstance() =
            SearchFragment().apply {

            }

        fun getCloudLabelingItems(items: ArrayList<String>?) {
            Log.d(TAG, "items passed : $items")

            items?.let {
                Log.d(TAG, "items passed NOT NULL : $items")
//                val visionSearchResultsFragment = VisionSearchResultsFragment.newInstance(it)

            }

        }
    }

    fun getCurrentStore() {
        val userUID = FirebaseAuth.getInstance().uid
        val ref =  MyDatabaseUtil.getDatabase().reference.child("customers/$userUID/current_store")

        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(p0: DataSnapshot) {
                val store = p0.getValue(CurrentStore::class.java)

                store?.let {
                    val storeId = store.storeID as String
                    query_hint?.text = "Search ${store.storeName}"
                    getCategories(storeId)
                }
            }

        })

    }
}
