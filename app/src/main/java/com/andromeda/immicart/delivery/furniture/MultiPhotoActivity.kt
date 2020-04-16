package com.andromeda.immicart.delivery.furniture

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.andromeda.immicart.R
import kotlinx.android.synthetic.main.activity_multi_photo.*
import android.util.Log
import android.view.Gravity
import android.widget.Toast




class MultiPhotoActivity : AppCompatActivity() {

    var imagesList = ArrayList<FurnitureImage>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_multi_photo)

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
//        imagesList.add(images)
        horizontal_recycler_view?.let {
            it.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
            it.adapter = adapter
        }

        viewpager_recycler_view?.let {
            val manager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
            it.layoutManager = manager
            it.adapter = viewPagerAdapter
            val snapHelper = PagerSnapHelper()
            snapHelper.attachToRecyclerView(it)

            it.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        val centerView = snapHelper.findSnapView(manager)
                        centerView?.let {
                            val pos = manager.getPosition(centerView)
                            Log.e("Snapped Item Position:", "" + pos)
                            imagesList.forEach {
                                it.isSelected = false
                            }
                            imagesList.get(pos).isSelected = true
                            adapter.notifyDataSetChanged()
                            horizontal_recycler_view.smoothScrollToPosition(pos)


                        }
//                        val pos = manager.getPosition(centerView)
//                        Log.e("Snapped Item Position:", "" + pos)
                    }
                }
            })

            val toast = Toast.makeText(this@MultiPhotoActivity, "Pinch To Zoom", Toast.LENGTH_LONG)
            toast.setGravity(Gravity.CENTER, 0, 0)
            toast.show()

        }
    }

    val viewPagerAdapter = ViewPagerImagesAdapter(imagesList, this)
    val adapter = FurnitureImageRecyclerAdapter(imagesList, { image: FurnitureImage, position : Int -> handleClicks(image,position) })

    fun handleClicks(image: FurnitureImage, position: Int) {
        imagesList.forEach {
            it.isSelected = false
        }
        imagesList.get(position).isSelected = true
        adapter.notifyDataSetChanged()
        viewpager_recycler_view.scrollToPosition(position)

    }
}
