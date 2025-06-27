package com.example.groww_assignment.presentation.screens.explore

import com.example.groww_assignment.domain.model.SearchResult
import com.example.groww_assignment.domain.model.Stock
import com.example.groww_assignment.domain.model.TopGainersLosers
import com.example.groww_assignment.domain.util.ErrorResult
import kotlin.collections.isNotEmpty

data class ExploreUiState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val topGainersLosers: TopGainersLosers? = null,
    val error: ErrorResult? = null,
    val lastUpdated: String = "",
    val searchQuery: String = "",
    val isSearchActive: Boolean = false,
    val searchResults: List<SearchResult> = emptyList(),
    val isSearchLoading: Boolean = false,
    val searchError: String? = null
) {
    val hasData: Boolean = topGainersLosers != null
    val hasError: Boolean = error != null
    val isEmpty: Boolean = !hasData && !isLoading && !hasError

    val topGainers: List<Stock> = topGainersLosers?.topGainers ?: emptyList()
    val topLosers: List<Stock> = topGainersLosers?.topLosers ?: emptyList()
    val mostActive: List<Stock> = topGainersLosers?.mostActivelyTraded ?: emptyList()

    val hasTopGainers: Boolean = topGainers.isNotEmpty()
    val hasTopLosers: Boolean = topLosers.isNotEmpty()
    val hasMostActive: Boolean = mostActive.isNotEmpty()
}