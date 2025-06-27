package com.example.groww_assignment.domain.usecase.network

import com.example.groww_assignment.domain.usecase.base.BaseUseCase
import com.example.groww_assignment.utils.NetworkType
import com.example.groww_assignment.utils.NetworkUtils
import javax.inject.Inject

class NetworkStatusUseCase @Inject constructor(
    private val networkUtils: NetworkUtils
) : BaseUseCase<Unit, NetworkStatus>() {

    override suspend fun execute(parameters: Unit): NetworkStatus {
        val isAvailable = networkUtils.isNetworkAvailable()
        val networkType = networkUtils.getNetworkType()

        return NetworkStatus(
            isAvailable = isAvailable,
            networkType = networkType,
            isWifi = networkType == NetworkType.WIFI,
            isCellular = networkType == NetworkType.CELLULAR
        )
    }
}

data class NetworkStatus(
    val isAvailable: Boolean,
    val networkType: NetworkType,
    val isWifi: Boolean,
    val isCellular: Boolean
) {
    val isConnected: Boolean = isAvailable
    val connectionQuality: ConnectionQuality = when {
        !isAvailable -> ConnectionQuality.NONE
        isWifi -> ConnectionQuality.EXCELLENT
        isCellular -> ConnectionQuality.GOOD
        else -> ConnectionQuality.POOR
    }
}

enum class ConnectionQuality {
    NONE, POOR, GOOD, EXCELLENT
}