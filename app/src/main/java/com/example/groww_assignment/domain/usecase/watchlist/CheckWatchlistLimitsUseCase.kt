package com.example.groww_assignment.domain.usecase.watchlist

import com.example.groww_assignment.domain.repository.WatchlistRepository
import com.example.groww_assignment.domain.usecase.base.BaseUseCase
import com.example.groww_assignment.utils.Constants.MAX_WATCHLIST_COUNT
import com.example.groww_assignment.utils.Constants.MAX_WATCHLIST_ITEMS
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class CheckWatchlistLimitsUseCase @Inject constructor(
    private val repository: WatchlistRepository,
    private val getStocksInWatchlistUseCase: GetStocksInWatchlistUseCase
) : BaseUseCase<CheckWatchlistLimitsUseCase.Params, WatchlistLimitResult>() {

    override suspend fun execute(parameters: Params): WatchlistLimitResult {
        return when (parameters.limitType) {
            LimitType.MAX_WATCHLISTS -> checkMaxWatchlists()
            LimitType.MAX_STOCKS_PER_WATCHLIST -> checkMaxStocksInWatchlist(parameters.watchlistId!!)
        }
    }

    private suspend fun checkMaxWatchlists(): WatchlistLimitResult {
        val watchlists = repository.getAllWatchlists().first()
        val currentCount = watchlists.size
        val maxAllowed = MAX_WATCHLIST_COUNT

        return WatchlistLimitResult(
            isAtLimit = currentCount >= maxAllowed,
            currentCount = currentCount,
            maxAllowed = maxAllowed,
            remaining = maxAllowed - currentCount
        )
    }

    private suspend fun checkMaxStocksInWatchlist(watchlistId: Long): WatchlistLimitResult {
        val stocks = getStocksInWatchlistUseCase(watchlistId).first()
        val currentCount = stocks.size
        val maxAllowed = MAX_WATCHLIST_ITEMS

        return WatchlistLimitResult(
            isAtLimit = currentCount >= maxAllowed,
            currentCount = currentCount,
            maxAllowed = maxAllowed,
            remaining = maxAllowed - currentCount
        )
    }

    data class Params(
        val limitType: LimitType,
        val watchlistId: Long? = null
    )
}

enum class LimitType {
    MAX_WATCHLISTS,
    MAX_STOCKS_PER_WATCHLIST
}

data class WatchlistLimitResult(
    val isAtLimit: Boolean,
    val currentCount: Int,
    val maxAllowed: Int,
    val remaining: Int
) {
    val canAddMore: Boolean = !isAtLimit
    val utilizationPercentage: Float = (currentCount.toFloat() / maxAllowed) * 100f
}
