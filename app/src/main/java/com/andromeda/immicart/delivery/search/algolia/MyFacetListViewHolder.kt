package com.andromeda.immicart.delivery.search.algolia

import android.view.View
import android.view.ViewGroup
import com.algolia.instantsearch.helper.android.filter.facet.FacetListViewHolder
import com.algolia.instantsearch.helper.android.inflate
import com.algolia.search.model.search.Facet
import com.andromeda.immicart.R

class MyFacetListViewHolder(view: View) : FacetListViewHolder(view) {

    override fun bind(facet: Facet, selected: Boolean, onClickListener: View.OnClickListener) {
        view.setOnClickListener(onClickListener)
        view.facetCount.text = facet.count.toString()
        view.facetCount.visibility = View.VISIBLE
        view.checkbox.isChecked = if (selected) true else false
        view.facetName.text = facet.value

    }

    object Factory : FacetListViewHolder.Factory {

        override fun createViewHolder(parent: ViewGroup): FacetListViewHolder {
            return MyFacetListViewHolder(parent.inflate(R.layout.item_filter))
        }
    }
}