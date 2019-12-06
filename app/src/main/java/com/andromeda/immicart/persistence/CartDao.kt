package com.andromeda.immicart.Scanning.persistence

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.andromeda.immicart.delivery.DeliveryCart


@Dao
interface CartDao {

    @Query("SELECT * from cart ORDER BY primaryKeyId ASC")
    fun getAllCartItems(): LiveData<List<Cart>>

    @Query("SELECT * from delivery_cart ORDER BY itemId ASC")
    fun getAllDeliveryCartItems(): LiveData<List<DeliveryCart>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(cart: Cart)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDeliveryitem(cart: DeliveryCart)


    @Query("UPDATE cart SET quantity = :quantity  WHERE primaryKeyId = :id")
    suspend fun updateQuantity(id: Int, quantity: Int)

    @Query("UPDATE delivery_cart SET quantity = :quantity  WHERE itemKey = :id")
    suspend fun updateDeliveryItemQuantity(id: String, quantity: Int)

    @Query("DELETE FROM cart")
    suspend fun deleteAll()

    @Query("DELETE FROM delivery_cart")
    suspend fun deleteAllDeliveryItems()

    @Query("DELETE FROM cart WHERE primaryKeyId = :id")
    suspend fun deleteById(id: Int)

    @Query("DELETE FROM delivery_cart WHERE itemId = :id")
    suspend fun deleteByDeliveryItemId(id: Int)


    @Query("SELECT COUNT(primaryKeyId) FROM cart")
    fun getRowCount(): LiveData<Int>

}