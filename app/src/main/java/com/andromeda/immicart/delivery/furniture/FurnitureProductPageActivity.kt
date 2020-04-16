package com.andromeda.immicart.delivery.furniture

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import com.andromeda.immicart.R
import androidx.viewpager.widget.ViewPager
import com.google.firebase.dynamiclinks.DynamicLink
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.google.firebase.dynamiclinks.ShortDynamicLink
import com.google.firebase.firestore.FirebaseFirestore
import com.pixelcan.inkpageindicator.InkPageIndicator
import kotlinx.android.synthetic.main.activity_furniture_product_page.*
import androidx.core.app.ActivityCompat.startActivityForResult
import android.content.ComponentName
import android.graphics.Paint
import androidx.lifecycle.ViewModelProviders
import com.andromeda.immicart.delivery.DeliveryCart
import com.andromeda.immicart.delivery.ProductDetailViewModel
import com.andromeda.immicart.delivery.wallet.C2BService.model.C2B
import com.andromeda.immicart.delivery.wallet.C2BService.model.RegisterURL
import com.andromeda.immicart.delivery.wallet.C2BService.services.SimulateService
import com.andromeda.immicart.delivery.wallet.stkPush.ApiClient
import com.andromeda.immicart.delivery.wallet.stkPush.model.AccessToken
import com.andromeda.immicart.delivery.wallet.stkPush.model.STKPush
import com.andromeda.immicart.delivery.wallet.stkPush.util.AppConstants.*
import com.andromeda.immicart.delivery.wallet.stkPush.util.Utils
import com.andromeda.immicart.networking.ImmicartAPIService
import com.pesapal.pesapalandroid.data.Payment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber


class FurnitureProductPageActivity : AppCompatActivity() {

    var imagesList = ArrayList<FurnitureImage>()
    private lateinit var viewModel: ProductDetailViewModel
    var deliveryCart : DeliveryCart? = null

    val TAG = "FurnitureProductPgAct"

    public val PRODUCT_ID = "PRODUCT_ID"
    public val STORE_ID = "STORE_ID"
    public val QUERY_PARAM_PRODUCT_ID = "QUERY_PARAM_PRODUCT_ID"
    private val PRODUCT_ID_LINK = "PRODUCT_ID_LINK"
    var shareLink : String? = null
    var productID : String? = null
    val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_furniture_product_page)
        viewModel = ViewModelProviders.of(this).get(ProductDetailViewModel::class.java)


        if (intent.hasExtra(PRODUCT_ID)) {

//            productIDLink = intent.getStringExtra(PRODUCT_ID_LINK)
            val id = intent.getStringExtra(PRODUCT_ID)
//            val storeId = intent.getStringExtra(STORE_ID)
            productID = id.toString()
//            getProductFromFirestore(storeId)
            val shareLink_ = createShareUri(productId = productID!!)
            shareContent(shareLink_.toString())
        }

        updateQty()

//        val images = FurnitureImage(1,R.drawable.sofa, true)
//        val images1 = FurnitureImage(2,R.drawable.sofa_1, false)
//        val images2 = FurnitureImage(3,R.drawable.sofa_2, false)
//        val images3 = FurnitureImage(4,R.drawable.sofa_3, false)
//        val images4 = FurnitureImage(6,R.drawable.sofa_4, false)
//        val images5 = FurnitureImage(7,R.drawable.sofa_5, false)
//        val images6 = FurnitureImage(8,R.drawable.sofa_6, false)
//        val images7 = FurnitureImage(9,R.drawable.sofa_7, false)
//
//        imagesList.add(images)
//        imagesList.add(images1)
//        imagesList.add(images2)
//        imagesList.add(images3)
//        imagesList.add(images4)
//        imagesList.add(images5)
//        imagesList.add(images6)
//        imagesList.add(images7)

        val mAdapter = TestFragmentAdapter(supportFragmentManager, imagesList)

        pager?.setAdapter(mAdapter)

        val mIndicator = findViewById<InkPageIndicator>(R.id.indicator)
        mIndicator.setViewPager(pager)

        pager?.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                Log.d(TAG, "Position: $position")
                val banner = imagesList.get(position)
                Log.d(TAG, " $banner")

            }

            override fun onPageSelected(position: Int) {
                // Check if this is the page you want.
            }
        })

        share_ll?.setOnClickListener {
            productID?.let{
                //                val shareLink = createShareUri(productId = productID!!)
//                shareContent(shareLink.toString())
                shareLink?.let {
                    shareDeepLink(it)
                }
            }
        }

        specifications?.setOnClickListener {
            startActivity(Intent(this, SpecificationsActivity::class.java))
        }

        glance?.setOnClickListener {
            startActivity(Intent(this, AtAGlanceActivity::class.java))
        }

        info?.setOnClickListener {
            startActivity(Intent(this, InformationActivity::class.java))
        }

        old_price?.setPaintFlags(old_price.getPaintFlags() or Paint.STRIKE_THRU_TEXT_FLAG)


        getAccess_Token()


        add_to_cart_btn?.setOnClickListener {
//            val cn = ComponentName(this, "com.pesapal.pesapalandroid.PesapalSettingsActivity")
//
//            val intent = Intent().setComponent(cn)
//
//            intent.putExtra("pkg", "com.andromeda.immicart")
//
////            startActivityForResult(intent, SETTINGS_ACTIVITY_REQUEST_CODE)
//            deliveryCart?.let {
//                viewModel.insert(it)
//
//            }

//            startActivity(Intent(this, FurnitureCheckoutActivity::class.java))


            performC2BRegisterURL()
//            performSTKPush()
//            makePayments()
        }
    }

    private val _baseURL = "https://us-central1-immicart-2ca69.cloudfunctions.net/"

   val  mApiClient = ApiClient()

    fun getAccess_Token() {
        ImmicartAPIService.create(_baseURL).token.enqueue(object : Callback<AccessToken> {
            override fun onResponse(call: Call<AccessToken>, response: Response<AccessToken>) {

                Log.d(TAG, "AccessToken : " + response.body()!!)

                val accessToken = response.body()?.accessToken
                Log.d(TAG, "AccessToken : " + accessToken)
                accessToken?.let {
                    mApiClient.setAuthToken(it)



                }

            }

            override fun onFailure(call: Call<AccessToken>, t: Throwable) {

            }
        })
    }

    //The MSISDN sending the funds
    fun formatPhoneNumber(phoneNumber: String): String {
        if (phoneNumber == "") {
            return ""
        }
        if ((phoneNumber.length < 11) and phoneNumber.startsWith("0")) {
            //here we can just remove the inline variable instead of the p. Like you did with the rest
            //String p = phoneNumber.replaceFirst("^0", "254");
            //return p
            return phoneNumber.replaceFirst("^0".toRegex(), "254")
        }
        return if (phoneNumber.length == 13 && phoneNumber.startsWith("+")) {
            phoneNumber.replaceFirst("^+".toRegex(), "")
        } else phoneNumber
    }

    fun performSTKPush() {


        val simulateService  = C2B(BUSINESS_SHORT_CODE,TRANSACTION_TYPE, "10", formatPhoneNumber("0796026997"), "0796026997");

        mApiClient.setGetAccessToken(false)

        mApiClient.mpesaSimulateService().simulate(simulateService).enqueue(object : Callback<C2B> {
            override fun onResponse(call: Call<C2B>, response: Response<C2B>) {
                try {
                    if (response.isSuccessful) {
                        Log.d(TAG, "C2B PROMPT Successful" + response.body()!!)
                        response.body().toString()
                        Timber.d("post submitted to API. %s", response.body())
                    } else {
                        Log.d(TAG, "C2B PROMPT UnSuccessful" + response.errorBody()!!.string())
                        Timber.e("Response %s", response.errorBody()!!.string())
                    }
                } catch (e: Exception) {
                    e.printStackTrace()

                }

            }

            override fun onFailure(call: Call<C2B>, t: Throwable) {
                //              TODO  mProgressDialog.dismiss();
                Timber.e(t)
            }
        })
    }

    fun performC2BRegisterURL() {


        val simulateService  = C2B(BUSINESS_SHORT_CODE,TRANSACTION_TYPE, "10", formatPhoneNumber("0796026997"), "0796026997");
        val registerURL  = RegisterURL(BUSINESS_SHORT_CODE, "Completed", CONFIRMATIONURL, VALIDATIONURL);



        mApiClient.setGetAccessToken(false)

        mApiClient.mpesaSimulateService().registerURL(registerURL).enqueue(object : Callback<RegisterURL> {
            override fun onResponse(call: Call<RegisterURL>, response: Response<RegisterURL>) {
                try {
                    if (response.isSuccessful) {
                        Log.d(TAG, "C2B RegisterURL PROMPT Successful" + response.body()!!)
                        response.body().toString()
                        Timber.d("post submitted to API. %s", response.body())
                    } else {
                        Log.d(TAG, "C2B  RegisterURL PROMPT UnSuccessful" + response.errorBody()!!.string())
                        Timber.e("Response %s", response.errorBody()!!.string())
                    }
                } catch (e: Exception) {
                    e.printStackTrace()

                }

            }

            override fun onFailure(call: Call<RegisterURL>, t: Throwable) {
                //              TODO  mProgressDialog.dismiss();
                Timber.e(t)
            }
        })
    }


    fun makePayments() {
        val payment = Payment()
        payment.reference = "p0909788786t9"//transaction unique reference
        payment.amount = 100.00
        payment.account = "just extra info, e.g dstv account number, order id"
        payment.description = "test_payment"
        payment.email = "dommietabu@gmail.com.com"
        payment.currency = "KES"
        payment.firstName = "Jane"
        payment.lastName = "Doe"
        payment.phoneNumber = "0796026997"
        val cn = ComponentName(this, "com.pesapal.pesapalandroid.PesapalPayActivity")

        val intent = Intent().setComponent(cn)
        intent.putExtra("payment", payment)
        startActivityForResult(intent, PAYMENT_ACTIVITY_REQUEST_CODE)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
//        val bundle: Bundle? = data?.getExtras();
//        if (bundle != null) {
//            for (key in bundle!!.keySet()) {
//                if (bundle.get(key) != null) {
//                    Log.e(TAG, key + " : " + bundle!!.get(key));
//
//                } else {
//                    Log.e(TAG, key + " : " + "NULL");
//
//                }
//            }
//        }
        if(requestCode == PAYMENT_ACTIVITY_REQUEST_CODE && data != null) {
            val payment : String = data.getStringExtra("payment")
            Log.d(TAG, "PAyment: $payment")

        }

    }

    val SETTINGS_ACTIVITY_REQUEST_CODE = 100
    val PAYMENT_ACTIVITY_REQUEST_CODE = 101

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
            .setAndroidParameters(DynamicLink.AndroidParameters.Builder().build())
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
}
