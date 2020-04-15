package com.andromeda.immicart.delivery.checkout

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties

data class DeliveryDetails(val phone: String, val year: Int, val dayOfDelivery: Int, val timeOfDelivery: String, val storeSubtotal: Int,
                            val deliveryFee: Int, val shoppersTip: Int, val total: Int)


data class GeoCoderPoint(val orderID: String,val numberOfItems: Int,val dayOfDelivery: Int,val timeOfDeliveryInInt: Int, val timeOfDelivery: String,val deliveryLatLng: String,
                         val storeName: String,val storeLatLng: String,val earningEstimate: Int)


data class locLatLng(val latitude: Double, val longitude: Double)
data class storeInfo(val name: String, val address: String, val logo: String)
data class deliveryInfo(val name: String, val address: String)
@IgnoreExtraProperties
data class Post(
    var orderID: String? = "",
    var customerID: String? = "",
    var storeID: String? = "",
    var deliveryLocation: locLatLng? = null,
    var storeLocation: locLatLng? = null,
    var timeOfDelivery: String?= "",
    var timeOfDeliveryInInt: Int? = -1,
    var assigned: Boolean? = false,
    var batched: Boolean? = false,
    var numberOfItems: Int? = 0,
    var earningEstimate: Int? = 0,
    var storeInfo : storeInfo? = null,
    var deliveryInfo : deliveryInfo? = null
) {

    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "orderID" to orderID,
            "customerID" to customerID,
            "storeID" to storeID,
            "deliveryLocation" to deliveryLocation,
            "storeLocation" to storeLocation,
            "timeOfDelivery" to timeOfDelivery,
            "timeOfDeliveryInInt" to timeOfDeliveryInInt,
            "batched" to batched,
            "assigned" to assigned,
            "numberOfItems" to numberOfItems,
            "earningEstimate" to earningEstimate,
            "storeInfo" to storeInfo,
            "deliveryInfo" to deliveryInfo
        )
    }
}