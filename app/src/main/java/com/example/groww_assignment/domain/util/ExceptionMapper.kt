package com.example.groww_assignment.domain.util


object ExceptionMapper {

    fun mapToUserFriendlyMessage(exception: Throwable): String {
        return when (exception) {
            is StockException.InvalidSymbol -> "Please enter a valid stock symbol (1-5 letters)"
            is StockException.StockNotFound -> "Stock '${exception.symbol}' not found"
            is StockException.InsufficientData -> "Not enough data available for '${exception.symbol}'"
            is StockException.ApiRateLimitExceeded -> "Too many requests. Please wait a moment and try again"
            is StockException.NoDataAvailable -> "No stock data available right now"
            is StockException.DataParsingError -> "Unable to process stock data"

            is WatchlistException.WatchlistNotFound -> "Watchlist not found"
            is WatchlistException.InvalidWatchlistName -> "Invalid watchlist name: ${exception.reason}"
            is WatchlistException.DuplicateWatchlistName -> "A watchlist with this name already exists"
            is WatchlistException.WatchlistFull -> "Watchlist is full (max ${exception.maxItems} stocks)"
            is WatchlistException.StockAlreadyInWatchlist -> "'${exception.symbol}' is already in this watchlist"
            is WatchlistException.StockNotInWatchlist -> "'${exception.symbol}' is not in this watchlist"
            is WatchlistException.MaxWatchlistsReached -> "Maximum number of watchlists reached"
            is WatchlistException.EmptyWatchlist -> "This watchlist is empty"

            is ValidationException.EmptyInput -> "${exception.fieldName} is required"
            is ValidationException.InvalidFormat -> "Please check the format of ${exception.fieldName}"
            is ValidationException.OutOfRange -> "${exception.fieldName} must be ${exception.min}-${exception.max} characters"
            is ValidationException.InvalidCharacters -> "${exception.fieldName} contains invalid characters"
            is ValidationException.InvalidSymbol -> "Please enter a valid stock symbol"

            is CacheException.CacheExpired -> "Data needs to be refreshed"
            is CacheException.CacheKeyNotFound -> "Requested data not found in cache"
            is CacheException.CacheFull -> "Storage is full, please clear some data"
            is CacheException.CacheCorrupted -> "Data corruption detected, refreshing..."
            is CacheException.CacheUnavailable -> "Cache service unavailable"

            is NetworkException.NetworkError -> "No internet connection"
            is NetworkException.ServerError -> "Server is temporarily unavailable"
            is NetworkException.ApiLimitExceeded -> "API limit reached, please try again later"
            is NetworkException.ApiError -> "Service error: ${exception.message}"
            is NetworkException.UnknownError -> "An unexpected error occurred"

            is IllegalArgumentException -> exception.message ?: "Invalid input provided"
            is IllegalStateException -> exception.message ?: "Operation cannot be performed right now"

            else -> "Something went wrong. Please try again."
        }
    }

    fun getErrorType(exception: Throwable): ErrorType {
        return when (exception) {
            is StockException -> ErrorType.STOCK_ERROR
            is WatchlistException -> ErrorType.WATCHLIST_ERROR
            is ValidationException -> ErrorType.VALIDATION_ERROR
            is CacheException -> ErrorType.CACHE_ERROR
            is NetworkException -> ErrorType.NETWORK_ERROR
            else -> ErrorType.UNKNOWN_ERROR
        }
    }
}

enum class ErrorType {
    STOCK_ERROR,
    WATCHLIST_ERROR,
    VALIDATION_ERROR,
    CACHE_ERROR,
    NETWORK_ERROR,
    UNKNOWN_ERROR
}