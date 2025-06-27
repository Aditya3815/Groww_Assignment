package com.example.groww_assignment.utils

object Constants {
    const val BASE_URL = "https://www.alphavantage.co/"

    const val CACHE_DURATION_MINUTES = 15L
    const val MAX_CACHE_SIZE = 50L * 1024 * 1024 // 50MB

    const val PAGE_SIZE = 20

    const val DATABASE_NAME = "stocks_database"
    const val DATABASE_VERSION = 1

    const val DEFAULT_WATCHLIST_NAME = "My Watchlist"
    const val MAX_WATCHLIST_ITEMS = 50
    const val MAX_WATCHLIST_COUNT = 10
}