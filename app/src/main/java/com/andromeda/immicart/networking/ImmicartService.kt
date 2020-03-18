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

    @GET("product/{product_barcode}")
    suspend fun makeSendyDeliveryRequest(
        @Path(
            value = "product_barcode",
            encoded = true
        ) productBarcode: String
    ): Call<Model.SendyRequestResponse>

    fun create(baseURL : String): ImmicartService {

        return RetrofitClientInstance.getRetrofitInstance(baseURL).create(ImmicartService::class.java)
    }
}