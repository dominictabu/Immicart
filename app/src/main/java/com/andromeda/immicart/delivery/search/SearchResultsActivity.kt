package com.andromeda.immicart.delivery.search

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProviders
import com.andromeda.immicart.R
import com.andromeda.immicart.delivery.search.algolia.MyViewModel


private const val STORE_ID = "STORE_ID"

class SearchResultsActivity : AppCompatActivity() {

    lateinit var myViewModel : MyViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_results)

//        myViewModel = ViewModelProviders.of(this).get(MyViewModel::class.java)
//
//        if(intent.hasExtra(STORE_ID)) {
//            val term = intent.getStringExtra(STORE_ID)
//            myViewModel.setSearchWord(term)
//        }





    }
}
