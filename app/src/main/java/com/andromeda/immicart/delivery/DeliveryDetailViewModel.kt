package com.andromeda.immicart.delivery

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.andromeda.immicart.Scanning.persistence.CartRepository
import com.andromeda.immicart.Scanning.persistence.ImmicartRoomDatabase
import com.andromeda.immicart.delivery.persistence.DeliveryRepository

class DeliveryDetailViewModel(application: Application) : AndroidViewModel(application) {
    private val deliveryRepository: DeliveryRepository
    val allDeliveryLocations: LiveData<List<Place>>

    private val repository: CartRepository
    val allDeliveryItems: LiveData<List<DeliveryCart>>


    init {
        // Gets reference to WordDao from WordRoomDatabase to construct
        // the correct WordRepository.
        val cartDao = ImmicartRoomDatabase.getDatabase(application, viewModelScope).cartDao()
        repository = CartRepository(cartDao)

        val deliveryDao = ImmicartRoomDatabase.getDatabase(application, viewModelScope).deliveryDao()
        deliveryRepository = DeliveryRepository(deliveryDao)
        allDeliveryItems = repository.allDeliveryItems

        allDeliveryLocations = deliveryRepository.allDeliveryLocations
    }


    fun allDeliveryLocations() : LiveData<List<Place>> {
        return allDeliveryLocations
    }

    fun allDeliveryItems() : LiveData<List<DeliveryCart>> {
        return allDeliveryItems
    }


}