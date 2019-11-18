package com.andromeda.immicart.delivery.trackingorder.networking;

import com.andromeda.immicart.delivery.trackingorder.model.Result;
import io.reactivex.Single;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface IGoogleApi {

    @GET
    Call<Result> getDataFromGoogle(@Url String url);


    //Google Maps
    @GET("maps/api/directions/json")
    Single<Result> getDirections(@Query("mode") String mode,
                                 @Query("transit_routing_preference") String routingPreference,
                                 @Query("origin") String origin,
                                 @Query("destination") String destination,
                                 @Query("key") String apiKey);

}
