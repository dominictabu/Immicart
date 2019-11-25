package com.andromeda.immicart.delivery.choose_store

import androidx.lifecycle.LiveData

class StoreRepository(private val storeDao: storeDao) {

    val allStores: LiveData<List<Store>> = storeDao.loadAllStores()


    suspend fun insert(store: Store) {
        storeDao.insert(store = store)
    }



    suspend fun deleteAll() {
        storeDao.deleteAll()
    }



}
