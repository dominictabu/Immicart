package com.andromeda.immicart.delivery.authentication

import com.andromeda.immicart.delivery.choose_store.Store
import com.andromeda.immicart.delivery.choose_store.StoreRepository

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.andromeda.immicart.Scanning.persistence.ImmicartRoomDatabase
import kotlinx.coroutines.launch


// Class extends AndroidViewModel and requires application as a parameter.
class AuthenticationActivityViewModel(application: Application) : AndroidViewModel(application) {

    // The ViewModel maintains a reference to the repository to get data.
    private val repository: StoreRepository
    // LiveData gives us updated words when they change.
    val allStores: LiveData<List<Store>>

    init {
        // Gets reference to WordDao from WordRoomDatabase to construct
        // the correct WordRepository.
        val storeDao = ImmicartRoomDatabase.getDatabase(application, viewModelScope).storeDao()
        repository = StoreRepository(storeDao)
        allStores = repository.allStores
    }

    fun allStores() : LiveData<List<Store>> {
        return allStores
    }

    // The implementation of insert() is completely hidden from the UI.
    // We don't want insert to block the main thread, so we're launching a new
    // coroutine. ViewModels have a coroutine scope based on their lifecycle called
    // viewModelScope which we can use here.
    fun insert(store: Store) = viewModelScope.launch {
        repository.insert(store)
    }

    fun deleteAll() = viewModelScope.launch {
        repository.deleteAll()
    }
}