package com.andromeda.immicart.Scanning.persistence

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query


@Dao
interface CartDao {

    @Query("SELECT * from cart ORDER BY primaryKeyId ASC")
    fun getAllCartItems(): LiveData<List<Cart>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(cart: Cart)


    @Query("UPDATE cart SET quantity = :quantity  WHERE primaryKeyId = :id")
    suspend fun updateQuantity(id: Int, quantity: Int)

    @Query("DELETE FROM cart")
    suspend fun deleteAll()

    @Query("DELETE FROM cart WHERE primaryKeyId = :id")
    suspend fun deleteById(id: Int)

    @Query("SELECT COUNT(primaryKeyId) FROM cart")
    fun getRowCount(): LiveData<Int>

}