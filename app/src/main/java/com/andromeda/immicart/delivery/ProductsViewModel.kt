package com.andromeda.immicart.delivery

import android.app.Application
import androidx.lifecycle.*
import com.andromeda.immicart.Scanning.persistence.CartRepository
import com.andromeda.immicart.Scanning.persistence.ImmicartRoomDatabase
import com.andromeda.immicart.delivery.persistence.DeliveryRepository
import kotlinx.coroutines.launch


// Class extends AndroidViewModel and requires application as a parameter.
class ProductsViewModel(application: Application) : AndroidViewModel(application) {

    // The ViewModel maintains a reference to the repository to get data.
    private val repository: CartRepository
    // LiveData gives us updated words when they change.
    val allDeliveryItems: LiveData<List<DeliveryCart>>
    private val deliveryRepository: DeliveryRepository

    val allDeliveryLocations: LiveData<List<Place>>


    val categoryId = MutableLiveData<Int>()

    init {
        // Gets reference to WordDao from WordRoomDatabase to construct
        // the correct WordRepository.
        val cartDao = ImmicartRoomDatabase.getDatabase(application, viewModelScope).cartDao()
        val deliveryDao = ImmicartRoomDatabase.getDatabase(application, viewModelScope).deliveryDao()
        repository = CartRepository(cartDao)
        deliveryRepository = DeliveryRepository(deliveryDao)
        allDeliveryItems = repository.allDeliveryItems
        allDeliveryLocations = deliveryRepository.allDeliveryLocations
    }

    fun allDeliveryLocations() : LiveData<List<Place>> {
        return allDeliveryLocations
    }


    fun setCategoryId(id: Int) {
        categoryId.value = id
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

    fun deleteById(id: Int) = viewModelScope.launch {
        repository.deleteDeliveryItemById(id)
    }

    fun updateQuantity(id: Int, newQuantity : Int) = viewModelScope.launch {
        repository.updateDeliveryItemQuantity(id, newQuantity)
    }
}