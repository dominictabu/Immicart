package com.andromeda.immicart.delivery

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

data class DeliveryCart_(@PrimaryKey @ColumnInfo(name = "_id") val _id: Int, val barcode: String, val name: String, val price: Int,
                        var quantity: Int, val image_url: String)
@Entity(tableName = "delivery_cart")
data class DeliveryCart(@ColumnInfo(name = "itemKey") val key: String, val barcode: String, val name: String, val offerPrice: Int, val normalPrice: Int,
                        var quantity: Int, val image_url: String) {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "itemId")
    var itemId: Int = 0
}
data class __Product__(val key: String, val barcode: String, val name: String, val offerPrice: Int, val normalPrice: Int,
                        var quantity: Int, val image_url: String, var isInCart: Boolean)


data class __Category__(val key: String? = null, val name: String? = null, val hasChildren: Boolean? = false)

//data class DeliveryCart(@PrimaryKey @ColumnInfo(name = "_id") val _id: Int, val barcode: String, val name: String, val price: Int,
//                        var quantity: Int, val image_url: String)

data class Images(val id: String, val url : Int)

data class PlaceOrder(
    val deliveryAddress: HashMap<String, Any>? = null,
    val customerUID: String? = null,
    val storeFee: Float? = null,
    val serviceFee: Float? = null,
    val deliveryFee: Float? = null,
    val items: List<DeliveryCart>? = null,
    val store: Store? = null
)

data class Store(val _id: Int? = null, val name: String? = null, val address: String? = null, val latLng: String? = null, val image_url: String? = null)
