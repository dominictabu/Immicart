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