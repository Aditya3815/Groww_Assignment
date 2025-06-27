package com.example.groww_assignment.domain.usecase.cache

import com.example.groww_assignment.data.local.cache.CacheManager
import com.example.groww_assignment.domain.usecase.base.BaseUseCase
import javax.inject.Inject

class CacheManagementUseCase @Inject constructor(
    private val cacheManager: CacheManager
) : BaseUseCase<CacheManagementUseCase.Params, CacheStatus>() {

    override suspend fun execute(parameters: Params): CacheStatus {
        return when (parameters.action) {
            CacheAction.CHECK_EXPIRY -> {
                val isExpired = cacheManager.isDataExpired(parameters.key ?: "")
                CacheStatus.Checked(isExpired)
            }
            CacheAction.UPDATE_TIMESTAMP -> {
                cacheManager.updateCacheTimestamp(parameters.key ?: "")
                CacheStatus.Updated
            }
            CacheAction.CLEAR_ALL -> {
                cacheManager.clearCache()
                CacheStatus.Cleared
            }
            CacheAction.CLEAR_KEY -> {
                cacheManager.clearCacheForKey(parameters.key ?: "")
                CacheStatus.Cleared
            }
        }
    }

    data class Params(
        val action: CacheAction,
        val key: String? = null
    )
}

enum class CacheAction {
    CHECK_EXPIRY,
    UPDATE_TIMESTAMP,
    CLEAR_ALL,
    CLEAR_KEY
}

sealed class CacheStatus {
    data class Checked(val isExpired: Boolean) : CacheStatus()
    object Updated : CacheStatus()
    object Cleared : CacheStatus()
}