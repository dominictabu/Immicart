package com.andromeda.immicart.delivery.search.algolia


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.algolia.instantsearch.helper.android.list.autoScrollToStart
import com.andromeda.immicart.R
import com.andromeda.immicart.networking.ImmicartAPIService
import kotlinx.android.synthetic.main.fragment_facet.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_STORE_ID = "storeID"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [FacetFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class FacetFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var storeID: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            storeID = it.getString(ARG_STORE_ID)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_facet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        getAlgoliaCredentails()
        val algoliaCredentails = AlgoliaCredentails("TV1YRRL3K4", "6de0552fc52736f7f2891edbf087b2f9")


        val factory = SearchViewModelFactory(algoliaCredentails,storeID)
        val viewModel = ViewModelProviders.of(requireActivity(), factory)[MyViewModel::class.java]

//        val viewModel = ViewModelProviders.of(requireActivity())[MyViewModel::class.java]

        facetList.let {
            it.adapter = viewModel.adapterFacet
            it.layoutManager = LinearLayoutManager(requireContext())
            it.autoScrollToStart(viewModel.adapterFacet)
        }

        type_facetList.let {
            it.adapter = viewModel.typeAdapterFacet
            it.layoutManager = LinearLayoutManager(requireContext())
            it.autoScrollToStart(viewModel.typeAdapterFacet)
        }

        see_more_txtview?.setOnClickListener {
        }

        cancel_action?.setOnClickListener {
            activity?.finish()
        }

        apply_filter_btn?.setOnClickListener {
            findNavController().popBackStack()

        }
    }


    private val _baseURL = "https://us-central1-immicart-2ca69.cloudfunctions.net/"

    fun getAlgoliaCredentails() {
        ImmicartAPIService.create(_baseURL).algoliaCredentails.enqueue(object : Callback<AlgoliaCredentails> {
            override fun onResponse(call: Call<AlgoliaCredentails>, response: Response<AlgoliaCredentails>) {

                Log.d(TAG, "AlgoliaCredentails : " + response.body()!!)

                val credentails = response.body()
                Log.d(TAG, "AlgoliaCredentails : " + credentails)
                credentails?.let {
                    val factory = SearchViewModelFactory(it, storeID)
                    val viewModel = ViewModelProviders.of(requireActivity(), factory)[MyViewModel::class.java]

//        val viewModel = ViewModelProviders.of(requireActivity())[MyViewModel::class.java]

                    facetList.let {
                        it.adapter = viewModel.adapterFacet
                        it.layoutManager = LinearLayoutManager(requireContext())
                        it.autoScrollToStart(viewModel.adapterFacet)
                    }

                    type_facetList.let {
                        it.adapter = viewModel.typeAdapterFacet
                        it.layoutManager = LinearLayoutManager(requireContext())
                        it.autoScrollToStart(viewModel.typeAdapterFacet)
                    }

                }
            }

            override fun onFailure(call: Call<AlgoliaCredentails>, t: Throwable) {
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
         * @return A new instance of fragment FacetFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FacetFragment().apply {
                arguments = Bundle().apply {
                    putString(storeID, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}