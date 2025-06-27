package com.example.groww_assignment.domain.usecase.watchlist

import com.example.groww_assignment.domain.repository.WatchlistRepository
import com.example.groww_assignment.domain.usecase.base.BaseUseCase
import javax.inject.Inject
import com.example.groww_assignment.domain.util.Result
class CreateWatchlistUseCase @Inject constructor(
    private val repository: WatchlistRepository
) : BaseUseCase<CreateWatchlistUseCase.Params, Long>() {

    override suspend fun execute(parameters: Params): Long {
        validateWatchlistName(parameters.name)

        val result = repository.createWatchlist(parameters.name.trim())

        return when (result) {
            is Result.Success -> result.data
            is Result.Error -> throw result.exception
            is Result.Loading -> throw Exception("Creating watchlist...")
        }
    }

    private fun validateWatchlistName(name: String) {
        when {
            name.isBlank() -> throw IllegalArgumentException("Watchlist name cannot be empty")
            name.length < 2 -> throw IllegalArgumentException("Watchlist name must be at least 2 characters")
            name.length > 50 -> throw IllegalArgumentException("Watchlist name cannot exceed 50 characters")
            !name.matches(Regex("^[a-zA-Z0-9\\s-_]+$")) -> throw IllegalArgumentException("Watchlist name contains invalid characters")
        }
    }

    data class Params(
        val name: String
    )
}
