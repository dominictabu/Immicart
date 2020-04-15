package com.andromeda.immicart.delivery.authentication

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.RadioGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.andromeda.immicart.R
import com.andromeda.immicart.delivery.CurrentStore
import com.andromeda.immicart.delivery.ProductsPageActivity
import com.andromeda.immicart.delivery.Utils.MyDatabaseUtil
import com.andromeda.immicart.delivery.choose_store.MapActivity
import com.andromeda.immicart.delivery.choose_store.Store
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.android.synthetic.main.fragment_select_store.*
import java.util.ArrayList

class SelectStoreActivity : AppCompatActivity() {

    private var TAG: String = "SelectStoreActivity"
    private lateinit var database: DatabaseReference

    val PREF_NAME = "STOREID"
    val keyChannel = "IS_PICKUP"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_select_store)
        database = MyDatabaseUtil.getDatabase().reference

//        val editor = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit()



        segmented2?.setOnCheckedChangeListener(object : RadioGroup.OnCheckedChangeListener {
            override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
                if(checkedId == R.id.button_delivery) {
                    val editor = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit()
                    editor.putBoolean(keyChannel, false)
                    editor.apply()

//                } else {
//                    val editor = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit()
//                    editor.putBoolean(keyChannel, true)
//                    editor.apply()
                }
            }
        })

        getAllDocs()
        fab?.setOnClickListener {
            startActivity(Intent(this, MapActivity::class.java))
        }

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

            val linearLayoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
            recycler_store.setLayoutManager(linearLayoutManager)

            val storeAdapter = StoreListRecyclerAdapter(stores, {store: Store -> selectStore(store)})
            recycler_store.setAdapter(storeAdapter)
        })
    }

    val CURRENT_STORE = "CURRENT_STORE"
    fun selectStore(store: Store) {
        Log.d(TAG, "Selected Store: "  + store)

//        val editor = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit()
//        editor.putString(keyChannel, store.key)
//        editor.apply()
//
//        val intent = Intent(this@SelectStoreActivity, ProductsPageActivity::class.java)
//        intent.putExtra(CURRENT_STORE, store)
//
//        startActivity(intent)


        val intent = Intent(this@SelectStoreActivity, ProductsPageActivity::class.java)
        intent.putExtra(CURRENT_STORE, store)
        startActivity(intent)


//        findNavController().navigate(R.id.create_account_action)
//        startActivity(Intent(context, ProductsPageActivity::class.java))

    }


    fun setCurrentStore(store: Store) {
        val storeMap = HashMap<String, Any>()
        storeMap.put("storeID", store.key)
        storeMap.put("storeName", store.name)
        storeMap.put("storeLogo", store.logoUrl)
        storeMap.put("storeLatLng", store.latlng)
        storeMap.put("storeAddress", store.address)


        val userUID = FirebaseAuth.getInstance().uid
        val ref = database.child("customers/$userUID").child("current_store")
        ref.setValue(storeMap).addOnSuccessListener {
            Log.d(TAG, "Store selected successfully")
            startActivity(Intent(this@SelectStoreActivity, ProductsPageActivity::class.java))

        }


    }

}
