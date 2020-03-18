package com.andromeda.immicart.delivery.search.visionSearch.imagelabeling


import android.graphics.Bitmap
import android.util.Log
import com.algolia.search.saas.AlgoliaException
import com.algolia.search.saas.Client
import com.algolia.search.saas.CompletionHandler
import com.algolia.search.saas.Query
import com.google.android.gms.tasks.Task
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.label.FirebaseVisionCloudImageLabelerOptions
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabel
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabeler
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import org.json.JSONObject

/** Cloud Label Detector Demo.  */
class CloudImageLabelingProcessor : VisionProcessorBase<List<FirebaseVisionImageLabel>>() {
    override fun onSuccess(originalCameraImage: Bitmap?, results: List<FirebaseVisionImageLabel>) {

        Log.d(TAG, "results: $results")

        val labelsStr = ArrayList<String>()

        results.forEach {
            Log.d(TAG, "cloud label: $it")
            it.confidence?.let {
                Log.d(TAG, "Confidence Level:  $it")
            }
            it.text?.let { text ->
                labelsStr.add(text)
            }
        }

        if(labelsStr.size > 0) {
            algoliaSearch(labelsStr)
        }

        Log.d(TAG, "cloud labels: $labelsStr")


    }

    fun algoliaSearch(labels: ArrayList<String>) {
        val client = Client("latency", "3d9875e51fbd20c7754e65422f7ce5e1")
        val index = client.getIndex("bestbuy")

        val completionHandler = object : CompletionHandler {
            override fun requestCompleted(p0: JSONObject?, p1: AlgoliaException?) {
                Log.d(TAG, "requestCompleted JSONObject : $p0")
            }


        }
// Search for a first result
        index.searchAsync(Query(labels[0]), completionHandler)

        labels.forEach {

        }

    }

    private val detector: FirebaseVisionImageLabeler by lazy {
        FirebaseVisionCloudImageLabelerOptions.Builder()
            .build().let { options ->
                FirebaseVision.getInstance().getCloudImageLabeler(options)
            }
    }

    override fun detectInImage(image: FirebaseVisionImage): Task<List<FirebaseVisionImageLabel>> {
        return detector.processImage(image)
    }

    override fun onSuccess(
        originalCameraImage: Bitmap?,
        results: List<FirebaseVisionImageLabel>,
        frameMetadata: FrameMetadata?,
        graphicOverlay: GraphicOverlay
    ) {
        graphicOverlay.clear()
        Log.d(TAG, "cloud label size: ${results.size}")
        val labelsStr = ArrayList<String>()

        results.forEach {
            Log.d(TAG, "cloud label: $it")
            it.text?.let { text ->
                labelsStr.add(text)
            }
        }
    }

    override fun onFailure(e: Exception) {
        Log.e(TAG, "Cloud Label detection failed $e")
    }


    companion object {
        private const val TAG = "CloudImgLabelProc"
    }
}
