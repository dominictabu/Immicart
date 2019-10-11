package com.andromeda.immicart.networking;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ImmicartAPIService {

    @GET("products/{product_id}")
    Call<Model.SingleProduct> getSingleProductById(@Path(value = "product_id", encoded = true) int productId);

    @GET("product/{product_barcode}")
    Call<Model.ResponseData> getSingleProductByBarcode(@Path(value = "product_barcode", encoded = true) String productBarcode);

    public static ImmicartAPIService create() {
        ImmicartAPIService service = RetrofitClientInstance.getRetrofitInstance().create(ImmicartAPIService.class);

        return service;
    }
}