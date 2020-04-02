package com.andromeda.immicart.delivery.search.visionSearch.imagelabeling

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProviders
import com.andromeda.immicart.R

class ImageLabelingActivity : AppCompatActivity() {

    private  val STORE_ID = "STORE_ID"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_labeling)
        val visionSearchViewModel = ViewModelProviders.of(this).get(VisionSearchViewModel::class.java)

        if(intent.hasExtra(STORE_ID)) {
            val storeID = intent.getStringExtra(STORE_ID)
            visionSearchViewModel.setStoreID(storeID)
        }

    }
}
