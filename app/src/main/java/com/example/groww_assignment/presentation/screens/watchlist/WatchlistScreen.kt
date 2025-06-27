package com.example.groww_assignment.presentation.screens.watchlist

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.with
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.groww_assignment.domain.model.Watchlist
import com.example.groww_assignment.presentation.components.common.EmptyState
import com.example.groww_assignment.presentation.components.common.RefreshableContent
import com.example.groww_assignment.presentation.components.common.StocksSnackbarHost
import com.example.groww_assignment.presentation.components.search.StockSearchBar
import com.example.groww_assignment.presentation.components.watchlist.WatchlistStockItem
import com.example.groww_assignment.presentation.screens.watchlist.components.AllWatchlistsView
import com.example.groww_assignment.presentation.screens.watchlist.components.CreateWatchlistDialog
import com.example.groww_assignment.presentation.screens.watchlist.components.DeleteWatchlistDialog
import com.example.groww_assignment.presentation.screens.watchlist.components.SingleWatchlistView
import com.example.groww_assignment.presentation.util.SnackbarControllerEntryPoint
import com.example.groww_assignment.presentation.util.UiState
import com.example.groww_assignment.utils.Dimensions
import dagger.hilt.android.EntryPointAccessors

@Composable
fun WatchlistScreen(
    onStockClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: WatchlistViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    val snackbarController = remember {
        EntryPointAccessors.fromApplication(
            context.applicationContext,
            SnackbarControllerEntryPoint::class.java
        ).snackbarController()
    }

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        // Main content
        when (uiState.viewMode) {
            WatchlistViewMode.ALL_WATCHLISTS -> {
                AllWatchlistsContent(
                    uiState = uiState,
                    onWatchlistClick = viewModel::onWatchlistSelected,
                    onCreateWatchlistClick = viewModel::onCreateWatchlistClick,
                    onEditWatchlist = { /* TODO: Implement edit */ },
                    onDeleteWatchlist = viewModel::onDeleteWatchlistClick,
                    onRefresh = viewModel::onRefresh
                )
            }

            WatchlistViewMode.SINGLE_WATCHLIST -> {
                SingleWatchlistContent(
                    uiState = uiState,
                    onBackClick = viewModel::onBackToAllWatchlists,
                    onStockClick = onStockClick,
                    onRemoveStock = viewModel::onRemoveStockFromWatchlist,
                    onSearchActiveChanged = viewModel::onSearchActiveChanged,
                    onSearchQueryChanged = viewModel::onSearchQueryChanged
                )
            }
        }

        // Dialogs
        if (uiState.showCreateDialog) {
            CreateWatchlistDialog(
                onDismiss = viewModel::onDismissCreateDialog,
                onCreateWatchlist = viewModel::onCreateWatchlist,
                isCreating = uiState.isCreatingWatchlist
            )
        }

        if (uiState.showDeleteDialog && uiState.watchlistToDelete != null) {
            DeleteWatchlistDialog(
                watchlist = uiState.watchlistToDelete!!,
                onDismiss = viewModel::onDismissDeleteDialog,
                onConfirmDelete = viewModel::onConfirmDeleteWatchlist,
                isDeleting = uiState.isDeletingWatchlist
            )
        }
    }

    StocksSnackbarHost(
        snackbarController = snackbarController,
        snackbarHostState = snackbarHostState
    )
}

@Composable
private fun AllWatchlistsContent(
    uiState: WatchlistUiState,
    onWatchlistClick: (Watchlist) -> Unit,
    onCreateWatchlistClick: () -> Unit,
    onEditWatchlist: (Watchlist) -> Unit,
    onDeleteWatchlist: (Watchlist) -> Unit,
    onRefresh: () -> Unit
) {
    RefreshableContent(
        uiState = when {
            uiState.isLoading -> UiState.Loading
            uiState.hasError -> UiState.Error(uiState.error!!)
            uiState.isEmpty -> UiState.Empty
            uiState.hasWatchlists -> UiState.Success(uiState.watchlists)
            else -> UiState.Idle
        },
        onRefresh = onRefresh
    ) { watchlists ->
        AllWatchlistsView(
            watchlists = watchlists,
            onWatchlistClick = onWatchlistClick,
            onCreateWatchlistClick = onCreateWatchlistClick,
            onEditWatchlist = onEditWatchlist,
            onDeleteWatchlist = onDeleteWatchlist,
            canCreateWatchlist = uiState.canCreateWatchlist
        )
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun SingleWatchlistContent(
    uiState: WatchlistUiState,
    onBackClick: () -> Unit,
    onStockClick: (String) -> Unit,
    onRemoveStock: (String) -> Unit,
    onSearchActiveChanged: (Boolean) -> Unit,
    onSearchQueryChanged: (String) -> Unit
) {
    val selectedWatchlist = uiState.selectedWatchlist ?: return

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
        label = "watchlist_search_transition"
    ) { isSearchActive ->
        if (isSearchActive) {
            SearchWatchlistContent(
                uiState = uiState,
                onQueryChange = onSearchQueryChanged,
                onStockClick = { symbol ->
                    onSearchActiveChanged(false)
                    onStockClick(symbol)
                },
                onCloseSearch = { onSearchActiveChanged(false) }
            )
        } else {
            SingleWatchlistView(
                watchlist = selectedWatchlist,
                stocks = uiState.displayStocks,
                onBackClick = onBackClick,
                onStockClick = onStockClick,
                onRemoveStock = onRemoveStock,
                onSearchClick = { onSearchActiveChanged(true) },
                isUpdatingStocks = uiState.isUpdatingStocks
            )
        }
    }
}

@Composable
private fun SearchWatchlistContent(
    uiState: WatchlistUiState,
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
            placeholder = "Search stocks in ${uiState.selectedWatchlist?.name}..."
        )

        Spacer(modifier = Modifier.height(Dimensions.paddingMedium))

        when {
            uiState.searchQuery.isEmpty() -> {
                EmptyState(
                    title = "Search Stocks",
                    message = "Enter a stock symbol or company name to search",
                    icon = androidx.compose.material.icons.Icons.Default.Search
                )
            }

            uiState.filteredStocks.isEmpty() -> {
                EmptyState(
                    title = "No Results",
                    message = "No stocks found matching '${uiState.searchQuery}'",
                    icon = androidx.compose.material.icons.Icons.Default.SearchOff
                )
            }

            else -> {
                androidx.compose.foundation.lazy.LazyColumn(
                    verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(1.dp)
                ) {
                   items(
                        items = uiState.filteredStocks,
                        key = { stock -> stock.symbol }
                    ) { stock ->
                       WatchlistStockItem(
                            stock = stock,
                            onStockClick = { onStockClick(stock.symbol) },
                            onRemoveClick = { /* Remove handled in main view */ },
                            showRemoveOption = false // Don't show remove in search
                        )

                        androidx.compose.material3.HorizontalDivider(
                            thickness = 0.5.dp,
                            color = androidx.compose.material3.MaterialTheme.colorScheme.outlineVariant
                        )
                    }
                }
            }
        }
    }
}
