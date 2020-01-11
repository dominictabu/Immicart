package com.andromeda.immicart.delivery.choose_store

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.andromeda.immicart.R
import com.andromeda.immicart.delivery.authentication.StoreListRecyclerAdapter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.android.synthetic.main.activity_choose_store.*
import kotlinx.android.synthetic.main.content_choose_store.*
import java.util.*

class ChooseStoreActivity : AppCompatActivity() {

    private var TAG = "ChooseStoreActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_store)

        getAllDocs()


        closeBtn.setOnClickListener {
            finish()
        }
    }


    fun getAllDocs() {
        // [START get_multiple_all]

        loading_view.show()
        val stores = ArrayList<Store>()
        val db = FirebaseFirestore.getInstance();

        val collectionPath = "stores"
        db.collection(collectionPath).get().addOnSuccessListener {
            loading_view.hide()

            Log.d(TAG, "Stores size" + it!!.size())
            for (document in it) {
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

            val linearLayoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
            recycler_store.setLayoutManager(linearLayoutManager)

            val storeAdapter = StoreListRecyclerAdapter(stores, {store: Store -> selectStore(store)})

            recycler_store.setAdapter(storeAdapter)


        }
    }


    fun selectStore(store: Store) {

    }

}
