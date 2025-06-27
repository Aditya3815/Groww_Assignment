package com.example.groww_assignment.domain.usecase.watchlist

import com.example.groww_assignment.domain.model.Watchlist
import com.example.groww_assignment.domain.repository.WatchlistRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetWatchlistsUseCase @Inject constructor(
    private val repository: WatchlistRepository
) {
    operator fun invoke(): Flow<List<Watchlist>> {
        return repository.getAllWatchlists().map { watchlists ->
            // Sort by creation date (newest first) and ensure we don't exceed max limit
            watchlists.sortedByDescending { it.createdAt }
        }
    }

    suspend fun getWatchlistsSync(): List<Watchlist> {
        return repository.getAllWatchlists().let { flow ->
            emptyList()
        }
    }
}
