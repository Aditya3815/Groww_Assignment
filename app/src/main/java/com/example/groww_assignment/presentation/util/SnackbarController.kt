package com.example.groww_assignment.presentation.util

import androidx.compose.material3.SnackbarDuration
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SnackbarController @Inject constructor() {

    private val _events = Channel<SnackbarEvent>()
    val events = _events.receiveAsFlow()

    suspend fun showSnackbar(
        message: String,
        actionLabel: String? = null,
        duration: SnackbarDuration = SnackbarDuration.Short
    ) {
        _events.send(
            SnackbarEvent(
                message = message,
                actionLabel = actionLabel,
                duration = duration
            )
        )
    }

    suspend fun showErrorSnackbar(message: String) {
        showSnackbar(
            message = message,
            actionLabel = "Dismiss",
            duration = SnackbarDuration.Long
        )
    }

    suspend fun showSuccessSnackbar(message: String) {
        showSnackbar(
            message = message,
            duration = SnackbarDuration.Short
        )
    }
}

@EntryPoint
@InstallIn(SingletonComponent::class)
interface SnackbarControllerEntryPoint {
    fun snackbarController(): SnackbarController
}

data class SnackbarEvent(
    val message: String,
    val actionLabel: String? = null,
    val duration: SnackbarDuration = SnackbarDuration.Short
)
