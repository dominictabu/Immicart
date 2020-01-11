package com.andromeda.immicart.delivery.trackingorder

import android.location.Location
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class OrdersViewModel : ViewModel() {

    val selectedOrder = MutableLiveData<OrderObject>()
    val selectedOrderID = MutableLiveData<String>()
    val currentLocation = MutableLiveData<Location>()

    fun setSelectedOrder(orderObject: OrderObject) {
        selectedOrder.value = orderObject
    }
    fun setSelectedOrderID(orderObject: String) {
        selectedOrderID.value = orderObject
    }

    fun setCurrentLocation(location: Location) {
        currentLocation.value = location
    }

}