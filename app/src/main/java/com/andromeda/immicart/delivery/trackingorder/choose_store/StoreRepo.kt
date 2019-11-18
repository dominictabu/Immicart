package com.andromeda.immicart.delivery.trackingorder.choose_store

import android.app.Application
import android.os.AsyncTask

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
