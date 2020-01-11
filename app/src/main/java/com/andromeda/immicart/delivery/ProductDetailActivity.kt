package com.andromeda.immicart.delivery

import android.graphics.Paint
import android.net.Uri
import android.os.Bundle
import android.util.Log
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity;
import com.andromeda.immicart.R
import com.andromeda.immicart.networking.ImmicartAPIService
import com.andromeda.immicart.networking.Model
import com.bumptech.glide.Glide

import kotlinx.android.synthetic.main.activity_product_detail.*
import kotlinx.android.synthetic.main.content_product_detail.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

import android.view.animation.AnimationUtils
import android.widget.ImageButton
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import android.content.Intent
import android.view.MenuItem
import com.google.firebase.dynamiclinks.ShortDynamicLink
import com.google.firebase.firestore.FirebaseFirestore


class ProductDetailActivity : AppCompatActivity() {


    private lateinit var viewModel: ProductDetailViewModel

    var existsInCart = false
    var deliveryCart : DeliveryCart? = null
    var productIDLink : String? = null
    var productID : String? = null
    val db = FirebaseFirestore.getInstance()

    var shareLink : String? = null


    companion object {

        public val PRODUCT_ID = "PRODUCT_ID"
        public val STORE_ID = "STORE_ID"
        public val QUERY_PARAM_PRODUCT_ID = "QUERY_PARAM_PRODUCT_ID"
        private val PRODUCT_ID_LINK = "PRODUCT_ID_LINK"
        private val TAG = "ProductDetailActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_detail)
        setSupportActionBar(toolbar)

        viewModel = ViewModelProviders.of(this).get(ProductDetailViewModel::class.java)


        supportActionBar!!.setDisplayShowTitleEnabled(false);

        supportActionBar!!.setHomeButtonEnabled(true);
        supportActionBar!!.setDisplayHomeAsUpEnabled(true);

                if (intent.hasExtra(PRODUCT_ID) && intent.hasExtra(STORE_ID)) {

//            productIDLink = intent.getStringExtra(PRODUCT_ID_LINK)
                    val id = intent.getStringExtra(PRODUCT_ID)
                    val storeId = intent.getStringExtra(STORE_ID)
                    productID = id.toString()
                    getProductFromFirestore(storeId)
                    val shareLink_ = createShareUri(productId = productID!!)
                    shareContent(shareLink_.toString())
                }


//            getProduct(id)
                    viewModel.allDeliveryItems().observe(this, Observer { items ->

                        Log.d(TAG, "CartItems: $items")
                        items?.let {
                            it.forEach {
                                if (productID == it.key) {
                                    existsInCart = true
                                }
                            }

                        }

                        if (existsInCart) {
                            addToCartButton.text = "Update Quantity"
                        } else {
                            addToCartButton.text = "Add to cart"

                        }
                    })




                    updateQty()


                    addToCartButton.setOnClickListener {
                        var currentQuantityString = quantityTv.text.toString()
                        var currentQuantityInt = currentQuantityString.toInt()

                        if(existsInCart) {
                            viewModel.updateQuantity(productID!!, currentQuantityInt)
                        } else {
                            deliveryCart?.let {
                                viewModel.insert(it)

                            }
                        }


                    }






        share_ll.setOnClickListener {
            productID?.let{
//                val shareLink = createShareUri(productId = productID!!)
//                shareContent(shareLink.toString())
                shareLink?.let {
                    shareDeepLink(it)
                }
            }
        }




//        val animation = AnimationUtils.loadAnimation(this, R.anim.rotate_circle)
//        save_ll.startAnimation(animation)


    }


    //FROM REST Endpoint using Retrofit
    fun getProduct(id: Int) {
        val retrofitResponse = immicartAPIService.getSingleProductById(id)

        retrofitResponse.enqueue(object : Callback<Model.SingleProduct> {
            override fun onFailure(call: Call<Model.SingleProduct>, t: Throwable) {
                //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onResponse(call: Call<Model.SingleProduct>, response: Response<Model.SingleProduct>) {
                //TODO //To change body of created functions use File | Settings | File Templates.

                Log.d(TAG, "Retrofit onResponse: ${response.body()}")
                val _response: Boolean = response.isSuccessful();
                if (_response) {
                    val singleProduct: Model.SingleProduct? = response.body()
                    Log.d(TAG, singleProduct.toString())
                    val name =singleProduct!!.name
                    Log.d(TAG, "name $name")

                    product_description.text = name

                    val description = singleProduct?.description
                    Log.d(TAG, "description $description")


                    val productQuaatityInStores = singleProduct?.quantity
                    Log.d(TAG, "productQuaatityInStores $productQuaatityInStores")

                    val unitPrice = singleProduct!!.price
                    val price = "KES $unitPrice"
                    product_price.text = price
                    deals_off.paintFlags = deals_off.getPaintFlags() or Paint.STRIKE_THRU_TEXT_FLAG

                    val barcode = singleProduct!!.barcode
                    val imageUrl = singleProduct!!.image_url
                    Glide.with(this@ProductDetailActivity).load(imageUrl).into(product_image)
                    Log.d(TAG, "unitPrice $unitPrice")
                    val id = singleProduct._id

//                    deliveryCart = DeliveryCart(singleProduct._id, singleProduct.barcode, singleProduct.name, singleProduct.price.toInt(), 1, singleProduct.image_url )

                }
            }
        })


    }

    //FROM Firebase Firestore
    fun getProductFromFirestore(storeId: String) {


        productID?.let {
            val documentPath = "stores/" + storeId + "/offers/" + productID
            db.document(documentPath).get().addOnSuccessListener { documentSnapshot ->
                val offer = documentSnapshot.data as HashMap<String, Any>
                val key = documentSnapshot.id
                val productName = offer["name"] as String
                val deadline = offer["deadline"] as String
                val normalPrice : String = offer["normal_price"] as String
                val offerPrice = offer["offer_price"] as String
                val category = offer["categoryOne"] as String
                var barcode = offer["barcode"] as String?
                val fileURL = offer["imageUrl"] as String

                if(barcode == null) {
                    barcode = "Not Set"
                }

                val intOfferPrice = offerPrice.toInt()
                val intNormalPrice = normalPrice.toInt()

                val savings = intNormalPrice - intOfferPrice
                savings_tv.text = savings.toString()

                product_description.text = productName

                val unitPrice = intOfferPrice
                val price = "KES $unitPrice"
                product_price.text = price
                val _normalPrice = "KES $intNormalPrice"
                deals_off.text = _normalPrice
                deals_off.paintFlags = deals_off.getPaintFlags() or Paint.STRIKE_THRU_TEXT_FLAG
                Glide.with(this@ProductDetailActivity).load(fileURL).into(product_image)
                //                    deliveryCart = DeliveryCart(singleProduct._id, singleProduct.barcode, singleProduct.name, singleProduct.price.toInt(), 1, singleProduct.image_url )

                deliveryCart = DeliveryCart(key, barcode, productName, category, intOfferPrice, intNormalPrice, 1, fileURL)

            }

        }
    }

    fun updateQty() {
        var currentQuantityString = quantityTv.text.toString()
        var currentQuantityInt = currentQuantityString.toInt()

        add_quantity.setOnClickListener {

            currentQuantityInt++
            quantityTv.text = currentQuantityInt.toString()
            (reduceQuantityBtn as ImageButton).setImageResource(R.drawable.ic_remove_primary_color_24dp)

        }


        reduceQuantityBtn.setOnClickListener {
            if (currentQuantityInt > 1) {
                currentQuantityInt--
                quantityTv.text = currentQuantityInt.toString()

            } else {
                (reduceQuantityBtn as ImageButton).setImageResource(R.drawable.ic_delete_primary_color_24dp)
//                currentQuantityInt--

            }

        }
    }

    private fun createShareUri(productId: String): Uri {
        val builder = Uri.Builder()
        builder.scheme("http") // "http"
            .authority("157.245.68.46") // "365salads.xyz"
            .appendPath("api/v1/products") // "salads"
            .appendQueryParameter(QUERY_PARAM_PRODUCT_ID, productId)
        val uri = builder.build()
        Log.d(TAG, "createShareUri method called. Uri returned: $uri")

        return uri
    }



    fun shareContent(link : String) {

        Log.d(TAG, "shareContent method called")

       FirebaseDynamicLinks.getInstance().createDynamicLink()
            .setLink(Uri.parse(link))
            .setDomainUriPrefix("https://immicart.page.link")
            // Set parameters
            // ...
            .buildShortDynamicLink(ShortDynamicLink.Suffix.SHORT)
            .addOnSuccessListener { result ->
                // Short link created
                val shortLink = result.shortLink
                Log.d(TAG, "shortLink : $shortLink")
                shareLink = shortLink.toString()
//                shareDeepLink(shortLink.toString())
                val flowchartLink = result.previewLink
            }.addOnFailureListener {
                // Error
                // ...
            }

    }


    private fun shareDeepLink(deepLink: String) {
        Log.d(TAG, "shareDeepLink method called")
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_SUBJECT, "Immicart")
        intent.putExtra(Intent.EXTRA_TEXT, deepLink)
        startActivity(Intent.createChooser(intent, "Share this product with: "))
    }


    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        item?.let {
            if(it.itemId == android.R.id.home) {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

}
