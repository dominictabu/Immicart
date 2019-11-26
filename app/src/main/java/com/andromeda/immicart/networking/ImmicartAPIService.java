package com.andromeda.immicart.networking;

import com.andromeda.immicart.delivery.trackingorder.model.Result;
import io.reactivex.Observable;
import io.reactivex.Single;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

import java.util.List;

public interface ImmicartAPIService {

    @GET("products/{product_id}")
    Call<Model.SingleProduct> getSingleProductById(@Path(value = "product_id", encoded = true) int productId);

    @GET("product/{product_barcode}")
    Call<Model.ResponseData> getSingleProductByBarcode(@Path(value = "product_barcode", encoded = true) String productBarcode);
    @GET("categories")
    Observable<List<Model.Category_>> getCategories();

    @GET("categories/{category_id}")
    Observable<Model.SingleCategoryData> getCategoryProducts(@Path(value = "category_id", encoded = true) int categoryId);




    //Google Maps
    @GET("maps/api/directions/json")
    Single<String> getDirections(@Query("mode") String mode,
                                 @Query("transit_routing_preference") String routingPreference,
                                 @Query("origin") String origin,
                                 @Query("destination") String destination,
                                 @Query("key") String apiKey);

    public static ImmicartAPIService create() {
        ImmicartAPIService service = RetrofitClientInstance.getRetrofitInstance().create(ImmicartAPIService.class);

        return service;
    }
}