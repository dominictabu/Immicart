package com.andromeda.immicart.delivery

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "delivery_cart")
data class DeliveryCart(@PrimaryKey @ColumnInfo(name = "_id") val _id: Int, val barcode: String, val name: String, val price: Int,
                        var quantity: Int, val image_url: String)

data class Images(val id: String, val url : Int)