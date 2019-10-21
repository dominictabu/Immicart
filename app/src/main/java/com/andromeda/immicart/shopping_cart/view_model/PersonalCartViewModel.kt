package com.andromeda.immicart.shopping_cart.view_model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.andromeda.immicart.Scanning.persistence.Cart
import com.andromeda.immicart.Scanning.persistence.CartRepository
import com.andromeda.immicart.Scanning.persistence.ImmicartRoomDatabase
import kotlinx.coroutines.launch



// Class extends AndroidViewModel and requires application as a parameter.
class PersonalCartViewModel(application: Application) : AndroidViewModel(application) {

    // The ViewModel maintains a reference to the repository to get data.
    private val repository: CartRepository
    // LiveData gives us updated words when they change.
    val allScannedItems: LiveData<List<Cart>>

    init {
        // Gets reference to WordDao from WordRoomDatabase to construct
        // the correct WordRepository.
        val cartDao = ImmicartRoomDatabase.getDatabase(application, viewModelScope).cartDao()
        repository = CartRepository(cartDao)
        allScannedItems = repository.allScannedItems
    }

    // The implementation of insert() is completely hidden from the UI.
    // We don't want insert to block the main thread, so we're launching a new
    // coroutine. ViewModels have a coroutine scope based on their lifecycle called
    // viewModelScope which we can use here.
    fun insert(cart: Cart) = viewModelScope.launch {
        repository.insert(cart)
    }

    fun deleteById(id: Int) = viewModelScope.launch {
        repository.deleteById(id)
    }

    fun updateQuantity(id: Int, newQuantity : Int) = viewModelScope.launch {
        repository.updateQuantity(id, newQuantity)
    }
}