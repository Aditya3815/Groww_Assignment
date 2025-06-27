package com.example.groww_assignment.domain.usecase.watchlist

import com.example.groww_assignment.domain.model.Watchlist
import com.example.groww_assignment.domain.repository.WatchlistRepository
import com.example.groww_assignment.domain.usecase.base.BaseUseCase
import com.example.groww_assignment.domain.usecase.preferences.PreferencesUseCase
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import com.example.groww_assignment.domain.util.Result
import com.example.groww_assignment.utils.Constants.DEFAULT_WATCHLIST_NAME

class GetDefaultWatchlistUseCase @Inject constructor(
    private val watchlistRepository: WatchlistRepository,
    private val preferencesUseCase: PreferencesUseCase,
    private val createWatchlistUseCase: CreateWatchlistUseCase
) : BaseUseCase<Unit, Watchlist>() {

    override suspend fun execute(parameters: Unit): Watchlist {
        val defaultWatchlistId = preferencesUseCase.getDefaultWatchlistId().first()

        if (defaultWatchlistId > 0) {
            val result = watchlistRepository.getWatchlistById(defaultWatchlistId)
            when (result) {
                is Result.Success -> return result.data
                is Result.Error -> {
                    // Watchlist might have been deleted, create a new default one
                }
                is Result.Loading -> throw Exception("Loading watchlist")
            }
        }

        // Create default watchlist if none exists
        val createResult = createWatchlistUseCase(
            CreateWatchlistUseCase.Params(DEFAULT_WATCHLIST_NAME)
        )

        return when (createResult) {
            is Result.Success -> {
                val newWatchlistId = createResult.data
                preferencesUseCase.setDefaultWatchlistId(newWatchlistId)

                // Get the created watchlist
                val getResult = watchlistRepository.getWatchlistById(newWatchlistId)
                when (getResult) {
                    is Result.Success -> getResult.data
                    is Result.Error -> throw getResult.exception
                    is Result.Loading -> throw Exception("Loading created watchlist")
                }
            }
            is Result.Error -> throw createResult.exception
            is Result.Loading -> throw Exception("Creating default watchlist")
        }
    }
}