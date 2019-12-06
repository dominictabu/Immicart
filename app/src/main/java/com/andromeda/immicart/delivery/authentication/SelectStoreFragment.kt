package com.andromeda.immicart.delivery.authentication


import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.andromeda.immicart.R
import com.andromeda.immicart.delivery.ProductsPageActivity
import com.andromeda.immicart.delivery.choose_store.Store
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.android.synthetic.main.fragment_select_store.*
import java.util.ArrayList

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SelectStoreFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */

class SelectStoreFragment : Fragment() {

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var TAG: String = "SelectStoreFragment"
    private lateinit var authenticationActivityViewModel: AuthenticationActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

        authenticationActivityViewModel =
            ViewModelProviders.of(this).get(AuthenticationActivityViewModel::class.java)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_select_store, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        getAllDocs()
    }

    fun getAllDocs() {
        // [START get_multiple_all]

        loading_view.show()
        val stores = ArrayList<Store>()
        val db = FirebaseFirestore.getInstance();

        val collectionPath = "stores"
        db.collection(collectionPath).addSnapshotListener(EventListener<QuerySnapshot> { queryDocumentSnapshots, e ->
            loading_view.hide()

            Log.d(TAG, "Stores size" + queryDocumentSnapshots!!.size())

            for (document in queryDocumentSnapshots) {
                Log.d(TAG, document.id + " => " + document.data)
                val map = document.data
                val name = map["name"] as String?
                val logoUrl = map["logoUrl"] as String?
                val county = map["county"] as String?
                val address = map["address"] as String?
                val latLng = map["latLng"] as String?
                val number_of_reviews = map["number_of_reviews"] as Long?
                val i = number_of_reviews?.toInt()

                val avg_rating = map["avg_rating"] as Double?
                val _averageRating = avg_rating!!.toFloat()


                val store = Store(document.id, name, logoUrl, county, address, latLng, i!!, _averageRating, true)

                stores.add(store)
            }



            val linearLayoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
            recycler_store.setLayoutManager(linearLayoutManager)

            val storeAdapter = StoreListRecyclerAdapter(stores, {store: Store -> selectStore(store)})

            recycler_store.setAdapter(storeAdapter)
        })
        //        db.collection(collectionPath)
        //                .get()
        //                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
        //                    @Override
        //                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
        //                        if (task.isSuccessful()) {
        //                            loading_view.hide();
        //
        //                            Log.d(TAG, "task.isSuccessful()");
        //
        //                            for (QueryDocumentSnapshot document : task.getResult()) {
        //                                Log.d(TAG, document.getId() + " => " + document.getData());
        //                                Map<String, Object> map = document.getData();
        //                                String name = (String) map.get("name");
        //                                String logoUrl = (String) map.get("logoUrl");
        //                                String county = (String) map.get("county");
        //                                String address = (String) map.get("address");
        //                                String latLng = (String) map.get("latLng");
        //                                Long number_of_reviews = (Long) map.get("number_of_reviews");
        //                                Integer i = number_of_reviews != null ? number_of_reviews.intValue() : null;
        //
        //                                Double avg_rating = (Double) map.get("avg_rating");
        //                                float _averageRating = (float) avg_rating.floatValue();
        //
        //
        //                                    Store store = new Store(document.getId(),name, logoUrl, county, address, latLng, i, _averageRating, true);
        //
        //                                    stores.add(store);
        //
        //
        //
        //
        //                            }
        //
        //                            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        //
        //
        //                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        //                            list_store_recycler.setLayoutManager(linearLayoutManager);
        //
        //                            StoreAdapter storeAdapter = new StoreAdapter(stores, getActivity(), latLng,ListOfStoresFragment.this);
        //
        //                            list_store_recycler.setAdapter(storeAdapter);
        //                        } else {
        //                            Log.d(TAG, "Error getting documents: ", task.getException());
        //                        }
        //                    }
        //                });
        // [END get_multiple_all]
    }


    fun selectStore(store: Store) {
        Log.d(TAG, "Selected Store: "  + store)
        authenticationActivityViewModel.deleteAll()
        authenticationActivityViewModel.insert(store)
        startActivity(Intent(context, ProductsPageActivity::class.java))

    }



    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SelectStoreFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SelectStoreFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
