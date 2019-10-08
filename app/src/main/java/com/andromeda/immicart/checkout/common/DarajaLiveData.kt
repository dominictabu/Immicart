package com.andromeda.immicart.checkout.common

import androidx.lifecycle.LiveData
import com.andromeda.immicart.mpesa.callback.DarajaException
import com.andromeda.immicart.mpesa.callback.DarajaListener

class DarajaLiveData<T> : LiveData<Resource<T>>(), DarajaListener<T> {

    init {
        value = Resource(Status.LOADING)
    }

    override fun onResult(data: T) {
        value = Resource(data)
    }

    override fun onError(exception: DarajaException) {
        value = Resource(DarajaException(exception.localizedMessage))
    }
}