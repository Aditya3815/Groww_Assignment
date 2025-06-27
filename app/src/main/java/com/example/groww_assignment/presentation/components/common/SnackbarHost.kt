package com.example.groww_assignment.presentation.components.common

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.groww_assignment.presentation.util.SnackbarController

@Composable
fun StocksSnackbarHost(
    snackbarController: SnackbarController,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
) {
    val events by snackbarController.events.collectAsStateWithLifecycle(initialValue = null)

    LaunchedEffect(events) {
        events?.let { event ->
            snackbarHostState.showSnackbar(
                message = event.message,
                actionLabel = event.actionLabel,
                duration = event.duration
            )
        }
    }

    SnackbarHost(hostState = snackbarHostState) { data ->
        Snackbar(
            snackbarData = data,
            containerColor = MaterialTheme.colorScheme.inverseSurface,
            contentColor = MaterialTheme.colorScheme.inverseOnSurface,
            actionColor = MaterialTheme.colorScheme.inversePrimary
        )
    }
}
