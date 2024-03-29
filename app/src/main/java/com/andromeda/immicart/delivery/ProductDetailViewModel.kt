package com.andromeda.immicart.delivery

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.andromeda.immicart.Scanning.persistence.CartRepository
import com.andromeda.immicart.Scanning.persistence.ImmicartRoomDatabase
import com.andromeda.immicart.delivery.choose_store.Store
import com.andromeda.immicart.delivery.choose_store.StoreRepository
import kotlinx.coroutines.launch


class ProductDetailViewModel(application: Application) : AndroidViewModel(application) {

    // The ViewModel maintains a reference to the repository to get data.
    private val repository: CartRepository
    // LiveData gives us updated words when they change.
    val allDeliveryItems: LiveData<List<DeliveryCart>>

    private val repository_: StoreRepository
    // LiveData gives us updated words when they change.
    val allStores: LiveData<List<Store>>

    init {
        // Gets reference to WordDao from WordRoomDatabase to construct
        // the correct WordRepository.
        val cartDao = ImmicartRoomDatabase.getDatabase(application, viewModelScope).cartDao()
        repository = CartRepository(cartDao)
        allDeliveryItems = repository.allDeliveryItems
        val storeDao = ImmicartRoomDatabase.getDatabase(application, viewModelScope).storeDao()
        repository_ = StoreRepository(storeDao)
        allStores = repository_.allStores
    }

    fun currentStores() : LiveData<List<Store>> {

        return allStores
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