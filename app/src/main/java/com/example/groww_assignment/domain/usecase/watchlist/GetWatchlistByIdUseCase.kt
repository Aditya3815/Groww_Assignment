package com.example.groww_assignment.domain.usecase.watchlist

import com.example.groww_assignment.domain.model.Watchlist
import com.example.groww_assignment.domain.repository.WatchlistRepository
import com.example.groww_assignment.domain.usecase.base.BaseUseCase
import javax.inject.Inject
import com.example.groww_assignment.domain.util.Result

class GetWatchlistByIdUseCase @Inject constructor(
    private val repository: WatchlistRepository
) : BaseUseCase<GetWatchlistByIdUseCase.Params, Watchlist>() {

    override suspend fun execute(parameters: Params): Watchlist {
        if (parameters.id <= 0) {
            throw IllegalArgumentException("Invalid watchlist ID")
        }

        val result = repository.getWatchlistById(parameters.id)

        return when (result) {
            is Result.Success -> result.data
            is Result.Error -> throw result.exception
            is Result.Loading -> throw Exception("Loading watchlist...")
        }
    }

    data class Params(
        val id: Long
    )
}