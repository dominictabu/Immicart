package com.andromeda.immicart.delivery


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import com.andromeda.immicart.R
import org.jetbrains.anko.support.v4.find


class DeliveryDetailsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_delivery_details, container, false)
    }
    fun addMobileNumber(view: View){
        val addPhoneNumber = find<LinearLayout>(R.layout.add_phone_number)
        
    }

}
