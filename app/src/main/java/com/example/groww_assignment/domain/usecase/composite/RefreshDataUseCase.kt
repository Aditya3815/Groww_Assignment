package com.example.groww_assignment.domain.usecase.composite

import com.example.groww_assignment.domain.model.TopGainersLosers
import com.example.groww_assignment.domain.usecase.base.BaseUseCase
import com.example.groww_assignment.domain.usecase.cache.CacheAction
import com.example.groww_assignment.domain.usecase.cache.CacheManagementUseCase
import com.example.groww_assignment.domain.usecase.network.NetworkStatus
import com.example.groww_assignment.domain.usecase.network.NetworkStatusUseCase
import com.example.groww_assignment.domain.usecase.preferences.PreferencesUseCase
import com.example.groww_assignment.domain.usecase.stocks.GetTopGainersLosersUseCase
import javax.inject.Inject
import com.example.groww_assignment.domain.util.Result


class RefreshDataUseCase @Inject constructor(
    private val getTopGainersLosersUseCase: GetTopGainersLosersUseCase,
    private val networkStatusUseCase: NetworkStatusUseCase,
    private val cacheManagementUseCase: CacheManagementUseCase,
    private val preferencesUseCase: PreferencesUseCase
) : BaseUseCase<RefreshDataUseCase.Params, RefreshResult>() {

    override suspend fun execute(parameters: Params): RefreshResult {
        // Check network status
        val networkStatusResult = networkStatusUseCase(Unit)
        val networkStatus = when (networkStatusResult) {
            is    Result.Success -> networkStatusResult.data
            is    Result.Error -> throw networkStatusResult.exception
            is    Result.Loading -> throw Exception("Checking network")
        }

        if (!networkStatus.isConnected && parameters.requireNetwork) {
            return RefreshResult.NetworkError("No internet connection available")
        }

        // Increment API call count
        preferencesUseCase.incrementApiCallCount()

        // Clear cache if forced refresh
        if (parameters.forceRefresh) {
            cacheManagementUseCase(
                CacheManagementUseCase.Params(CacheAction.CLEAR_ALL)
            )
        }

        // Refresh data
        val refreshResult = getTopGainersLosersUseCase(
            GetTopGainersLosersUseCase.Params(forceRefresh = parameters.forceRefresh)
        )

        return when (refreshResult) {
            is    Result.Success -> {
                RefreshResult.Success(
                    data = refreshResult.data,
                    networkStatus = networkStatus,
                    timestamp = System.currentTimeMillis()
                )
            }
            is    Result.Error -> {
                RefreshResult.Error(refreshResult.exception.message ?: "Refresh failed")
            }
            is    Result.Loading -> {
                RefreshResult.Loading
            }
        }
    }

    data class Params(
        val forceRefresh: Boolean = false,
        val requireNetwork: Boolean = true
    )
}

sealed class RefreshResult {
    data class Success(
        val data: TopGainersLosers,
        val networkStatus: NetworkStatus,
        val timestamp: Long
    ) : RefreshResult()

    data class Error(val message: String) : RefreshResult()
    data class NetworkError(val message: String) : RefreshResult()
    object Loading : RefreshResult()
}