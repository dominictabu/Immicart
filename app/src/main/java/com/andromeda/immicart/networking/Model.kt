package com.andromeda.immicart.networking

import androidx.room.Entity

object Model {
    data class Result(val query: Query)
    data class Query(val searchinfo: SearchInfo)
    data class SearchInfo(val totalhits: Int)

    data class Category(val _id: Int, val name: String, val link: String)


    data class SingleProduct(val _id: Int, val barcode: String, val name: String, val price: Float,
                             val quantity: Int, val image_url: String, val created: String,
                             val last_updated: String, val description: String, val category: Category)
    data class ResponseData(val data : SingleProduct)

    data class Category_(val _id: Int, val name: String, val created: String, val last_updated: String, val link: String)
    data class Links(val first: String?, val last: String?, val prev: String?, val next: String?)
    data class ScannedProduct(var _id: Int,var barcode: String, var name: String, var price: Float,  var quantity: Int)
}