package com.example.groww_assignment.domain.util

sealed class CacheException(message: String, cause: Throwable? = null) : Exception(message, cause) {

    object CacheExpired : CacheException(
        "Cached data has expired"
    )

    data class CacheKeyNotFound(val key: String) : CacheException(
        "Cache key not found: $key"
    )

    object CacheFull : CacheException(
        "Cache storage is full"
    )

    data class CacheCorrupted(val key: String) : CacheException(
        "Cached data is corrupted for key: $key"
    )

    object CacheUnavailable : CacheException(
        "Cache service is currently unavailable"
    )
}