package com.andromeda.immicart.delivery.search

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SortViewModel : ViewModel() {

    val currentSortingOption = MutableLiveData<Int>()

    fun setCurrentSortingOption(id: Int) {
        currentSortingOption.value = id
    }

}