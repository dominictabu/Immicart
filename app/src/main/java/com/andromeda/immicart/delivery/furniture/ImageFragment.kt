package com.andromeda.immicart.delivery.furniture


import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView

import com.andromeda.immicart.R
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ImageFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class ImageFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var mResource: Int? = null
//    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            mResource = it.getInt(ARG_PARAM1)
//            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_image, container, false)
        val imageView = view.findViewById<ImageView>(R.id.imageView)
        val pinchZoom = view.findViewById<ImageButton>(R.id.zoom_in)
        val options = RequestOptions()
//        options.fitCenter()

        Glide.with(requireActivity())
            .load(mResource)
            .apply(options.fitCenter())
            .into(imageView)
        imageView?.setOnClickListener {
            startActivity(Intent(requireActivity(), MultiPhotoActivity::class.java))
        }
        return view
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ImageFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: Int) =
            ImageFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_PARAM1, param1)
//                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
