package com.example.groww_assignment.domain.usecase.preferences

import com.example.groww_assignment.data.local.datastore.PreferencesManager
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class PreferencesUseCase @Inject constructor(
    private val preferencesManager: PreferencesManager
) {

    fun getDefaultWatchlistId(): Flow<Long> = preferencesManager.defaultWatchlistId

    suspend fun setDefaultWatchlistId(id: Long) {
        preferencesManager.setDefaultWatchlistId(id)
    }
    suspend fun incrementApiCallCount() {
        preferencesManager.incrementApiCallCount()
    }
}