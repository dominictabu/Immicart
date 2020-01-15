package com.andromeda.immicart.delivery


import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment

import com.andromeda.immicart.R
import com.google.android.libraries.places.internal.it

//TODO the code which was to be implemented here has been moved
class RatingDialogFragment : DialogFragment() {
    private var content:String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        content = arguments!!.getString("content")


        val style = DialogFragment.STYLE_NO_FRAME
        val theme = R.style.BottomSheetDialogTheme

        setStyle(style, theme)

    }



//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_rate, container, false)
//    }

//    TODO move this code to where alert dialog should be implemented

    fun alert(){
        val alertDialog = AlertDialog.Builder(activity!!)
//        alertDialog.setMessage(it)

    }


}
