package com.example.groww_assignment.domain.util

sealed class StockException(message: String, cause: Throwable? = null) : Exception(message, cause) {

    data class InvalidSymbol(val symbol: String) : StockException(
        "Invalid stock symbol: $symbol"
    )

    data class StockNotFound(val symbol: String) : StockException(
        "Stock not found: $symbol"
    )

    data class InsufficientData(val symbol: String) : StockException(
        "Insufficient data available for: $symbol"
    )

    object ApiRateLimitExceeded : StockException(
        "API rate limit exceeded. Please try again later."
    )

    object NoDataAvailable : StockException(
        "No stock data available at this time"
    )

    data class DataParsingError(val rawData: String) : StockException(
        "Failed to parse stock data: $rawData"
    )
}