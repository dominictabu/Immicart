package com.andromeda.immicart.delivery

import android.app.Application
import androidx.lifecycle.*
import com.andromeda.immicart.Scanning.persistence.CartRepository
import com.andromeda.immicart.Scanning.persistence.ImmicartRoomDatabase
import com.andromeda.immicart.delivery.choose_store.Store
import com.andromeda.immicart.delivery.choose_store.StoreRepository
import com.andromeda.immicart.delivery.persistence.CurrentLocation
import com.andromeda.immicart.delivery.persistence.DeliveryRepository
import kotlinx.coroutines.launch


// Class extends AndroidViewModel and requires application as a parameter.
class ProductsViewModel(application: Application) : AndroidViewModel(application) {

    // The ViewModel maintains a reference to the repository to get data.
    private val repository: CartRepository
    // LiveData gives us updated words when they change.
    val allDeliveryItems: LiveData<List<DeliveryCart>>
    private val deliveryRepository: DeliveryRepository

    val allDeliveryLocations: LiveData<List<CurrentLocation>>

    private val repository_: StoreRepository
    // LiveData gives us updated words when they change.
    val allStores: LiveData<List<Store>>


    val categoryId = MutableLiveData<String>()
    val searchWord = MutableLiveData<String>()
    val categoryParent = MutableLiveData<__Category__>()
    val categoryChildOne = MutableLiveData<__Category__>()
    val categoryLastChild = MutableLiveData<__Category__>()

    init {
        // Gets reference to WordDao from WordRoomDatabase to construct
        // the correct WordRepository.
        val cartDao = ImmicartRoomDatabase.getDatabase(application, viewModelScope).cartDao()
        val deliveryDao = ImmicartRoomDatabase.getDatabase(application, viewModelScope).deliveryDao()
        repository = CartRepository(cartDao)
        deliveryRepository = DeliveryRepository(deliveryDao)
        allDeliveryItems = repository.allDeliveryItems
        allDeliveryLocations = deliveryRepository.allDeliveryLocations
        val storeDao = ImmicartRoomDatabase.getDatabase(application, viewModelScope).storeDao()
        repository_ = StoreRepository(storeDao)
        allStores = repository_.allStores
    }

    fun currentStores() : LiveData<List<Store>> {

        return allStores
    }

    fun allDeliveryLocations() : LiveData<List<CurrentLocation>> {
        return allDeliveryLocations
    }




    fun setCategoryId(id: String) {
        categoryId.value = id
    }
    fun setSearchWord(word: String) {
        searchWord.value = word
    }

    fun setCategoryParent(category: __Category__) {
        categoryParent.value = category
    }

    fun setCategoryChildOne(category: __Category__) {
        categoryChildOne.value = category
    }

    fun setCategoryLastChild(category: __Category__) {
        categoryLastChild.value = category
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