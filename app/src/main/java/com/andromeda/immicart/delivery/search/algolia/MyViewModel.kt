package com.andromeda.immicart.delivery.search.algolia


import android.util.Log
import androidx.lifecycle.LiveData
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
import com.andromeda.immicart.delivery.DeliveryCart
import io.ktor.client.features.logging.LogLevel


class MyViewModel : ViewModel() {

    private val TAG = "MyViewModel"

    val client = ClientSearch(ApplicationID("latency"), APIKey("3d9875e51fbd20c7754e65422f7ce5e1"), LogLevel.ALL)
    val index = client.initIndex(IndexName("bestbuy"))
    val searcher = SearcherSingleIndex(index)
    val stats = StatsConnector(searcher)

    fun retry() {

    }
    val dataSourceFactory = SearcherSingleIndexDataSource.Factory(searcher) { hit ->
        Product(
            hit.json.getPrimitive("name").content,
            hit.json.getPrimitive("image").content,
            hit.json.getPrimitive("salePrice").content,
            hit.json.getObjectOrNull("_highlightResult")

        )

    }

    val pagedListConfig = PagedList.Config.Builder().setPageSize(50).build()
    val products: LiveData<PagedList<Product>> = LivePagedListBuilder(dataSourceFactory, pagedListConfig).build()
    val adapterProduct = ProductAdapter() {
            retry()
    }

    val searchBox = SearchBoxConnectorPagedList(searcher, listOf(products))
    val filterState = FilterState()
    val facetList = FacetListConnector(
        searcher = searcher,
        filterState = filterState,
        attribute = Attribute("category"),
        selectionMode = SelectionMode.Single
    )
    val facetPresenter = FacetListPresenterImpl(
        sortBy = listOf(FacetSortCriterion.CountDescending, FacetSortCriterion.IsRefined),
        limit = 100
    )
    val adapterFacet = FacetListAdapter(MyFacetListViewHolder.Factory)
    val connection = ConnectionHandler()

    init {
        connection += searchBox
        connection += stats
        connection += facetList
        connection += searcher.connectFilterState(filterState)
        connection += facetList.connectView(adapterFacet, facetPresenter)
        connection += filterState.connectPagedList(products)

    }

    override fun onCleared() {
        super.onCleared()
        searcher.cancel()
        connection.disconnect()
    }

}