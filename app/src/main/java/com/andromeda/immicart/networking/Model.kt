package com.andromeda.immicart.networking

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.android.gms.common.internal.FallbackServiceBroker

object Model {
    data class Result(val query: Query)
    data class Query(val searchinfo: SearchInfo)
    data class SearchInfo(val totalhits: Int)

    //Product
    data class Category(val _id: Int, val name: String, val link: String)

    data class Product(val _id: Int, val barcode: String, val name: String,val price: Float, val quantity: Int, val image_url: String, val id_link: String, val code_link: String, var isInCart: Boolean = false)

    data class SingleProduct(val _id: Int, val barcode: String, val name: String, val price: Float,
                             val quantity: Int, val image_url: String, val created: String,
                             val last_updated: String, val description: String, val category: Category)
    data class ResponseData(val data : SingleProduct)


    //Get Paginated Categories
    data class Category_(val _id: Int, val name: String, val created: String?, val last_updated: String?, val link: String)
    data class Links(val first: String?, val last: String?, val prev: String?, val next: String?)
    data class CategoryMeta(val current_page: Int, val from: String, val last_page: Int, val path: String, val per_page: Int, val to: Int, val total: Int)
    data class PaginatedCategory(val data: List<Category_>?, val links: Links?, val meta: CategoryMeta?)

    //GET Single Category
    data class Category__(val _id: Int, val name: String, val link: String)
    data class Product_(val _id: Int, val barcode: String, val name: String,val price: Int, val quantity: Int, val image_url: String, val id_link: String, val code_link: String, var isInCart : Boolean? = false)
    data class SingleCategoryData(val _id: Int, val name: String, val created:String, val last_updated: String, val children: List<Category__>?, val products: List<Product_>?)

    data class ProductCart_(val _id: Int, val barcode: String, val name: String,val price: Int, val quantity: Int, val image_url: String, val id_link: String, val code_link: String, var isInCart : Boolean = false)

    data class ResponseCategoryData(val data: SingleCategoryData)
    data class ScannedProduct(var _id: Int,var barcode: String, var name: String, var price: Float,  var quantity: Int)


    //PAYMENTS
    data class MPESAResponse(val error: String)
}