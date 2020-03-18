package com.andromeda.immicart.delivery.search.algolia

import android.util.Log
import android.view.View
import android.view.ViewGroup
import com.algolia.instantsearch.helper.android.filter.facet.FacetListViewHolder
import com.algolia.instantsearch.helper.android.inflate
import com.algolia.search.model.search.Facet
import com.andromeda.immicart.R

import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.algolia.instantsearch.core.Callback
import com.algolia.instantsearch.core.selectable.list.SelectableItem
import com.algolia.instantsearch.helper.filter.facet.FacetListItem
import com.algolia.instantsearch.helper.filter.facet.FacetListView

var MyFacetListViewHolder_TAG = "MyFacetListViewHolder"
var SearchMyFacetListViewHolder_TAG = "SearchFacetListAdapter"

class MyFacetListViewHolder(view: View) : FacetListViewHolder(view) {

    override fun bind(facet: Facet, selected: Boolean, onClickListener: View.OnClickListener) {
        Log.d(MyFacetListViewHolder_TAG, "createViewHolder called")

        view.setOnClickListener(onClickListener)
        view.facetCount.text = facet.count.toString()
        view.facetCount.visibility = View.VISIBLE
        view.checkbox.isChecked = if (selected) true else false
        view.facetName.text = facet.value

    }

    object Factory : FacetListViewHolder.Factory {

        override fun createViewHolder(parent: ViewGroup): FacetListViewHolder {
            Log.d(MyFacetListViewHolder_TAG, "createViewHolder called")

            return MyFacetListViewHolder(parent.inflate(R.layout.item_filter))
        }
    }
}

class MyTypeFacetListViewHolder(view: View) : FacetListViewHolder(view) {

    override fun bind(facet: Facet, selected: Boolean, onClickListener: View.OnClickListener) {
        Log.d(MyFacetListViewHolder_TAG, "createViewHolder called")

        view.setOnClickListener(onClickListener)
        view.facetCount.text = facet.count.toString()
        view.facetCount.visibility = View.VISIBLE
        view.checkbox.isChecked = if (selected) true else false
        view.facetName.text = facet.value

    }

    object Factory : FacetListViewHolder.Factory {

        override fun createViewHolder(parent: ViewGroup): FacetListViewHolder {
            Log.d(MyFacetListViewHolder_TAG, "createViewHolder called")

            return MyFacetListViewHolder(parent.inflate(R.layout.item_filter))
        }
    }
}




public class SearchFacetListAdapter(
    private val factory: FacetListViewHolder.Factory
) : ListAdapter<FacetListItem, FacetListViewHolder>(diffUtil),
    FacetListView {

    override var onSelection: Callback<Facet>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FacetListViewHolder {
        Log.d(SearchMyFacetListViewHolder_TAG, "onCreateViewHolder called")
        return factory.createViewHolder(parent)
    }

    override fun onBindViewHolder(holder: FacetListViewHolder, position: Int) {
        Log.d(SearchMyFacetListViewHolder_TAG, "onBindViewHolder called")
        val (facet, selected) = getItem(position)

        holder.bind(facet, selected, View.OnClickListener { onSelection?.invoke(facet) })
    }

    override fun setItems(items: List<SelectableItem<Facet>>) {
        Log.d(SearchMyFacetListViewHolder_TAG, "setItems called")
        Log.d(SearchMyFacetListViewHolder_TAG, "setItems : $items")
        submitList(items)
    }

    companion object {

        private val diffUtil = object : DiffUtil.ItemCallback<FacetListItem>() {

            override fun areItemsTheSame(
                oldItem: FacetListItem,
                newItem: FacetListItem
            ): Boolean {
                return oldItem::class == newItem::class
            }

            override fun areContentsTheSame(
                oldItem: FacetListItem,
                newItem: FacetListItem
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}