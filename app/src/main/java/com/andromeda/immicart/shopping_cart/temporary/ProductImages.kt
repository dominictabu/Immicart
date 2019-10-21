package com.andromeda.immicart.shopping_cart.temporary

import com.google.gson.annotations.SerializedName

data class ProductImages(@SerializedName("filename")
                         var imageUrl: String? = null)