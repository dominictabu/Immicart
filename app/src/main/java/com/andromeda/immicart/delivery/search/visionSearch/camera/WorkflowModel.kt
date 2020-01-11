package com.andromeda.immicart.delivery.search.visionSearch.camera



import android.app.Application
import android.content.Context
import androidx.annotation.MainThread
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.andromeda.immicart.Scanning.persistence.CartRepository
import com.andromeda.immicart.Scanning.persistence.ImmicartRoomDatabase
import com.andromeda.immicart.delivery.choose_store.Store
import com.andromeda.immicart.delivery.choose_store.StoreRepository
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode
import kotlinx.coroutines.launch

import java.util.HashSet



/** View model for handling application workflow based on camera preview.  */
class WorkflowModel(application: Application) : AndroidViewModel(application) {

    val workflowState = MutableLiveData<WorkflowState>()
//    val objectToSearch = MutableLiveData<DetectedObject>()
//    val searchedObject = MutableLiveData<SearchedObject>()
    val detectedBarcode = MutableLiveData<FirebaseVisionBarcode>()
    private val repository_: StoreRepository
    private val repository: CartRepository

    val allStores: LiveData<List<Store>>


    init {
        val storeDao = ImmicartRoomDatabase.getDatabase(application, viewModelScope).storeDao()
        val cartDao = ImmicartRoomDatabase.getDatabase(application, viewModelScope).cartDao()
        repository = CartRepository(cartDao)
        repository_ = StoreRepository(storeDao)
        allStores = repository_.allStores
    }

    fun updateQuantity(id: String, newQuantity : Int) = viewModelScope.launch {
        repository.updateDeliveryItemQuantity(id, newQuantity)
    }






    private val objectIdsToSearch = HashSet<Int>()

    var isCameraLive = false
        private set

//    private var confirmedObject: DetectedObject? = null

    private val context: Context
        get() = getApplication<Application>().applicationContext

    /**
     * State set of the application workflow.
     */
    enum class WorkflowState {
        NOT_STARTED,
        DETECTING,
        DETECTED,
        CONFIRMING,
        CONFIRMED,
        SEARCHING,
        SEARCHED
    }

    @MainThread
    fun setWorkflowState(workflowState: WorkflowState) {
        if (workflowState != WorkflowState.CONFIRMED &&
            workflowState != WorkflowState.SEARCHING &&
            workflowState != WorkflowState.SEARCHED) {
//            confirmedObject = null
        }
        this.workflowState.value = workflowState
    }

//    @MainThread
//    fun confirmingObject(confirmingObject: DetectedObject, progress: Float) {
//        val isConfirmed = progress.compareTo(1f) == 0
//        if (isConfirmed) {
//            confirmedObject = confirmingObject
//            if (PreferenceUtils.isAutoSearchEnabled(context)) {
//                setWorkflowState(WorkflowState.SEARCHING)
//                triggerSearch(confirmingObject)
//            } else {
//                setWorkflowState(WorkflowState.CONFIRMED)
//            }
//        } else {
//            setWorkflowState(WorkflowState.CONFIRMING)
//        }
//    }

//    @MainThread
//    fun onSearchButtonClicked() {
//        confirmedObject?.let {
//            setWorkflowState(WorkflowState.SEARCHING)
//            triggerSearch(it)
//        }
//    }

//    private fun triggerSearch(detectedObject: DetectedObject) {
//        val objectId = detectedObject.objectId ?: throw NullPointerException()
//        if (objectIdsToSearch.contains(objectId)) {
//            // Already in searching.
//            return
//        }
//
//        objectIdsToSearch.add(objectId)
//        objectToSearch.value = detectedObject
//    }

    fun markCameraLive() {
        isCameraLive = true
        objectIdsToSearch.clear()
    }

    fun markCameraFrozen() {
        isCameraLive = false
    }
//
//    fun onSearchCompleted(detectedObject: DetectedObject, products: List<Product>) {
//        val lConfirmedObject = confirmedObject
//        if (detectedObject != lConfirmedObject) {
//            // Drops the search result from the object that has lost focus.
//            return
//        }
//
//        objectIdsToSearch.remove(detectedObject.objectId)
//        setWorkflowState(WorkflowState.SEARCHED)
//
//        searchedObject.value = SearchedObject(context.resources, lConfirmedObject, products)
//    }
}
