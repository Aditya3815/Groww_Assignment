package com.example.groww_assignment.presentation.screens.view_all

import com.example.groww_assignment.domain.model.Stock
import com.example.groww_assignment.domain.usecase.pagination.PaginationResult
import com.example.groww_assignment.domain.util.ErrorResult
import kotlin.collections.isNotEmpty

data class ViewAllUiState(
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val isRefreshing: Boolean = false,
    val stocks: List<Stock> = emptyList(),
    val error: ErrorResult? = null,
    val stockType: String = "",
    val pagination: PaginationResult<Stock>? = null,
    val searchQuery: String = "",
    val isSearchActive: Boolean = false,
    val filteredStocks: List<Stock> = emptyList()
) {
    val hasData: Boolean = stocks.isNotEmpty()
    val hasError: Boolean = error != null
    val isEmpty: Boolean = !hasData && !isLoading && !hasError
    val canLoadMore: Boolean = pagination?.hasNextPage == true && !isLoadingMore

    val displayStocks: List<Stock> = if (isSearchActive && searchQuery.isNotEmpty()) {
        filteredStocks
    } else {
        stocks
    }

    val title: String = when (stockType.lowercase()) {
        "gainers", "top_gainers" -> "Top Gainers"
        "losers", "top_losers" -> "Top Losers"
        "active", "most_active" -> "Most Active"
        else -> "Stocks"
    }

    val subtitle: String = when (stockType.lowercase()) {
        "gainers", "top_gainers" -> "Best performing stocks"
        "losers", "top_losers" -> "Biggest declines today"
        "active", "most_active" -> "High volume trading stocks"
        else -> "Stock listings"
    }
}
