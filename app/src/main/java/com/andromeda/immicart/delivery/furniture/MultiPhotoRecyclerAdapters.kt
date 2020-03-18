package com.andromeda.immicart.delivery.furniture

import android.content.Context
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import androidx.recyclerview.widget.RecyclerView
import com.andromeda.immicart.R
import com.bumptech.glide.Glide
import com.ortiz.touchview.TouchImageView
import kotlinx.android.synthetic.main.item_furniture_viewpager.view.*
import kotlinx.android.synthetic.main.item_horizontal_furniture.view.*
import kotlinx.android.synthetic.main.item_selected_horizontal_furniture.view.*

class FurnitureImageRecyclerAdapter(val images : ArrayList<FurnitureImage>, val clicklistener : (FurnitureImage, Int) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        when (viewType) {
            R.layout.item_horizontal_furniture -> return ImageListRecyclerViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.item_horizontal_furniture,
                    parent,
                    false
                )
            )
            R.layout.item_selected_horizontal_furniture -> return SelectedImageListRecyclerViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.item_selected_horizontal_furniture,
                    parent,
                    false
                )
            )
            else -> { // Note the block
                return ImageListRecyclerViewHolder(
                    LayoutInflater.from(parent.context).inflate(
                        R.layout.item_horizontal_furniture,
                        parent,
                        false
                    )
                )
            }
        }
    }

    override fun getItemCount(): Int {
        return images.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        if(images.size > 0) {
            val order = images[position]
            if(getItemViewType(position) == R.layout.item_horizontal_furniture) {
                (holder as ImageListRecyclerViewHolder).bindItem(order)

            } else if (getItemViewType(position) == R.layout.item_selected_horizontal_furniture) {
                (holder as SelectedImageListRecyclerViewHolder).bindItem(order)
            }

        }
    }
    override fun getItemViewType(position: Int): Int {
        val isSelected = images.get(position).isSelected
        //categoriesPr.get(position).isInCart
                    if (isSelected ) {
                        return R.layout.item_selected_horizontal_furniture
                    } else  {
                        return R.layout.item_horizontal_furniture
                    }
    }


    inner class ImageListRecyclerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItem(images: FurnitureImage) {
            Glide.with(itemView.context).load(images.link).into(itemView.furniture_image_horizontal)

            itemView.setOnClickListener {

                clicklistener(images, adapterPosition)

            }

        }

    }
    inner class SelectedImageListRecyclerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItem(images: FurnitureImage) {
            Glide.with(itemView.context).load(images.link).into(itemView.furniture_image_selected)


//            val position = adapterPosition

            itemView.setOnClickListener {

                clicklistener(images, adapterPosition)

            }

        }

    }
}


class ViewPagerImagesAdapter(val items : ArrayList<FurnitureImage>, val context: Context) : RecyclerView.Adapter<PinchViewHolder>() {

    // Gets the number of animals in the list
    override fun getItemCount(): Int {
        return items.size
    }

    // Inflates the item views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PinchViewHolder {

        return PinchViewHolder(TouchImageView(parent.context).apply {
             layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT)

            setOnTouchListener { view, event ->
                var result = true
                //can scroll horizontally checks if there's still a part of the image
                //that can be scrolled until you reach the edge
                if (event.pointerCount >= 2 || view.canScrollHorizontally(1) && canScrollHorizontally(-1)) {
                    //multi-touch event
                    result = when (event.action) {
                        MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                            // Disallow RecyclerView to intercept touch events.
                            parent.requestDisallowInterceptTouchEvent(true)
                            // Disable touch on view
                            false
                        }
                        MotionEvent.ACTION_UP -> {
                            // Allow RecyclerView to intercept touch events.
                            parent.requestDisallowInterceptTouchEvent(false)
                            true
                        }
                        else -> true
                    }
                }
                result
            }
        })

//        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_furniture_viewpager, parent, false))
    }

    // Binds each animal in the ArrayList to a view
    override fun onBindViewHolder(holder: PinchViewHolder, position: Int) {
//        Glide.with(context).load(items.get(position).link).into(holder.imagePlace)
        holder.imagePlace.setImageResource(items.get(position).link)
    }

    override fun getItemId(i: Int): Long {
        return i.toLong()
    }

    override fun getItemViewType(i: Int): Int {
        return 0
    }
}

class PinchViewHolder(view: TouchImageView) : RecyclerView.ViewHolder(view) {
    val imagePlace = view
}

//class ViewHolder (view: View) : RecyclerView.ViewHolder(view) {
//    // Holds the TextView that will add each animal to
//    val product_image = view.furniture_image_viewpager
//}