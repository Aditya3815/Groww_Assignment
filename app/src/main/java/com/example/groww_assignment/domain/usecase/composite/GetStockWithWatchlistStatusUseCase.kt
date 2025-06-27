package com.example.groww_assignment.domain.usecase.composite

import com.example.groww_assignment.domain.model.StockDetail
import com.example.groww_assignment.domain.usecase.base.BaseUseCase
import com.example.groww_assignment.domain.usecase.stocks.GetStockDetailUseCase
import com.example.groww_assignment.domain.usecase.watchlist.IsStockInWatchlistUseCase
import javax.inject.Inject
import com.example.groww_assignment.domain.util.Result


class GetStockWithWatchlistStatusUseCase @Inject constructor(
    private val getStockDetailUseCase: GetStockDetailUseCase,
    private val isStockInWatchlistUseCase: IsStockInWatchlistUseCase
) : BaseUseCase<GetStockWithWatchlistStatusUseCase.Params, StockWithWatchlistStatus>() {

    override suspend fun execute(parameters: Params): StockWithWatchlistStatus {
        val stockDetailResult = getStockDetailUseCase(
            GetStockDetailUseCase.Params(parameters.symbol)
        )

        val stockDetail = when (stockDetailResult) {
            is Result.Success -> stockDetailResult.data
            is Result.Error -> throw stockDetailResult.exception
            is Result.Loading -> throw Exception("Loading stock data")
        }

        val watchlistStatuses = mutableMapOf<Long, Boolean>()

        parameters.watchlistIds.forEach { watchlistId ->
            val isInWatchlist = isStockInWatchlistUseCase(
                IsStockInWatchlistUseCase.Params(watchlistId, parameters.symbol)
            )
            when (isInWatchlist) {
                is Result.Success -> {
                    watchlistStatuses[watchlistId] = isInWatchlist.data
                }
                else -> {
                    watchlistStatuses[watchlistId] = false
                }
            }
        }

        return StockWithWatchlistStatus(
            stockDetail = stockDetail,
            watchlistStatuses = watchlistStatuses
        )
    }

    data class Params(
        val symbol: String,
        val watchlistIds: List<Long>
    )
}

data class StockWithWatchlistStatus(
    val stockDetail: StockDetail,
    val watchlistStatuses: Map<Long, Boolean>
) {
    fun isInWatchlist(watchlistId: Long): Boolean {
        return watchlistStatuses[watchlistId] == true
    }

    fun isInAnyWatchlist(): Boolean {
        return watchlistStatuses.values.any { it }
    }
}