package com.andromeda.immicart.delivery.furniture.algolia


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
import androidx.recyclerview.widget.LinearLayoutManager
import com.algolia.instantsearch.core.connection.ConnectionHandler
import com.algolia.instantsearch.helper.android.list.autoScrollToStart
import com.algolia.instantsearch.helper.android.searchbox.SearchBoxViewAppCompat
import com.algolia.instantsearch.helper.android.searchbox.connectView
import com.algolia.instantsearch.helper.android.stats.StatsTextView
import com.algolia.instantsearch.helper.stats.StatsPresenterImpl
import com.algolia.instantsearch.helper.stats.connectView

import com.andromeda.immicart.R
import kotlinx.android.synthetic.main.fragment_furniture_search.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [FurnitureSearchFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class FurnitureSearchFragment : Fragment() {
    // TODO: Rename and change types of parameters

    private var param1: String? = null
    private var param2: String? = null
    private val connection = ConnectionHandler()
    private  val TAG = "ProductFragment"

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
        return inflater.inflate(R.layout.fragment_furniture_search, container, false)
    }
    override fun onDestroyView() {
        super.onDestroyView()
        connection.disconnect()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val viewModel = ViewModelProviders.of(requireActivity())[MyViewModel::class.java]

        viewModel.products.observe(this, Observer { hits ->
            Log.d(TAG, "Hits: " + hits)
            viewModel.adapterProduct.submitList(hits) })

        val searchBoxView = SearchBoxViewAppCompat(search_view_search_fragment)

        connection += viewModel.searchBox.connectView(searchBoxView)
        val statsView = StatsTextView(stats)
        connection += viewModel.stats.connectView(statsView, StatsPresenterImpl())
        products_items_recycler.let {
            it.itemAnimator = null
            it.adapter = viewModel.adapterProduct
            it.layoutManager = GridLayoutManager(requireContext(), 2)
            it.autoScrollToStart(viewModel.adapterProduct)
        }

        filters?.setOnClickListener {
            findNavController().navigate(R.id.action_furnitureSearchFragment2_to_facetFragment3)
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FurnitureSearchFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FurnitureSearchFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
