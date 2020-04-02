package com.andromeda.immicart.delivery.search.visionSearch.imagelabeling

import android.app.Application
import androidx.lifecycle.*
import com.andromeda.immicart.Scanning.persistence.CartRepository
import com.andromeda.immicart.Scanning.persistence.ImmicartRoomDatabase
import com.andromeda.immicart.delivery.DeliveryCart
import com.andromeda.immicart.delivery.__Category__
import com.andromeda.immicart.delivery.choose_store.Store
import com.andromeda.immicart.delivery.choose_store.StoreRepository
import com.andromeda.immicart.delivery.persistence.CurrentLocation
import com.andromeda.immicart.delivery.persistence.DeliveryRepository
import kotlinx.coroutines.launch

class VisionSearchViewModel(application: Application) : AndroidViewModel(application){


    // The ViewModel maintains a reference to the repository to get data.
    private val repository: CartRepository
    // LiveData gives us updated words when they change.
    val allDeliveryItems: LiveData<List<DeliveryCart>>
    private val deliveryRepository: DeliveryRepository


    val labelslist = MutableLiveData<List<String>>()

    fun setLabels(labels: List<String>) {
        labelslist.value = labels
    }

    val storeiD = MutableLiveData<String>()

    fun setStoreID(id: String) {
        storeiD.value = id
    }


    init {
        // Gets reference to WordDao from WordRoomDatabase to construct
        // the correct WordRepository.
        val cartDao = ImmicartRoomDatabase.getDatabase(application, viewModelScope).cartDao()
        val deliveryDao = ImmicartRoomDatabase.getDatabase(application, viewModelScope).deliveryDao()
        repository = CartRepository(cartDao)
        deliveryRepository = DeliveryRepository(deliveryDao)
        allDeliveryItems = repository.allDeliveryItems
    }


    fun allDeliveryItems() : LiveData<List<DeliveryCart>> {
        return allDeliveryItems
    }

    // The implementation of insert() is completely hidden from the UI.
    // We don't want insert to block the main thread, so we're launching a new
    // coroutine. ViewModels have a coroutine scope based on their lifecycle called
    // viewModelScope which we can use here.
    fun insert(cart: DeliveryCart) = viewModelScope.launch {
        repository.insertDeliveryItem(cart)
    }

    fun deleteById(key: Int) = viewModelScope.launch {
        repository.deleteDeliveryItemById(key)
    }

    fun updateQuantity(id: String, newQuantity : Int) = viewModelScope.launch {
        repository.updateDeliveryItemQuantity(id, newQuantity)
    }


}