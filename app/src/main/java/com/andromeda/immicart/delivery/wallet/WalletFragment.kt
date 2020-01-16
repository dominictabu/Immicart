package com.andromeda.immicart.delivery.wallet


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.andromeda.immicart.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [WalletFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class WalletFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var TAG: String = "WalletFragment"
    private var currentFirebaseUser: FirebaseUser? = null

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
        return inflater.inflate(R.layout.fragment_wallet, container, false)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        currentFirebaseUser = FirebaseAuth.getInstance().currentUser

        top_up_button.setOnClickListener {
//            findNavController().navigate(R.id.action_walletFragment_to_MPESAFragment)
            activity?.let {
                startActivity(Intent(activity!!, MPESAActivity::class.java))
            }
        }
    }

    internal fun snapshotListeners() {
        val uid = currentFirebaseUser?.getUid()
        val db = FirebaseFirestore.getInstance()
        uid?.let {

            val docPath = "customers/$uid/wallet/data"
            val docRef = db.document(docPath)
            docRef.addSnapshotListener(EventListener<DocumentSnapshot> { snapshot, e ->
                if (e != null) {
                    Log.d(TAG, "Listen failed.", e)
                    return@EventListener
                }
                val source = if (snapshot != null && snapshot.metadata.hasPendingWrites())
                    "Local"
                else
                    "Server"
                if (snapshot != null && snapshot.exists()) {
                    Log.d(TAG, source + " data: " + snapshot.data)

                    val stringObjectMap = snapshot.data
                    if (stringObjectMap != null) {
                        if (stringObjectMap.containsKey("credit")) {
                            val credit = stringObjectMap["credit"] as Long
                            wallet_credit?.text = "KES ${credit.toInt()}"
                        }
                    }
                    //TODO Show Error or success dialog
                } else {
                    Log.d(TAG, "$source data: null")
                }
            })
        }
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment WalletFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            WalletFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
