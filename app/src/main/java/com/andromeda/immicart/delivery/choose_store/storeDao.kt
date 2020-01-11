package com.andromeda.immicart.delivery.choose_store

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface storeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(store: Store)

    @Query("SELECT * FROM store")
    fun loadAllStores(): LiveData<List<Store>>

    @Query("DELETE FROM store")
    suspend fun deleteAll()



}