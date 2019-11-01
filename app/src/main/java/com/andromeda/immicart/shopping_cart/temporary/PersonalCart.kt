package com.andromeda.immicart.shopping_cart.temporary

import com.google.gson.annotations.SerializedName

data class PersonalCart(
    @SerializedName("id")
    var id: Int? = null,

    @SerializedName("name")
    var itemName: String? = null,

    @SerializedName("description")
    var description: String? = null,

    @SerializedName("name")
    var discountedPrice: String? = null,

    @SerializedName("price")
    var sellingPrice: Int? = null,

    @SerializedName("amount")
    var amount: String? = null,

    @SerializedName("image")
    var itemImage: List<ProductImages> = arrayListOf()
)