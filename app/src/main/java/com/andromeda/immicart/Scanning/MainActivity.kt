package com.andromeda.immicart.Scanning

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Matrix
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import android.util.Size
import android.view.Surface
import android.view.ViewGroup
import android.widget.Toast
import androidx.camera.core.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager.VERTICAL
import androidx.recyclerview.widget.RecyclerView
import com.andromeda.immicart.R
import com.andromeda.immicart.Scanning.persistence.Cart
import com.andromeda.immicart.networking.ImmicartAPIService
import com.andromeda.immicart.networking.Model
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Callback
import retrofit2.Call
import retrofit2.Response


private const val PERMISSIONS_REQUEST_CODE = 10

private val PERMISSIONS_REQUIRED = arrayOf(
    Manifest.permission.CAMERA)

private var lensFacing = CameraX.LensFacing.BACK
private lateinit var adapter: ProductsRecyclerAdapter
private lateinit var mainActivityViewModel: MainActivityViewModel

lateinit var cartItems: List<Cart>

private val TAG = "MainActivity"
    val immicartAPIService by lazy {
        ImmicartAPIService.create()
    }

class MainActivity : AppCompatActivity(), ActivityCompat.OnRequestPermissionsResultCallback {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mainActivityViewModel = ViewModelProviders.of(this).get(MainActivityViewModel::class.java)

        if (!hasPermissions()) {
            // Request camera-related permissions
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                ActivityCompat.requestPermissions(this,
                    PERMISSIONS_REQUIRED,
                    PERMISSIONS_REQUEST_CODE
                )
            }
        } else {
            setUpPreview()
            initializeRecyclerView()
            mainActivityViewModel.allScannedItems.observe(this, Observer { items ->
                // Update the cached copy of the words in the adapter.
                items?.let {
                    cartItems = items
                    badge.text = it.count().toString()
                    adapter.setCartItems(it)
                }
            })

        }

    }

    private fun hasPermissions(): Boolean {
        for (permission in PERMISSIONS_REQUIRED) {
            if (ContextCompat.checkSelfPermission(this@MainActivity, permission) !=
                PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }

    private fun setUpPreview() {
        val previewConfig = PreviewConfig.Builder().apply {
            setLensFacing(lensFacing)

        }.build()
        val preview = Preview(previewConfig)

        preview.setOnPreviewOutputUpdateListener {
                previewOutput: Preview.PreviewOutput? ->
            // Your code here. For example, use previewOutput?.getSurfaceTexture()
            // and post to a GL renderer.

            // To update the SurfaceTexture, we have to remove it and re-add it
            val parent = view_finder.parent as ViewGroup
            parent.removeView(view_finder)
            parent.addView(view_finder, 0)

            view_finder.surfaceTexture = previewOutput?.getSurfaceTexture()
            updateTransform()
        }


        val analyzerConfig = ImageAnalysisConfig.Builder().apply {
            // Use a worker thread for image analysis to prevent glitches
            val analyzerThread = HandlerThread(
                "BarcodeAnalysis").apply { start() }
            setCallbackHandler(Handler(analyzerThread.looper))
            setTargetResolution(Size(1280, 720))
            setLensFacing(lensFacing)
            // In our analysis, we care more about the latest image than
            // analyzing *every* image
            setImageReaderMode(
                ImageAnalysis.ImageReaderMode.ACQUIRE_LATEST_IMAGE)
        }.build()

//        val analyzerUseCase = ImageAnalysis(analyzerConfig)
//        analyzerUseCase.setAnalyzer { image, rotationDegrees ->
//            runBarcodeScanner(image.image!! , rotationDegrees)
//
//        }


        val imageAnalysisConfig = ImageAnalysisConfig.Builder().apply {
            val analyzerThread = HandlerThread(
                "BarcodeScannerAnalysis").apply { start() }
            setCallbackHandler(Handler(analyzerThread.looper))
            setTargetResolution(Size(1280, 720))
            setImageReaderMode(ImageAnalysis.ImageReaderMode.ACQUIRE_LATEST_IMAGE)

        }.build()
        val imageAnalysis = ImageAnalysis(imageAnalysisConfig)

        imageAnalysis.setAnalyzer { image: ImageProxy, rotationDegrees: Int ->
            // insert your code here.



        }

        val imageAnalyzer = ImageAnalysis(analyzerConfig).apply {
            analyzer = BarcodeImageAnalyzer().apply {

            } }

        CameraX.bindToLifecycle(this as LifecycleOwner,  preview, imageAnalysis)
    }


    private fun updateTransform() {
        val matrix = Matrix()

        // Compute the center of the view finder
        val centerX = view_finder.width / 2f
        val centerY = view_finder.height / 2f

        // Correct preview output to account for display rotation
        val rotationDegrees = when(view_finder.display?.rotation) {
            Surface.ROTATION_0 -> 0
            Surface.ROTATION_90 -> 90
            Surface.ROTATION_180 -> 180
            Surface.ROTATION_270 -> 270
            else -> return
        }
        matrix.postRotate(-rotationDegrees.toFloat(), centerX, centerY)

        // Finally, apply transformations to our TextureView
        view_finder.setTransform(matrix)
    }


    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Take the user to the success fragment when permission is granted
                Toast.makeText(this, "Permission request granted", Toast.LENGTH_LONG).show()
                setUpPreview()
                initializeRecyclerView()


            } else {
                Toast.makeText(this@MainActivity, "Permission request denied", Toast.LENGTH_LONG).show()
            }
        }
    }


    fun initializeRecyclerView() {
        val scannedProduct = ScannedProduct(R.drawable.breadimage, "Bread", 100f, 1 )

        val cartItem = Cart(_id = 1,barcode = "222222", name = "sdgede", price = 4444f, quantity = 12, image_url = "dveydeyde")
        var arrayList: ArrayList<Cart> = ArrayList()
        arrayList.add(cartItem)
        arrayList.add(cartItem)
        arrayList.add(cartItem)

        adapter = ProductsRecyclerAdapter({cartItem : Cart, newQuantity: Int -> cartItemClicked(cartItem, newQuantity)}, {cartItem : Cart -> removeItemClicked(cartItem)})


//        val scannedProductRecyclerAdapter: ScannedProductRecyclerAdapter = ScannedProductRecyclerAdapter(arrayList, this@MainActivity)
        scanned_product_recycler_view.layoutManager = LinearLayoutManager(this@MainActivity, RecyclerView.HORIZONTAL, false)
        scanned_product_recycler_view.adapter = adapter
//        adapter.submitList(arrayList)

        retrieveProduct("6297821976")

    }


    fun checkIfItemsaretheSame() {

    }


    private fun retrieveProduct(barcode: String) {
        val retrofitResponse = immicartAPIService.getSingleProductByBarcode(barcode)
        retrofitResponse.enqueue(object : Callback<Model.ResponseData> {
            override fun onFailure(call: Call<Model.ResponseData>, t: Throwable) {
                //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onResponse(call: Call<Model.ResponseData>, response: Response<Model.ResponseData>) {
                //TODO //To change body of created functions use File | Settings | File Templates.

                Log.d(TAG, "Retrofit onResponse: ${response.body()}")
                val _response: Boolean = response.isSuccessful();
                if (_response) {

                    val retroResponse: Model.ResponseData? = response.body()
                    val singleProduct: Model.SingleProduct? = retroResponse?.data
                    Log.d(TAG, retroResponse.toString())
                    val name =singleProduct!!.name
                    Log.d(TAG, "name $name")

                    val description = singleProduct?.description
                    Log.d(TAG, "description $description")


                    val productQuaatityInStores = singleProduct?.quantity
                    Log.d(TAG, "productQuaatityInStores $productQuaatityInStores")

                    val unitPrice = singleProduct!!.price
                    val barcode = singleProduct!!.barcode
                    val imageUrl = singleProduct!!.image_url
                    Log.d(TAG, "unitPrice $unitPrice")
                    val id = singleProduct._id


                    val cartItem = Cart(_id = id,barcode = barcode, name = name, price = unitPrice, quantity = 1, image_url = imageUrl)

                   cartItems.forEach {
                        if (id == cartItem._id) {
                            var _newQuantity = cartItem.quantity
                            _newQuantity++
                            mainActivityViewModel.updateQuantity(cartItem._id, _newQuantity)
                            return@forEach
                        }
                    }





                    mainActivityViewModel.insert(cartItem)



                    val responseCode = response.code()
                    Log.d(TAG, "Response Code: $responseCode")
//                    Log.d(TAG, results)
                }

            }

        })

    }

    private fun cartItemClicked(cartItem : Cart, newQuantity: Int) {
//        Toast.makeText(this, "Clicked: ${cartItem.name}", Toast.LENGTH_LONG).show()
        //TODO Change Quantity
        Log.d(TAG, "newQuantity : $newQuantity , $cartItem ")

        mainActivityViewModel.updateQuantity(cartItem.primaryKeyId, newQuantity)
    }


    private fun removeItemClicked(cartItem: Cart) {
        //Change Quantity via Room
//        Toast.makeText(this, "Removed: ${cartItem.name}", Toast.LENGTH_LONG).show()

        val id = cartItem.primaryKeyId
        mainActivityViewModel.deleteById(id)

    }


    private class BarcodeImageAnalyzer : ImageAnalysis.Analyzer {
        private fun degreesToFirebaseRotation(degrees: Int): Int = when(degrees) {
            0 -> FirebaseVisionImageMetadata.ROTATION_0
            90 -> FirebaseVisionImageMetadata.ROTATION_90
            180 -> FirebaseVisionImageMetadata.ROTATION_180
            270 -> FirebaseVisionImageMetadata.ROTATION_270
            else -> throw Exception("Rotation must be 0, 90, 180, or 270.")
        }

        override fun analyze(imageProxy: ImageProxy?, degrees: Int) {
            val mediaImage = imageProxy?.image
            val imageRotation = degreesToFirebaseRotation(degrees)
            if (mediaImage != null) {
                val image = FirebaseVisionImage.fromMediaImage(mediaImage, imageRotation)
                // Pass image to an ML Kit Vision API
                // ...

                val detector = FirebaseVision.getInstance()
                    .visionBarcodeDetector
                val result = detector.detectInImage(image)
                    .addOnSuccessListener { barcodes ->
                        // Task completed successfully
                        // ...

                    }
                    .addOnFailureListener {
                        // Task failed with an exception
                        // ...
                    }
            }
        }
    }



}
