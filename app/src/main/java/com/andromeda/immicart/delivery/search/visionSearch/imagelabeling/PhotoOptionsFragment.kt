package com.andromeda.immicart.delivery.search.visionSearch.imagelabeling

import android.Manifest
import android.app.Activity
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.res.Configuration

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import kotlin.math.max


import com.andromeda.immicart.R
import kotlinx.android.synthetic.main.fragment_photo_options.*
import java.io.IOException

import com.algolia.search.saas.Client
import com.algolia.search.saas.AlgoliaException
import org.json.JSONObject

import com.algolia.search.saas.CompletionHandler
import com.algolia.search.saas.Query
import com.google.android.gms.tasks.Task
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.label.FirebaseVisionCloudImageLabelerOptions
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabel
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabeler


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [PhotoOptionsFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class PhotoOptionsFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var selectedMode = CLOUD_LABEL_DETECTION
    private var selectedSize: String = SIZE_PREVIEW

    private var isLandScape: Boolean = false

    private var imageUri: Uri? = null
    // Max width (portrait mode)
    private var imageMaxWidth = 0
    // Max height (portrait mode)
    private var imageMaxHeight = 0
    private lateinit var visionSearchViewModel: VisionSearchViewModel
//    private var imageProcessor: VisionImageProcessor? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_photo_options, container, false)
    }

    val cameraRequestCode : Int = 1001
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        visionSearchViewModel = ViewModelProviders.of(activity!!).get(VisionSearchViewModel::class.java)

        gallery_btn?.setOnClickListener {
            startChooseImageIntentForResult()

        }

        camera_btn?.setOnClickListener {
            if(hasPermissionInManifest(requireActivity(), Manifest.permission.CAMERA )) {
                startCameraIntentForResult()
            } else {
                ActivityCompat.requestPermissions(
                    activity!!,
                    arrayOf(Manifest.permission.CAMERA),
                    cameraRequestCode
                )
            }
        }

        closeBtn?.setOnClickListener {
            activity?.let {
                it.finish()
            }
        }

        if (previewPane == null) {
            Log.d(TAG, "Preview is null")
        }
//        if (previewOverlay == null) {
//            Log.d(TAG, "graphicOverlay is null")
//        }
        savedInstanceState?.let {
            imageUri = it.getParcelable(KEY_IMAGE_URI)
            imageMaxWidth = it.getInt(KEY_IMAGE_MAX_WIDTH)
            imageMaxHeight = it.getInt(KEY_IMAGE_MAX_HEIGHT)
            selectedSize = it.getString(KEY_SELECTED_SIZE, "")

            imageUri?.let { _ ->
                tryReloadAndDetectInImage()
            }
        }

        isLandScape = resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE


    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if(requestCode == cameraRequestCode) {

            // If request is cancelled, the result arrays are empty.
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCameraIntentForResult()

            } else {
                Toast.makeText(activity, "Camera Permission denied", Toast.LENGTH_SHORT).show()

            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            controlPanel?.visibility = View.GONE
            tryReloadAndDetectInImage()
        } else if (requestCode == REQUEST_CHOOSE_IMAGE && resultCode == Activity.RESULT_OK) {
            // In this case, imageUri is returned by the chooser, save it.
            imageUri = data!!.data
            controlPanel?.visibility = View.GONE
            tryReloadAndDetectInImage()
        }
    }

    public override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        with(outState) {
            putParcelable(KEY_IMAGE_URI, imageUri)
            putInt(KEY_IMAGE_MAX_WIDTH, imageMaxWidth)
            putInt(KEY_IMAGE_MAX_HEIGHT, imageMaxHeight)
            putString(KEY_SELECTED_SIZE, selectedSize)
        }
    }


    private fun startCameraIntentForResult() {
        // Clean up last time's image
        imageUri = null
        previewPane?.setImageBitmap(null)

        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        takePictureIntent.resolveActivity(activity!!.packageManager)?.let {
            val values = ContentValues()
            values.put(MediaStore.Images.Media.TITLE, "New Picture")
            values.put(MediaStore.Images.Media.DESCRIPTION, "From Camera")
            imageUri = activity!!.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        }
    }


    private fun startChooseImageIntentForResult() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_CHOOSE_IMAGE)
    }


    private fun tryReloadAndDetectInImage() {
//        imageProcessor = CloudImageLabelingProcessor()
        try {
            if (imageUri == null) {
                return
            }

            // Clear the overlay first
//            previewOverlay?.clear()

            val imageBitmap = if (Build.VERSION.SDK_INT < 29) {
                MediaStore.Images.Media.getBitmap(activity!!.contentResolver, imageUri)
            } else {
                val source = ImageDecoder.createSource(activity!!.contentResolver, imageUri!!)
                ImageDecoder.decodeBitmap(source)
            }

            // Get the dimensions of the View
            val targetedSize = getTargetedWidthHeight()

            val targetWidth = targetedSize.first
            val maxHeight = targetedSize.second

            // Determine how much to scale down the image
            val scaleFactor = max(
                imageBitmap.width.toFloat() / targetWidth.toFloat(),
                imageBitmap.height.toFloat() / maxHeight.toFloat())

            val resizedBitmap = Bitmap.createScaledBitmap(
                imageBitmap,
                (imageBitmap.width / scaleFactor).toInt(),
                (imageBitmap.height / scaleFactor).toInt(),
                true)

            previewPane?.setImageBitmap(resizedBitmap)
            loading_view?.visibility = View.VISIBLE
            resizedBitmap?.let {

                process(it)

            }
        } catch (e: IOException) {
            Log.e(TAG, "Error retrieving saved image")
        }
    }


    // Returns max image width, always for portrait mode. Caller needs to swap width / height for
    // landscape mode.
    private fun getImageMaxWidth(): Int {
        if (imageMaxWidth == 0) {
            // Calculate the max width in portrait mode. This is done lazily since we need to wait for
            // a UI layout pass to get the right values. So delay it to first time image rendering time.
            imageMaxWidth = if (isLandScape) {
                (previewPane.parent as View).height - controlPanel.height
            } else {
                (previewPane.parent as View).width
            }
        }

        return imageMaxWidth
    }

    // Returns max image height, always for portrait mode. Caller needs to swap width / height for
    // landscape mode.
    private fun getImageMaxHeight(): Int {
        if (imageMaxHeight == 0) {
            // Calculate the max width in portrait mode. This is done lazily since we need to wait for
            // a UI layout pass to get the right values. So delay it to first time image rendering time.
            imageMaxHeight = if (isLandScape) {
                (previewPane.parent as View).width
            } else {
                (previewPane.parent as View).height - controlPanel.height
            }
        }

        return imageMaxHeight
    }


    // Gets the targeted width / height.
    private fun getTargetedWidthHeight(): Pair<Int, Int> {
        val targetWidth: Int
        val targetHeight: Int

        when (selectedSize) {
            SIZE_PREVIEW -> {
                val maxWidthForPortraitMode = getImageMaxWidth()
                val maxHeightForPortraitMode = getImageMaxHeight()
                targetWidth = if (isLandScape) maxHeightForPortraitMode else maxWidthForPortraitMode
                targetHeight = if (isLandScape) maxWidthForPortraitMode else maxHeightForPortraitMode
            }
            SIZE_640_480 -> {
                targetWidth = if (isLandScape) 640 else 480
                targetHeight = if (isLandScape) 480 else 640
            }
            SIZE_1024_768 -> {
                targetWidth = if (isLandScape) 1024 else 768
                targetHeight = if (isLandScape) 768 else 1024
            }
            else -> throw IllegalStateException("Unknown size")
        }

        return Pair(targetWidth, targetHeight)
    }


    fun process(bitmap: Bitmap) {
        detectInVisionImage(
            null, /* bitmap */
            FirebaseVisionImage.fromBitmap(bitmap))
    }

    private val detector: FirebaseVisionImageLabeler by lazy {
        FirebaseVisionCloudImageLabelerOptions.Builder()
            .build().let { options ->
                FirebaseVision.getInstance().getCloudImageLabeler(options)
            }
    }
    fun detectInImage(image: FirebaseVisionImage): Task<List<FirebaseVisionImageLabel>> {
        return detector.processImage(image)
    }

    private fun detectInVisionImage(
        originalCameraImage: Bitmap?,
        image: FirebaseVisionImage
    ) {
        detectInImage(image)
            .addOnSuccessListener { results ->
                Log.d(TAG, "results: $results")

                val labelsStr = ArrayList<String>()

                results.forEach {
                    Log.d(TAG, "cloud label: ${it.text}")
                    it.confidence?.let {
                        Log.d(TAG, "Confidence Level:  $it")
                    }
                    it.text?.let { text ->
                        labelsStr.add(text)
                    }
                }

                visionSearchViewModel.setLabels(labelsStr)
                findNavController().navigate(R.id.action_photoOptionsFragment_to_visionSearchResultsFragment)

            }
            .addOnFailureListener { e -> e.stackTrace}
    }


    //Check for Manifest Permissions


    fun hasPermissionInManifest(context: Context, permissionName: String) : Boolean {
        val packageName : String = context.packageName
        try {
            val packageInfo : PackageInfo = context.packageManager.getPackageInfo(packageName, PackageManager.GET_PERMISSIONS)
            val declaredPermissions : Array<String> = packageInfo.requestedPermissions

            if(declaredPermissions != null && declaredPermissions.size > 0) {
                for (p in declaredPermissions) {
                    if(p.equals(permissionName)) {
                        return true
                    }
                }
            }

        } catch (e : PackageManager.NameNotFoundException) {

        }

        return false
    }

//    public boolean hasPermissionInManifest(Context context, String permissionName) {
//    final String packageName = context.getPackageName();
//    try {
//        final PackageInfo packageInfo = context.getPackageManager()
//                .getPackageInfo(packageName, PackageManager.GET_PERMISSIONS);
//        final String[] declaredPermisisons = packageInfo.requestedPermissions;
//        if (declaredPermisisons != null && declaredPermisisons.length > 0) {
//            for (String p : declaredPermisisons) {
//                if (p.equals(permissionName)) {
//                    return true;
//                }
//            }
//        }
//    } catch (NameNotFoundException e) {
//
//    }
//    return false;
//}



    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment PhotoOptionsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            PhotoOptionsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }

        private const val TAG = "PhotoOptionsFragment"
        private const val CLOUD_LABEL_DETECTION = "Cloud Label"
        private const val SIZE_PREVIEW = "w:max" // Available on-screen width.
        private const val SIZE_1024_768 = "w:1024" // ~1024*768 in a normal ratio
        private const val SIZE_640_480 = "w:640" // ~640*480 in a normal ratio

        private const val KEY_IMAGE_URI = "com.andromeda.immicart.delivery.search.KEY_IMAGE_URI"
        private const val KEY_IMAGE_MAX_WIDTH = "com.andromeda.immicart.delivery.search.KEY_IMAGE_MAX_WIDTH"
        private const val KEY_IMAGE_MAX_HEIGHT = "com.andromeda.immicart.delivery.search.KEY_IMAGE_MAX_HEIGHT"
        private const val KEY_SELECTED_SIZE = "com.andromeda.immicart.delivery.search.KEY_SELECTED_SIZE"

        private const val REQUEST_IMAGE_CAPTURE = 1001
        private const val REQUEST_CHOOSE_IMAGE = 1002
    }
}
