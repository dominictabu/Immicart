package com.andromeda.immicart.delivery.search.algolia


import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.algolia.instantsearch.core.connection.ConnectionHandler
import com.algolia.instantsearch.core.selectable.list.SelectionMode
import com.algolia.instantsearch.helper.android.filter.facet.FacetListAdapter
import com.algolia.instantsearch.helper.android.filter.state.connectPagedList
import com.algolia.instantsearch.helper.android.list.SearcherSingleIndexDataSource
import com.algolia.instantsearch.helper.android.searchbox.SearchBoxConnectorPagedList
import com.algolia.instantsearch.helper.filter.facet.FacetListConnector
import com.algolia.instantsearch.helper.filter.facet.FacetListPresenterImpl
import com.algolia.instantsearch.helper.filter.facet.FacetSortCriterion
import com.algolia.instantsearch.helper.filter.facet.connectView
import com.algolia.instantsearch.helper.filter.state.FilterState
import com.algolia.instantsearch.helper.searcher.SearcherSingleIndex
import com.algolia.instantsearch.helper.searcher.connectFilterState
import com.algolia.instantsearch.helper.stats.StatsConnector
import com.algolia.search.client.ClientSearch
import com.algolia.search.model.APIKey
import com.algolia.search.model.ApplicationID
import com.algolia.search.model.Attribute
import com.algolia.search.model.IndexName
import com.algolia.search.model.search.Query
import com.andromeda.immicart.delivery.DeliveryCart
import io.ktor.client.features.logging.LogLevel


class MyViewModel(indexName : String) : ViewModel() {

    private val TAG = "MyViewModel"

//    val client = ClientSearch(ApplicationID("latency"), APIKey("3d9875e51fbd20c7754e65422f7ce5e1"), LogLevel.ALL)
//    val index = client.initIndex(IndexName("bestbuy"))

    val client = ClientSearch(ApplicationID("TV1YRRL3K4"), APIKey("6de0552fc52736f7f2891edbf087b2f9"), LogLevel.ALL)
//    val indexName = MutableLiveData<String>()

    val index = client.initIndex(IndexName(indexName))

    val searcher = SearcherSingleIndex(index)
//    var searcher_ : SearcherSingleIndex
    val stats = StatsConnector(searcher)
    val storeID = MutableLiveData<String>()
//    fun setindexName() {
//        indexName.value()
//    }
    fun setStoreID(word: String) {
     storeID.value = word
    }

    val dataSourceFactory = SearcherSingleIndexDataSource.Factory(searcher) { hit ->
        DeliveryCartSearch(
            hit.json.getPrimitive("objectID").content,
            hit.json.getPrimitive("barcode").content,
            hit.json.getPrimitive("name").content,
            hit.json.getPrimitive("categoryOne").content,
            hit.json.getPrimitive("offer_price").content.toInt(),
            hit.json.getPrimitive("normal_price").content.toInt(),
            0,
            hit.json.getPrimitive("imageUrl").content,
            false,
            hit.json.getObjectOrNull("_highlightResult")
        )

    }

    val pagedListConfig = PagedList.Config.Builder().setPageSize(50).build()
    val products: LiveData<PagedList<DeliveryCartSearch>> = LivePagedListBuilder(dataSourceFactory, pagedListConfig).build()

    val searchBox = SearchBoxConnectorPagedList(searcher, listOf(products))
    val filterState = FilterState()
    val facetList = FacetListConnector(
        searcher = searcher,
        filterState = filterState,
        attribute = Attribute("categoryOne"),
        selectionMode = SelectionMode.Multiple
    )
    val secondFacetList = FacetListConnector(
        searcher = searcher,
        filterState = filterState,
        attribute = Attribute("categoryTwo"),
        selectionMode = SelectionMode.Multiple
    )
    var facetPresenter = FacetListPresenterImpl(
        sortBy = listOf(FacetSortCriterion.CountDescending, FacetSortCriterion.IsRefined),
        limit = 10
    )
    val adapterFacet = SearchFacetListAdapter(MyFacetListViewHolder.Factory)
    val typeAdapterFacet = SearchFacetListAdapter(MyTypeFacetListViewHolder.Factory)
    val connection = ConnectionHandler()

    init {
        connection += searchBox
        connection += stats
        connection += facetList
        connection += secondFacetList
        connection += searcher.connectFilterState(filterState)
        connection += facetList.connectView(adapterFacet, facetPresenter)
        connection += secondFacetList.connectView(typeAdapterFacet, facetPresenter)
        connection += filterState.connectPagedList(products)

    }

    override fun onCleared() {
        super.onCleared()
        searcher.cancel()
        connection.disconnect()
    }

}