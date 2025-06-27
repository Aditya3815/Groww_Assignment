package com.example.groww_assignment.domain.usecase.watchlist

import com.example.groww_assignment.domain.repository.WatchlistRepository
import com.example.groww_assignment.domain.usecase.base.BaseUseCase
import javax.inject.Inject
import com.example.groww_assignment.domain.util.Result

class RemoveFromWatchlistUseCase @Inject constructor(
    private val repository: WatchlistRepository
) : BaseUseCase<RemoveFromWatchlistUseCase.Params, Unit>() {

    override suspend fun execute(parameters: Params): Unit {
        validateParams(parameters)

        // Check if stock is in watchlist
        val isInWatchlist = repository.isStockInWatchlist(
            parameters.watchlistId,
            parameters.stockSymbol
        )

        if (!isInWatchlist) {
            throw IllegalStateException("Stock is not in this watchlist")
        }

        val result = repository.removeStockFromWatchlist(parameters.watchlistId, parameters.stockSymbol)

        when (result) {
            is Result.Success -> return
            is Result.Error -> throw result.exception
            is Result.Loading -> throw Exception("Removing from watchlist...")
        }
    }

    private fun validateParams(parameters: Params) {
        when {
            parameters.watchlistId <= 0 -> throw IllegalArgumentException("Invalid watchlist ID")
            parameters.stockSymbol.isBlank() -> throw IllegalArgumentException("Invalid stock symbol")
        }
    }

    data class Params(
        val watchlistId: Long,
        val stockSymbol: String
    )
}
