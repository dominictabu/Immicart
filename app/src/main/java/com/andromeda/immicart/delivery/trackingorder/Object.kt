package com.andromeda.immicart.delivery.trackingorder



//data class OrderObject(val customerUID: String? = null, val orderID: String?, var mobileNumber:String?,
//                       val items: List<DeliveryCart>? = null, val datePosted: String?, var timeInMillisCreated:Long?,
//                       val payments: Payments? = null, val deliveryAddress: DeliveryAddress?, var store:DeliveryStore?, var orderStatus : OrderStatus?)
//
//
//data class Payments(val storeSubtotal : Float, val serviceFee : Float, val deliveryFee : Float)
//data class DeliveryAddress(val Name : String?, val Address : String, val PlaceFullText : String, val LatLng : String)
//data class OrderStatus(val assigned : Boolean?, val shopping : Boolean?, val delivering : Boolean?, val completed : Boolean?)
//data class DeliveryStore(val key : String?, val logoURL : String, val latLng : String, val name : String, val address : String)


data class OrderObject(val customerUID: String? = null, val orderID: String? = null, var mobileNumber:String? = null,
                       val items: List<DeliveryCart_>? = null, val datePosted: String? = null, var timeInMillisCreated:Long? = null,
                       val payments: Payments? = null, val deliveryAddress: DeliveryAddress? = null, var store:DeliveryStore? = null, var orderStatus : OrderStatus? = null)


data class Payments(val storeSubtotal : Float? = null, val serviceFee : Float? = null, val deliveryFee : Float? = null)
data class DeliveryAddress(val Name : String? = null, val Address : String? = null,  val PlaceFullText : String? = null, val LatLng : String? = null)
data class OrderStatus(val assigned : Boolean? = null, val shopping : Boolean? = null, val delivering : Boolean? = null, val completed : Boolean? = null)
data class DeliveryStore(val key : String? = null, val logoURL : String? = null, val latLng : String? = null, val name : String? = null, val address : String? = null)


data class DeliveryCart_(val key: String? = null, val barcode: String? = null, val name: String? = null, val category: String? = null,  val offerPrice: Int? = null, val normalPrice: Int? = null,
                        var quantity: Int? = null, val image_url : String? = null)