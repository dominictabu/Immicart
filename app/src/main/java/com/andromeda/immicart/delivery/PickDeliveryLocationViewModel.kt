package com.andromeda.immicart.delivery

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.andromeda.immicart.Scanning.persistence.ImmicartRoomDatabase
import com.andromeda.immicart.delivery.persistence.DeliveryRepository
import kotlinx.coroutines.launch

class PickDeliveryLocationViewModel(application: Application) : AndroidViewModel(application) {

    // The ViewModel maintains a reference to the repository to get data.
    private val repository: DeliveryRepository
    // LiveData gives us updated words when they change.
    val allDeliveryLocations: LiveData<List<Place>>

    init {
        // Gets reference to WordDao from WordRoomDatabase to construct
        // the correct WordRepository.
        val deliveryDao = ImmicartRoomDatabase.getDatabase(application, viewModelScope).deliveryDao()
        repository = DeliveryRepository(deliveryDao)
        allDeliveryLocations = repository.allDeliveryLocations
    }


    fun allDeliveryLocations() : LiveData<List<Place>> {
        return allDeliveryLocations
    }


    fun insertDeliveryLocation(place: Place) = viewModelScope.launch {
        repository.insertDeliveryLocation(place)
    }

    fun deleteAllDeliveryLocations() = viewModelScope.launch {
        repository.deleteAllDeliveryLocations()
    }

}