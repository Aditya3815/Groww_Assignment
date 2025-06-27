package com.example.groww_assignment.domain.usecase.watchlist

import com.example.groww_assignment.domain.repository.WatchlistRepository
import com.example.groww_assignment.domain.usecase.base.BaseUseCase
import javax.inject.Inject

class IsStockInWatchlistUseCase @Inject constructor(
    private val repository: WatchlistRepository
) : BaseUseCase<IsStockInWatchlistUseCase.Params, Boolean>() {

    override suspend fun execute(parameters: Params): Boolean {
        validateParams(parameters)
        return repository.isStockInWatchlist(parameters.watchlistId, parameters.stockSymbol)
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