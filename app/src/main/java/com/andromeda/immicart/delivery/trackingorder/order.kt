package com.andromeda.immicart.delivery.trackingorder


data class Order(val _id: Int, val store: Store, var products: List<Product>?, val singleLiner: String, val orderTotal: String)
data class Store(val name: String, val address: String, val latLng: String, val image_url: String)
data class Product(val name: Int, val image_url: String, val unit_Price: String, val quantity: Int)
