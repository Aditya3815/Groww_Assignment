package com.example.groww_assignment.domain.usecase.watchlist

import com.example.groww_assignment.domain.model.Stock
import com.example.groww_assignment.domain.repository.WatchlistRepository
import com.example.groww_assignment.domain.usecase.base.BaseUseCase
import com.example.groww_assignment.domain.util.Result
import javax.inject.Inject

class AddToWatchlistUseCase @Inject constructor(
    private val repository: WatchlistRepository
) : BaseUseCase<AddToWatchlistUseCase.Params, Unit>() {

    override suspend fun execute(parameters: Params): Unit {
        validateParams(parameters)

        // Check if stock is already in watchlist
        val isAlreadyAdded = repository.isStockInWatchlist(
            parameters.watchlistId,
            parameters.stock.symbol
        )

        if (isAlreadyAdded) {
            throw IllegalStateException("Stock is already in this watchlist")
        }

        val result = repository.addStockToWatchlist(parameters.watchlistId, parameters.stock)

        when (result) {
            is Result.Success -> return
            is Result.Error -> throw result.exception
            is Result.Loading -> throw Exception("Adding to watchlist...")
        }
    }

    private fun validateParams(parameters: Params) {
        when {
            parameters.watchlistId <= 0 -> throw IllegalArgumentException("Invalid watchlist ID")
            parameters.stock.symbol.isBlank() -> throw IllegalArgumentException("Invalid stock symbol")
        }
    }

    data class Params(
        val watchlistId: Long,
        val stock: Stock
    )
}