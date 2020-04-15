package com.andromeda.immicart.delivery.search

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProviders
import com.andromeda.immicart.R
import com.andromeda.immicart.delivery.ProductsViewModel
import com.andromeda.immicart.delivery.choose_store.Store
import com.andromeda.immicart.delivery.search.algolia.MyViewModel


private const val CURRENT_STORE = "CURRENT_STORE"

class SearchResultsActivity : AppCompatActivity() {

    lateinit var myViewModel : MyViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_results)

        val _viewModel = ViewModelProviders.of(this).get(ProductsViewModel::class.java)

        if(intent.hasExtra(CURRENT_STORE)) {

            val store = intent.getSerializableExtra(CURRENT_STORE) as Store

            _viewModel.setCurrentStore(store)

        }

//        myViewModel = ViewModelProviders.of(this).get(MyViewModel::class.java)
//
//        if(intent.hasExtra(STORE_ID)) {
//            val term = intent.getStringExtra(STORE_ID)
//            myViewModel.setSearchWord(term)
//        }





    }
}
