package com.andromeda.immicart.Scanning.persistence

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart")
data class Cart(@PrimaryKey(autoGenerate = true) var primaryKeyId: Int = 0,@ColumnInfo(name = "_id") val _id: Int, val barcode: String, val name: String, val price: Float,
                val quantity: Int, val image_url: String)
