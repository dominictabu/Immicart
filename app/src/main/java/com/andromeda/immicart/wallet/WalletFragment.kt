package com.andromeda.immicart.wallet


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation

import com.andromeda.immicart.R
import kotlinx.android.synthetic.main.fragment_wallet.*

class WalletFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_wallet, container, false)


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        walletAmount?.text=3000.toString()  // Value to be triggered from firebase.
        addCredit()
    }



 fun addCredit(){

//     button_add_credit.setOnClickListener { view: View ->
//         Navigation.findNavController(view).navigate(R.id.action_walletFragment_to_paymentDetailsFragment)
//     }

 }

}
