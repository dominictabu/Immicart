package com.andromeda.immicart.delivery.search.visionSearch.imagelabeling

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class VisionSearchViewModel : ViewModel(){

    val labelslist = MutableLiveData<List<String>>()

    fun setLabels(labels: List<String>) {
        labelslist.value = labels
    }


}