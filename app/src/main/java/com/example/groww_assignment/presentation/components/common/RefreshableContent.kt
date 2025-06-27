package com.example.groww_assignment.presentation.components.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.groww_assignment.presentation.util.UiState
import kotlinx.coroutines.delay

@Composable
fun <T> RefreshableContent(
    uiState: UiState<T>,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable (T) -> Unit
) {
    var isRefreshing by remember { mutableStateOf(false) }

    LaunchedEffect(uiState) {
        if (uiState !is UiState.Loading && isRefreshing) {
            delay(500)
            isRefreshing = false
        }
    }

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = {
            isRefreshing = true
            onRefresh()
        },
        modifier = modifier
    ) {
        UiStateHandler(
            uiState = uiState,
            onRetry = onRefresh,
            successContent = content
        )
    }
}