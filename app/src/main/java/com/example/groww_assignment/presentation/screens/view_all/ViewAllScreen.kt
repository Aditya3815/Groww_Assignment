package com.example.groww_assignment.presentation.screens.view_all

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.with
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.groww_assignment.domain.usecase.pagination.PaginationResult
import com.example.groww_assignment.presentation.components.common.EmptyState
import com.example.groww_assignment.presentation.components.common.RefreshableContent
import com.example.groww_assignment.presentation.components.common.StocksSnackbarHost
import com.example.groww_assignment.presentation.components.search.StockSearchBar
import com.example.groww_assignment.presentation.components.stock.CompactStockItem
import com.example.groww_assignment.presentation.util.SnackbarController
import com.example.groww_assignment.presentation.util.SnackbarControllerEntryPoint
import com.example.groww_assignment.presentation.util.UiState
import com.example.groww_assignment.utils.Dimensions
import dagger.hilt.android.EntryPointAccessors

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun ViewAllScreen(
    type: String,
    onBackClick: () -> Unit,
    onStockClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ViewAllViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val listState = rememberLazyListState()
    val context = LocalContext.current

    val snackbarController = remember {
        EntryPointAccessors.fromApplication(
            context.applicationContext,
            SnackbarControllerEntryPoint::class.java
        ).snackbarController()
    }

    LaunchedEffect(listState) {
        snapshotFlow {
            listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index
        }.collect { lastVisibleIndex ->
            if (lastVisibleIndex != null &&
                lastVisibleIndex >= uiState.displayStocks.size - 3 &&
                uiState.canLoadMore
            ) {
                viewModel.loadMoreStocks()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(uiState.title) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.onSearchActiveChanged(true) }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search"
                        )
                    }
                }
            )
        },
        snackbarHost = {
            StocksSnackbarHost(
                snackbarController = snackbarController,
                snackbarHostState = snackbarHostState
            )
        }
    ) { paddingValues ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            AnimatedContent(
                targetState = uiState.isSearchActive,
                transitionSpec = {
                    if (targetState) {
                        slideInVertically(initialOffsetY = { -it }) + fadeIn() with
                                slideOutVertically(targetOffsetY = { -it }) + fadeOut()
                    } else {
                        slideInVertically(initialOffsetY = { it }) + fadeIn() with
                                slideOutVertically(targetOffsetY = { it }) + fadeOut()
                    }
                },
                label = "search_content_transition"
            ) { isSearchActive ->
                if (isSearchActive) {
                    SearchContent(
                        uiState = uiState,
                        onQueryChange = viewModel::onSearchQueryChanged,
                        onStockClick = { symbol ->
                            viewModel.onSearchActiveChanged(false)
                            onStockClick(symbol)
                        },
                        onCloseSearch = { viewModel.onSearchActiveChanged(false) }
                    )
                } else {
                    StocksListContent(
                        uiState = uiState,
                        listState = listState,
                        onStockClick = onStockClick,
                        onRefresh = viewModel::onRefresh,
                        onRetry = viewModel::onRetry,
                        onLoadMore = viewModel::loadMoreStocks
                    )
                }
            }
        }
    }
}

@Composable
private fun StocksListContent(
    uiState: ViewAllUiState,
    listState: LazyListState,
    onStockClick: (String) -> Unit,
    onRefresh: () -> Unit,
    onRetry: () -> Unit,
    onLoadMore: () -> Unit
) {
    RefreshableContent(
        uiState = when {
            uiState.isLoading -> UiState.Loading
            uiState.hasError -> UiState.Error(uiState.error!!)
            uiState.isEmpty -> UiState.Empty
            uiState.hasData -> UiState.Success(uiState.stocks)
            else -> UiState.Idle
        },
        onRefresh = onRefresh
    ) { _ ->
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = Dimensions.paddingSmall),
            verticalArrangement = Arrangement.spacedBy(1.dp)
        ) {
            item {
                ViewAllHeader(
                    subtitle = uiState.subtitle,
                    totalCount = uiState.stocks.size,
                    pagination = uiState.pagination
                )
            }

            items(
                items = uiState.displayStocks,
                key = { stock -> stock.symbol }
            ) { stock ->
                CompactStockItem(
                    stock = stock,
                    onClick = { onStockClick(stock.symbol) }
                )

                HorizontalDivider(
                    thickness = 0.5.dp,
                    color = MaterialTheme.colorScheme.outlineVariant
                )
            }

            if (uiState.isLoadingMore) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(Dimensions.paddingLarge),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp
                            )
                            Text(
                                text = "Loading more stocks...",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
            }

            if (!uiState.canLoadMore && uiState.hasData) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(Dimensions.paddingLarge),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No more stocks to load",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchContent(
    uiState: ViewAllUiState,
    onQueryChange: (String) -> Unit,
    onStockClick: (String) -> Unit,
    onCloseSearch: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Dimensions.paddingMedium)
    ) {
        StockSearchBar(
            query = uiState.searchQuery,
            onQueryChange = onQueryChange,
            onSearch = { /* Search is performed on query change */ },
            placeholder = "Search in ${uiState.title.lowercase()}..."
        )

        Spacer(modifier = Modifier.height(Dimensions.paddingMedium))

        when {
            uiState.searchQuery.isEmpty() -> {
                EmptyState(
                    title = "Search ${uiState.title}",
                    message = "Enter a stock symbol or company name to search",
                    icon = Icons.Default.Search
                )
            }

            uiState.filteredStocks.isEmpty() -> {
                EmptyState(
                    title = "No Results",
                    message = "No stocks found matching '${uiState.searchQuery}'",
                    icon = Icons.Default.SearchOff
                )
            }

            else -> {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(1.dp)
                ) {
                    items(
                        items = uiState.filteredStocks,
                        key = { stock -> stock.symbol }
                    ) { stock ->
                        CompactStockItem(
                            stock = stock,
                            onClick = { onStockClick(stock.symbol) }
                        )

                        HorizontalDivider(
                            thickness = 0.5.dp,
                            color = MaterialTheme.colorScheme.outlineVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ViewAllHeader(
    subtitle: String,
    totalCount: Int,
    pagination: PaginationResult<*>?,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(Dimensions.paddingMedium),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimensions.paddingMedium)
        ) {
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "$totalCount stocks",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                )

                if (pagination != null) {
                    Text(
                        text = "Page ${pagination.currentPage + 1} of ${pagination.totalPages}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}