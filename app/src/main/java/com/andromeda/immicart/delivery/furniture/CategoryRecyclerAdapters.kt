package com.andromeda.immicart.delivery.furniture

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.andromeda.immicart.R
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_furniture.view.*
import kotlinx.android.synthetic.main.item_furniture_category.view.*
import kotlinx.android.synthetic.main.item_home_department.view.*
import java.text.DecimalFormat

class PaymentMethodRecyclerAdapter(val items : List<PaymentMethod>, val context: Context, val clickListener: (PaymentMethod) -> Unit) : RecyclerView.Adapter<PaymentMethodRecyclerAdapterViewHolder>() {

    // Gets the number of animals in the list
    override fun getItemCount(): Int {
        return items.size
    }

    // Inflates the item views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaymentMethodRecyclerAdapterViewHolder {
        return PaymentMethodRecyclerAdapterViewHolder(LayoutInflater.from(context).inflate(R.layout.item_home_department, parent, false))
    }

    // Binds each animal in the ArrayList to a view
    override fun onBindViewHolder(holder: PaymentMethodRecyclerAdapterViewHolder, position: Int) {
        Glide.with(context).load(items.get(position).link).into(holder.product_image)
        holder.category.text = items.get(position).name
        holder.itemView.setOnClickListener {
            clickListener(items.get(position))
        }
    }
}

class PaymentMethodRecyclerAdapterViewHolder (view: View) : RecyclerView.ViewHolder(view) {
    // Holds the TextView that will add each animal to
    val product_image = view.product_image
    val category = view.category
}
class CategoryRecyclerAdapter(val items : List<Department>, val context: Context, val clickListener: (Department) -> Unit) : RecyclerView.Adapter<CategoryRecyclerAdapterViewHolder>() {

    // Gets the number of animals in the list
    override fun getItemCount(): Int {
        return items.size
    }

    // Inflates the item views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryRecyclerAdapterViewHolder {
        return CategoryRecyclerAdapterViewHolder(LayoutInflater.from(context).inflate(R.layout.item_home_department, parent, false))
    }

    // Binds each animal in the ArrayList to a view
    override fun onBindViewHolder(holder: CategoryRecyclerAdapterViewHolder, position: Int) {
        Glide.with(context).load(items.get(position).link).into(holder.product_image)
        holder.category.text = items.get(position).name
        holder.itemView.setOnClickListener {
            clickListener(items.get(position))
        }
    }
}

class CategoryRecyclerAdapterViewHolder (view: View) : RecyclerView.ViewHolder(view) {
    // Holds the TextView that will add each animal to
    val product_image = view.product_image
    val category = view.category
}

class HorizontalCategoryRecyclerAdapter(val items : List<Department>, val context: Context, val clickListener: (Department) -> Unit) : RecyclerView.Adapter<HorizontalCategoryRecyclerAdapterViewHolder>() {

    // Gets the number of animals in the list
    override fun getItemCount(): Int {
        return items.size
    }

    // Inflates the item views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HorizontalCategoryRecyclerAdapterViewHolder {
        return HorizontalCategoryRecyclerAdapterViewHolder(LayoutInflater.from(context).inflate(R.layout.item_furniture_category, parent, false))
    }

    // Binds each animal in the ArrayList to a view
    override fun onBindViewHolder(holder: HorizontalCategoryRecyclerAdapterViewHolder, position: Int) {
        Glide.with(context).load(items.get(position).link).into(holder.product_image)
        holder.category.text = items.get(position).name
        holder.itemView.setOnClickListener {
            clickListener(items.get(position))
        }
    }
}

class HorizontalCategoryRecyclerAdapterViewHolder (view: View) : RecyclerView.ViewHolder(view) {
    // Holds the TextView that will add each animal to
    val product_image = view.furniture_category_image
    val category = view.furniture_category_name
}


class FurnitureRecyclerAdapter(val items_ : List<Furniture>, val context: Context, val clickListener: (Furniture) -> Unit) : RecyclerView.Adapter<FurnitureRecyclerAdapterViewHolder>() {

    // Gets the number of animals in the list
    override fun getItemCount(): Int {
        return items_.size
    }

    // Inflates the item views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FurnitureRecyclerAdapterViewHolder {
        return FurnitureRecyclerAdapterViewHolder(LayoutInflater.from(context).inflate(R.layout.item_furniture, parent, false))
    }

    // Binds each animal in the ArrayList to a view
    override fun onBindViewHolder(holder: FurnitureRecyclerAdapterViewHolder, position: Int) {
        val item = items_.get(position)

        if(item != null) {
            Glide.with(holder.itemView.context).load(items_?.get(position)?.link).into(holder.product_image_)
            holder.furniture_name.text = items_.get(position).name
            val formatter = DecimalFormat("#,###,###");
            val saveFormattedString = formatter.format(items_.get(position).price.toInt());
            holder.furniture_price.text = "KES $saveFormattedString"

            holder.itemView.setOnClickListener {
                clickListener(item)
            }

        }

    }
}

class FurnitureRecyclerAdapterViewHolder (view: View) : RecyclerView.ViewHolder(view) {
    // Holds the TextView that will add each animal to
    val product_image_ = view.image
    val furniture_name = view.furniture_name
    val furniture_price = view.furniture_price
}

data class Department(val link : Int, val name : String)

data class Furniture(val link: Int, val name: String, val price: String)