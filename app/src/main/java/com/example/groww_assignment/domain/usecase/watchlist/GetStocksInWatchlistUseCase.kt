package com.example.groww_assignment.domain.usecase.watchlist

import com.example.groww_assignment.domain.model.Stock
import com.example.groww_assignment.domain.repository.WatchlistRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetStocksInWatchlistUseCase @Inject constructor(
    private val repository: WatchlistRepository
) {
    operator fun invoke(watchlistId: Long): Flow<List<Stock>> {
        require(watchlistId > 0) { "Invalid watchlist ID" }
        return repository.getStocksInWatchlist(watchlistId)
    }
}