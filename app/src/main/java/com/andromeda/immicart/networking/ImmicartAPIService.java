package com.andromeda.immicart.networking;

import com.andromeda.immicart.delivery.search.algolia.AlgoliaCredentails;
import com.andromeda.immicart.delivery.trackingorder.model.Result;
import com.andromeda.immicart.delivery.wallet.stkPush.model.AccessToken;
import io.reactivex.Observable;
import io.reactivex.Single;
import retrofit2.Call;
import retrofit2.http.*;

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


    @POST("mpesaCallbackURL")
    Call<Model.MPESAResponse> getMPESAResponse();

    @POST("requestNodeSendyDelivery")
    Call<Model.SendyRequestResponse> makeSendyDeliveryRequest(@Query("from_name") String from_name, @Query("from_lat") Double from_lat, @Query("from_long") Double from_long, @Query("to_name") String to_name, @Query("to_lat") Double to_lat, @Query("to_long") Double to_long);

    @POST("mpesaAPI/token")
    Call<AccessToken> getToken();
    @POST("algoliaCredentails")
    Call<AlgoliaCredentails> getAlgoliaCredentails();
    //Google Maps
    @GET("maps/api/directions/json")
    Single<String> getDirections(@Query("mode") String mode,
                                 @Query("transit_routing_preference") String routingPreference,
                                 @Query("origin") String origin,
                                 @Query("destination") String destination,
                                 @Query("key") String apiKey);

    public static ImmicartAPIService create(String baseURL) {
        ImmicartAPIService service = RetrofitClientInstance.getRetrofitInstance(baseURL).create(ImmicartAPIService.class);

        return service;
    }
}