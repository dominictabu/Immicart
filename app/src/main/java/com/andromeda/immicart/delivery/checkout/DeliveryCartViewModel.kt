package com.andromeda.immicart.delivery.checkout

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.andromeda.immicart.Scanning.persistence.CartRepository
import com.andromeda.immicart.Scanning.persistence.ImmicartRoomDatabase
import com.andromeda.immicart.delivery.DeliveryCart
import com.andromeda.immicart.delivery.choose_store.Store
import com.andromeda.immicart.delivery.choose_store.StoreRepository
import com.andromeda.immicart.delivery.persistence.CurrentLocation
import com.andromeda.immicart.delivery.persistence.DeliveryRepository
import kotlinx.coroutines.launch


// Class extends AndroidViewModel and requires application as a parameter.
class DeliveryCartViewModel(application: Application) : AndroidViewModel(application) {

    // The ViewModel maintains a reference to the repository to get data.
    private val repository: CartRepository
    private val deliveryRepository: DeliveryRepository

    val allDeliveryLocations: LiveData<List<CurrentLocation>>
    private val repository_: StoreRepository
    val allStores: LiveData<List<Store>>
    val deliveryDetails = MutableLiveData<DeliveryDetails>()
    val trackingLink = MutableLiveData<String>()
    val sendy_order_number = MutableLiveData<String>()


    val currentStore = MutableLiveData<Store>()
    fun setCurrentStore(word: Store) {
        currentStore.value = word
    }
    // LiveData gives us updated words when they change.
    val allDeliveryItems: LiveData<List<DeliveryCart>>

    init {
        // Gets reference to WordDao from WordRoomDatabase to construct
        // the correct WordRepository.
        val cartDao = ImmicartRoomDatabase.getDatabase(application, viewModelScope).cartDao()
        repository = CartRepository(cartDao)
        allDeliveryItems = repository.allDeliveryItems
        val deliveryDao = ImmicartRoomDatabase.getDatabase(application, viewModelScope).deliveryDao()

        deliveryRepository = DeliveryRepository(deliveryDao)

        allDeliveryLocations = deliveryRepository.allDeliveryLocations
        val storeDao = ImmicartRoomDatabase.getDatabase(application, viewModelScope).storeDao()
        repository_ = StoreRepository(storeDao)
        allStores = repository_.allStores
    }

    fun setDeliveryDetails(orderObject: DeliveryDetails) {
        deliveryDetails.value = orderObject
    }

    fun setTrackingLink(link: String) {
        trackingLink.value = link
    }

    fun setOrderNumber(number: String) {
        sendy_order_number.value = number
    }

    fun currentStores() : LiveData<List<Store>> {
        return allStores
    }

    fun allDeliveryLocations() : LiveData<List<CurrentLocation>> {
        return allDeliveryLocations
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

    fun updateQuantity(id: String, newQuantity : Int) = viewModelScope.launch {
        repository.updateDeliveryItemQuantity(id, newQuantity)
    }
}