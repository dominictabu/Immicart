package com.andromeda.immicart.delivery.search.visionSearch.imagelabeling


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.algolia.search.saas.AlgoliaException
import com.algolia.search.saas.Client
import com.algolia.search.saas.CompletionHandler
import com.algolia.search.saas.Query

import com.andromeda.immicart.R
import com.andromeda.immicart.delivery.DeliveryCart
import com.andromeda.immicart.delivery.ProductsAdapter
import kotlinx.android.synthetic.main.fragment_vision_search_results.*
import org.json.JSONArray
import org.json.JSONObject




// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_ITEMS_LIST = "ARG_ITEMS_LIST"
private const val ARG_LABELS = "ARG_LABELS"

/**
 * A simple [Fragment] subclass.
 * Use the [VisionSearchResultsFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class VisionSearchResultsFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var items: ArrayList<DeliveryCart>? = null
    private var labels: ArrayList<String>? = null
    private lateinit var visionSearchViewModel: VisionSearchViewModel
    private lateinit var productsAdapter: ProductsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            items = it.getParcelableArrayList<DeliveryCart>(ARG_ITEMS_LIST)
            labels = it.getStringArrayList(ARG_LABELS)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_vision_search_results, container, false)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        visionSearchViewModel = ViewModelProviders.of(activity!!).get(VisionSearchViewModel::class.java)

        val gridLayoutManager = GridLayoutManager(activity,2 )

//        products_items_recycler.setNestedScrollingEnabled(false);

        products_items_recycler?.setLayoutManager(gridLayoutManager)
        productsAdapter = ProductsAdapter(requireActivity(), { cartItem : DeliveryCart, newQuantity: Int -> cartItemClicked(cartItem, newQuantity)})

        products_items_recycler?.setAdapter(productsAdapter)



        visionSearchViewModel.labelslist.observe(activity!!, Observer {
            algoliaSearch(it)

        })

        myBackIcon?.setOnClickListener {
            findNavController().popBackStack()

        }


    }
    val deliveryCarts  = ArrayList<DeliveryCart>()
    var hitsLength = 0

    fun algoliaSearch(labels: List<String>) {
        val client = Client("latency", "3d9875e51fbd20c7754e65422f7ce5e1")
        val index = client.getIndex("bestbuy")

        val completionHandler = object : CompletionHandler {
            override fun requestCompleted(p0: JSONObject?, p1: AlgoliaException?) {
                Log.d(TAG, "requestCompleted JSONObject : $p0")

                p0?.let {
                    val hits = p0.get("hits") as JSONArray
                    Log.d(TAG, "Hits decoded: $hits")
                    hitsLength += hits.length()

                    for (i in 0 until hits.length()) {
                        val hit = hits.getJSONObject(i)
                        val name = hit.getString("name")
                        val category = hit.getString("category")
                        val image = hit.getString("image")
                        val salePrice = hit.getString("salePrice")
                        val intPrice = salePrice.toFloat().toInt()
                        val objectID = hit.getString("objectID")
                        val deliveryCart = DeliveryCart(objectID, objectID, name, category, intPrice, 10, 1,image)
                        deliveryCarts.add(deliveryCart)
                    }

                    stats?.text = "${hitsLength} similar items found"
                    productsAdapter.updateList(deliveryCarts, "123")

//                    val hitsObjects = hits[0] as JSONObject
//
//                    hitsObjects.forEach {
//                        val name = it.getString("name")
//                        val category = it.getString("category")
//                        val image = it.getString("image")
//                        val salePrice = it.getString("salePrice")
//                        val intPrice = salePrice.toFloat().toInt()
//                        val objectID = it.getString("objectID")
//                        val deliveryCart = DeliveryCart(objectID, objectID, name, category, intPrice, 10, 1,image)
//                        deliveryCarts.add(deliveryCart)
//                    }


                }


            }

        }
// Search for a first result

        index.searchAsync(Query(labels[0]), completionHandler)

//        labels.forEachIndexed { i, element ->
//            if(i == 0) {
//                index.searchAsync(Query(element), completionHandler)
//
//            }
//        }
//        labels.forEach {
//
//        }

    }

    fun initializeRecycler(deliveryCarts: ArrayList<DeliveryCart>) {
//        val gridLayoutManager = GridLayoutManager(activity,2 )
//
////        products_items_recycler.setNestedScrollingEnabled(false);
//
//        products_items_recycler?.setLayoutManager(gridLayoutManager)
//        val productsAdapter = ProductsAdapter(requireActivity(), { cartItem : DeliveryCart, newQuantity: Int -> cartItemClicked(cartItem, newQuantity)})
//
//        products_items_recycler?.setAdapter(productsAdapter)
        productsAdapter.updateList(deliveryCarts, "123")
    }

    private fun cartItemClicked(cartItem : DeliveryCart, newQuantity: Int) {
//        Toast.makeText(this, "Clicked: ${cartItem.name}", Toast.LENGTH_LONG).show()
        //TODO Change Quantity
        Log.d(TAG, "newQuantity : $newQuantity , $cartItem ")

//
//        if (cartItems.contains(cartItem)) {
//            Log.d(TAG, "CartItems contain the item")
//            viewModel?.updateQuantity(cartItem.key, newQuantity)
//
//        } else {
//            Log.d(TAG, "CartItems DO NOT  contain the item")
//
//            viewModel?.insert(cartItem)
//        }

    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment VisionSearchResultsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(items: ArrayList<DeliveryCart>) =
            VisionSearchResultsFragment().apply {
                arguments = Bundle().apply {
                    putStringArrayList(ARG_LABELS, labels)
                    putParcelableArrayList(ARG_ITEMS_LIST, items)


                }
            }
        private const val TAG = "VisionSearchResultsFrag"

    }
}
