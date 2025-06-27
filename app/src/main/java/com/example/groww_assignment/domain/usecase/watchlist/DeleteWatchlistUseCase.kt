package com.example.groww_assignment.domain.usecase.watchlist

import com.example.groww_assignment.domain.model.Watchlist
import com.example.groww_assignment.domain.repository.WatchlistRepository
import com.example.groww_assignment.domain.usecase.base.BaseUseCase
import javax.inject.Inject
import com.example.groww_assignment.domain.util.Result

class DeleteWatchlistUseCase @Inject constructor(
    private val repository: WatchlistRepository
) : BaseUseCase<DeleteWatchlistUseCase.Params, Unit>() {

    override suspend fun execute(parameters: Params): Unit {
        if (parameters.watchlist.id <= 0) {
            throw IllegalArgumentException("Invalid watchlist ID")
        }

        val result = repository.deleteWatchlist(parameters.watchlist)

        when (result) {
            is Result.Success -> return
            is Result.Error -> throw result.exception
            is Result.Loading -> throw Exception("Deleting watchlist...")
        }
    }

    data class Params(
        val watchlist: Watchlist
    )
}