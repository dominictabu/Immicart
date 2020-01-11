package com.andromeda.immicart.delivery.account

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


class MyAccountViewModel : ViewModel() {

    val shareAppDynamicLink = MutableLiveData<String>()


    fun setShareAppDynamicLink(link: String) {
        shareAppDynamicLink.value = link
    }



}