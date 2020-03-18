package com.andromeda.immicart.delivery.furniture.algolia

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.algolia.instantsearch.helper.android.highlighting.toSpannedString
import com.andromeda.immicart.R
import com.andromeda.immicart.delivery.search.algolia.Product
import kotlinx.android.synthetic.main.item_furniture.view.*


class ProductAdapter : PagedListAdapter<Product, ProductViewHolder>(ProductAdapter) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_furniture, parent, false)

        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = getItem(position)

        if (product != null) holder.bind(product)
    }

    companion object : DiffUtil.ItemCallback<Product>() {

        override fun areItemsTheSame(
            oldItem: Product,
            newItem: Product
        ): Boolean {
            return oldItem::class == newItem::class
        }

        override fun areContentsTheSame(
            oldItem: Product,
            newItem: Product
        ): Boolean {
            return oldItem.name == newItem.name
        }
    }
}

class ProductViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

    fun bind(product: Product) {
        view.furniture_price.text = "KES ${product.price}"
        view.furniture_name.text = product.highlightedName?.toSpannedString() ?: product.name
    }
}