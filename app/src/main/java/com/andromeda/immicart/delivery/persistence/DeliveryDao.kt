package com.andromeda.immicart.delivery.persistence

import androidx.annotation.NonNull
import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface DeliveryDao {

    @Query("SELECT * from current_location")
    fun getAllDeliveryLocations(): LiveData<List<CurrentLocation>>

    @Query("SELECT * from delivery_locations")
    fun getAllOtherDeliveryLocations(): LiveData<List<DeliveryLocation>>

    @Query("DELETE FROM current_location")
    suspend fun deleteAllDeliveryLocations()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDeliveryLocation(place: CurrentLocation)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInAllDeliveryLocation(locations: DeliveryLocation)

    @Query("DELETE FROM delivery_locations")
    suspend fun deleteAllOtherDeliveryLocations()
}


class DeliveryRepository(private val deliveryDao: DeliveryDao) {

    val allDeliveryLocations: LiveData<List<CurrentLocation>> = deliveryDao.getAllDeliveryLocations()
    val allOtherDeliveryLocations: LiveData<List<DeliveryLocation>> = deliveryDao.getAllOtherDeliveryLocations()

    suspend fun insertCurrentDeliveryLocation(place: CurrentLocation) {
        deliveryDao.insertDeliveryLocation(place = place)
    }

    suspend fun insertInAllDeliveryLocation(locations: DeliveryLocation) {
        deliveryDao.insertInAllDeliveryLocation(locations = locations)

    }

    suspend fun deleteAllOtherDeliveryLocations() {
        deliveryDao.deleteAllOtherDeliveryLocations()
    }
    suspend fun deleteAllDeliveryLocations() {
        deliveryDao.deleteAllDeliveryLocations()
    }

}

@Entity(tableName = "delivery_locations")
data class DeliveryLocation(@NonNull @PrimaryKey var placeID: String,
                            var name: String? = null,
                            var address: String? = null,
                            var placeFullText: String? = null,
                            var latLng: String,
                            var isSelected: Boolean = false )
@Entity(tableName = "current_location")
data class CurrentLocation(@NonNull @PrimaryKey var placeID: String,
                            var name: String? = null,
                            var address: String? = null,
                            var placeFullText: String? = null,
                            var latLng: String,
                            var isSelected: Boolean = false )

