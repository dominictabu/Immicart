package com.andromeda.immicart.delivery.search.algolia

import com.algolia.instantsearch.core.highlighting.HighlightedString
import com.algolia.instantsearch.helper.highlighting.Highlightable
import com.algolia.search.model.Attribute
import kotlinx.serialization.json.JsonObject


data class Product(
    val name: String,
    val image: String,
    val price: String,
    override val _highlightResult: JsonObject?
) : Highlightable {

    public val highlightedName: HighlightedString?
        get() = getHighlight(Attribute("name"))
}


data class DeliveryCartSearch(val key: String, val barcode: String, val name: String, val category: String, val offerPrice: Int, val normalPrice: Int, val quantity : Int,
                             val image_url: String, var isInCart: Boolean? = false,
                              override val _highlightResult: JsonObject?
)  : Highlightable {

    public val highlightedName: HighlightedString?
        get() = getHighlight(Attribute("name"))
}