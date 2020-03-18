package com.andromeda.immicart.delivery.furniture.algolia

import android.util.Log
import android.view.View
import android.view.ViewGroup
import com.algolia.instantsearch.helper.android.filter.facet.FacetListViewHolder
import com.algolia.instantsearch.helper.android.inflate
import com.algolia.search.model.search.Facet
import com.andromeda.immicart.R
//import kotlinx.android.synthetic.main.facet_item.view.*
//import kotlinx.android.synthetic.main.facet_item.view.facetCount
//import kotlinx.android.synthetic.main.facet_item.view.facetName
import kotlinx.android.synthetic.main.item_filter.view.*

class MyFacetListViewHolder(view: View) : FacetListViewHolder(view) {

    val TAG = "MyFacetListViewHolder"
    override fun bind(facet: Facet, selected: Boolean, onClickListener: View.OnClickListener) {
//        view.setOnClickListener(onClickListener)
//        view.facetCount.text = facet.count.toString()
//        view.facetCount.visibility = View.VISIBLE
//        view.icon.visibility = if (selected) View.VISIBLE else View.INVISIBLE
//        view.facetName.text = facet.value
        Log.d(TAG,"MyFacetListViewHolder bind $facet" )
        Log.d(TAG,"MyFacetListViewHolder bind facet.value ${facet.value}" )
        Log.d(TAG,"MyFacetListViewHolder selected $selected" )

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