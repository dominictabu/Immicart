package com.andromeda.immicart.delivery.persistence

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.andromeda.immicart.delivery.Place

@Dao
interface DeliveryDao {

    @Query("SELECT * from place")
    fun getAllDeliveryLocations(): LiveData<List<Place>>

    @Query("DELETE FROM place")
    suspend fun deleteAllDeliveryLocations()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDeliveryLocation(place: Place)

}


class DeliveryRepository(private val deliveryDao: DeliveryDao) {

    val allDeliveryLocations: LiveData<List<Place>> = deliveryDao.getAllDeliveryLocations()

    suspend fun insertDeliveryLocation(place: Place) {
        deliveryDao.insertDeliveryLocation(place = place)
    }

    suspend fun deleteAllDeliveryLocations() {
        deliveryDao.deleteAllDeliveryLocations()
    }

}
