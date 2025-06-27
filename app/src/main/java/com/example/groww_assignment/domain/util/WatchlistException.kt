package com.example.groww_assignment.domain.util

sealed class WatchlistException(message: String, cause: Throwable? = null) : Exception(message, cause) {

    data class WatchlistNotFound(val id: Long) : WatchlistException(
        "Watchlist not found with ID: $id"
    )

    data class InvalidWatchlistName(val name: String, val reason: String) : WatchlistException(
        "Invalid watchlist name '$name': $reason"
    )

    data class DuplicateWatchlistName(val name: String) : WatchlistException(
        "Watchlist with name '$name' already exists"
    )

    data class WatchlistFull(val maxItems: Int) : WatchlistException(
        "Watchlist is full. Maximum $maxItems items allowed."
    )

    data class StockAlreadyInWatchlist(val symbol: String, val watchlistName: String) : WatchlistException(
        "Stock $symbol is already in watchlist '$watchlistName'"
    )

    data class StockNotInWatchlist(val symbol: String, val watchlistName: String) : WatchlistException(
        "Stock $symbol is not in watchlist '$watchlistName'"
    )

    object MaxWatchlistsReached : WatchlistException(
        "Maximum number of watchlists reached"
    )

    data class EmptyWatchlist(val name: String) : WatchlistException(
        "Watchlist '$name' is empty"
    )
}