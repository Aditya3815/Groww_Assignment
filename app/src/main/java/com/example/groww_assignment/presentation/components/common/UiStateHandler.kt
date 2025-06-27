package com.example.groww_assignment.presentation.components.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.groww_assignment.domain.util.ErrorResult
import com.example.groww_assignment.presentation.util.UiState

@Composable
fun <T> UiStateHandler(
    uiState: UiState<T>,
    onRetry: (() -> Unit)? = null,
    loadingContent: @Composable () -> Unit = { FullScreenLoading() },
    emptyContent: @Composable () -> Unit = {
        FullScreenEmptyState(
            title = "No Data",
            message = "No data available"
        )
    },
    errorContent: @Composable (ErrorResult) -> Unit = { error ->
        Box(modifier = Modifier.fillMaxSize()) {
            ErrorMessage(
                message = error.message,
                onRetry = onRetry
            )
        }
    },
    successContent: @Composable (T) -> Unit
) {
    when (uiState) {
        is UiState.Idle -> {
        }
        is UiState.Loading -> loadingContent()
        is UiState.Success -> successContent(uiState.data)
        is UiState.Error -> errorContent(uiState.errorResult)
        is UiState.Empty -> emptyContent()
    }
}

@Composable
fun FullScreenEmptyState(
    title: String,
    message: String
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = androidx.compose.ui.Alignment.Center
    ) {
        Column(
            horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
