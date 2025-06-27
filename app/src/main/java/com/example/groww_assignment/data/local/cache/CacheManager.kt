package com.example.groww_assignment.data.local.cache

import com.example.groww_assignment.utils.Constants.CACHE_DURATION_MINUTES
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CacheManager @Inject constructor(
) {
    private val cacheTimestamps = mutableMapOf<String, Long>()

    fun isDataExpired(key: String): Boolean {
        val timestamp = cacheTimestamps[key] ?: return true
        val currentTime = System.currentTimeMillis()
        val expirationTime = timestamp + (CACHE_DURATION_MINUTES * 60 * 1000)
        return currentTime > expirationTime
    }

    fun updateCacheTimestamp(key: String) {
        cacheTimestamps[key] = System.currentTimeMillis()
    }

    fun clearCache() {
        cacheTimestamps.clear()
    }

    fun clearCacheForKey(key: String) {
        cacheTimestamps.remove(key)
    }

    companion object {
        const val TOP_GAINERS_LOSERS_KEY = "top_gainers_losers"
        const val STOCK_DETAIL_KEY_PREFIX = "stock_detail_"
        const val TIME_SERIES_KEY_PREFIX = "time_series_"
        const val SEARCH_KEY_PREFIX = "search_"
    }
}