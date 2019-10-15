package com.andromeda.immicart.networking

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface ImmicartService {
    @GET("products/{product_id}")
    fun getSingleProductById(
        @Path(
            value = "product_id",
            encoded = true
        ) productId: Int
    ): Call<Model.SingleProduct>

    @GET("product/{product_barcode}")
    suspend fun getSingleProductByBarcode(
        @Path(
            value = "product_barcode",
            encoded = true
        ) productBarcode: String
    ): Call<Model.ResponseData>

    fun create(): ImmicartService {

        return RetrofitClientInstance.getRetrofitInstance().create(ImmicartService::class.java)
    }
}