package com.example.groww_assignment.presentation.screens.watchlist

import com.example.groww_assignment.domain.model.Stock
import com.example.groww_assignment.domain.model.Watchlist
import com.example.groww_assignment.domain.util.ErrorResult
import kotlin.collections.isNotEmpty

data class WatchlistUiState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val watchlists: List<Watchlist> = emptyList(),
    val selectedWatchlist: Watchlist? = null,
    val stocksInSelectedWatchlist: List<Stock> = emptyList(),
    val error: ErrorResult? = null,

    // UI State
    val viewMode: WatchlistViewMode = WatchlistViewMode.ALL_WATCHLISTS,
    val showCreateDialog: Boolean = false,
    val showDeleteDialog: Boolean = false,
    val watchlistToDelete: Watchlist? = null,
    val searchQuery: String = "",
    val isSearchActive: Boolean = false,
    val filteredStocks: List<Stock> = emptyList(),

    // Operations
    val isCreatingWatchlist: Boolean = false,
    val isDeletingWatchlist: Boolean = false,
    val isUpdatingStocks: Boolean = false
) {
    val hasWatchlists: Boolean = watchlists.isNotEmpty()
    val hasError: Boolean = error != null
    val isEmpty: Boolean = !hasWatchlists && !isLoading && !hasError

    val displayStocks: List<Stock> = if (isSearchActive && searchQuery.isNotEmpty()) {
        filteredStocks
    } else {
        stocksInSelectedWatchlist
    }

    val hasStocksInSelected: Boolean = stocksInSelectedWatchlist.isNotEmpty()
    val selectedWatchlistEmpty: Boolean = selectedWatchlist != null && stocksInSelectedWatchlist.isEmpty()

    val canCreateWatchlist: Boolean = watchlists.size < 10 // Max watchlists limit
}

enum class WatchlistViewMode {
    ALL_WATCHLISTS,
    SINGLE_WATCHLIST
}