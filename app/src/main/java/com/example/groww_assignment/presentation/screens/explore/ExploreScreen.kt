package com.example.groww_assignment.presentation.screens.explore

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.groww_assignment.presentation.components.common.ErrorMessage
import com.example.groww_assignment.presentation.components.common.LoadingIndicator
import com.example.groww_assignment.presentation.components.common.RefreshableContent
import com.example.groww_assignment.presentation.components.common.StocksSnackbarHost
import com.example.groww_assignment.presentation.components.search.SearchResults
import com.example.groww_assignment.presentation.components.search.SearchSuggestions
import com.example.groww_assignment.presentation.components.search.StockSearchBar
import com.example.groww_assignment.presentation.screens.explore.components.ExploreHeader
import com.example.groww_assignment.presentation.screens.explore.components.MostActiveSection
import com.example.groww_assignment.presentation.screens.explore.components.TopGainersSection
import com.example.groww_assignment.presentation.screens.explore.components.TopLosersSection
import com.example.groww_assignment.presentation.util.SnackbarController
import com.example.groww_assignment.presentation.util.SnackbarControllerEntryPoint
import com.example.groww_assignment.presentation.util.UiState
import com.example.groww_assignment.utils.Dimensions
import dagger.hilt.android.EntryPointAccessors

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun ExploreScreen(
    onStockClick: (String) -> Unit,
    onViewAllClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ExploreViewModel = hiltViewModel(),
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Explore") },
                actions = {
                    IconButton(
                        onClick = { viewModel.onSearchActiveChanged(true) }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search stocks"
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
            // Main content or search overlay
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
                    SearchOverlay(
                        uiState = uiState,
                        onQueryChange = viewModel::onSearchQueryChanged,
                        onSearchSubmit = viewModel::onSearchSubmitted,
                        onStockClick = { symbol ->
                            viewModel.onSearchActiveChanged(false)
                            onStockClick(symbol)
                        },
                        onCloseSearch = { viewModel.onSearchActiveChanged(false) }
                    )
                } else {
                    ExploreContent(
                        uiState = uiState,
                        onStockClick = onStockClick,
                        onViewAllClick = onViewAllClick,
                        onRefresh = viewModel::onRefresh,
                        onRetry = viewModel::onRetry
                    )
                }
            }
        }
    }
}

@Composable
private fun ExploreContent(
    uiState: ExploreUiState,
    onStockClick: (String) -> Unit,
    onViewAllClick: (String) -> Unit,
    onRefresh: () -> Unit,
    onRetry: () -> Unit
) {
    RefreshableContent(
        uiState = when {
            uiState.isLoading -> UiState.Loading
            uiState.hasError -> UiState.Error(uiState.error!!)
            uiState.isEmpty -> UiState.Empty
            uiState.hasData -> UiState.Success(uiState.topGainersLosers!!)
            else -> UiState.Idle
        },
        onRefresh = onRefresh
    ) { data ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(Dimensions.paddingLarge),
            contentPadding = PaddingValues(vertical = Dimensions.paddingMedium)
        ) {
            // Header
            item {
                ExploreHeader(
                    lastUpdated = uiState.lastUpdated,
                    isRefreshing = uiState.isRefreshing,
                    onRefreshClick = onRefresh
                )
            }

            // Market Summary Card
            item {
                MarketSummaryCard(
                    topGainersCount = uiState.topGainers.size,
                    topLosersCount = uiState.topLosers.size,
                    mostActiveCount = uiState.mostActive.size,
                    modifier = Modifier.padding(horizontal = Dimensions.paddingMedium)
                )
            }

            // Top Gainers Section
            if (uiState.hasTopGainers) {
                item {
                    TopGainersSection(
                        stocks = uiState.topGainers.take(10), // Show top 10
                        onStockClick = onStockClick,
                        onViewAllClick = { onViewAllClick("gainers") }
                    )
                }
            }

            // Top Losers Section
            if (uiState.hasTopLosers) {
                item {
                    TopLosersSection(
                        stocks = uiState.topLosers.take(10),
                        onStockClick = onStockClick,
                        onViewAllClick = { onViewAllClick("losers") }
                    )
                }
            }

            // Most Active Section
            if (uiState.hasMostActive) {
                item {
                    MostActiveSection(
                        stocks = uiState.mostActive.take(10),
                        onStockClick = onStockClick,
                        onViewAllClick = { onViewAllClick("active") }
                    )
                }
            }

            // Footer spacing
            item {
                Spacer(modifier = Modifier.height(Dimensions.paddingLarge))
            }
        }
    }
}

@Composable
private fun SearchOverlay(
    uiState: ExploreUiState,
    onQueryChange: (String) -> Unit,
    onSearchSubmit: (String) -> Unit,
    onStockClick: (String) -> Unit,
    onCloseSearch: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Dimensions.paddingMedium)
    ) {
        // Search bar
        StockSearchBar(
            query = uiState.searchQuery,
            onQueryChange = onQueryChange,
            onSearch = onSearchSubmit,
            isLoading = uiState.isSearchLoading,
            placeholder = "Search stocks, ETFs, symbols..."
        )

        Spacer(modifier = Modifier.height(Dimensions.paddingMedium))

        // Search results or suggestions
        when {
            uiState.searchQuery.isEmpty() -> {
                SearchSuggestionsContent(
                    onSuggestionClick = { query ->
                        onQueryChange(query)
                        onSearchSubmit(query)
                    }
                )
            }

            uiState.isSearchLoading -> {
                LoadingIndicator(
                    message = "Searching stocks...",
                    modifier = Modifier.fillMaxWidth()
                )
            }

            uiState.searchError != null -> {
                ErrorMessage(
                    message = uiState.searchError,
                    onRetry = { onSearchSubmit(uiState.searchQuery) }
                )
            }

            else -> {
                SearchResults(
                    results = uiState.searchResults,
                    onResultClick = onStockClick,
                    emptyMessage = "No stocks found for '${uiState.searchQuery}'"
                )
            }
        }
    }
}

@Composable
private fun SearchSuggestionsContent(
    onSuggestionClick: (String) -> Unit
) {
    SearchSuggestions(
        popularStocks = listOf("AAPL", "GOOGL", "MSFT", "AMZN", "TSLA", "META", "NVDA", "NFLX"),
        onSuggestionClick = onSuggestionClick
    )
}

@Composable
private fun MarketSummaryCard(
    topGainersCount: Int,
    topLosersCount: Int,
    mostActiveCount: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimensions.paddingLarge),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            MarketSummaryItem(
                title = "Gainers",
                count = topGainersCount,
                modifier = Modifier.weight(1f)
            )

            VerticalDivider(
                modifier = Modifier.height(48.dp),
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.3f)
            )

            MarketSummaryItem(
                title = "Losers",
                count = topLosersCount,
                modifier = Modifier.weight(1f)
            )

            VerticalDivider(
                modifier = Modifier.height(48.dp),
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.3f)
            )

            MarketSummaryItem(
                title = "Active",
                count = mostActiveCount,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun MarketSummaryItem(
    title: String,
    count: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
    ) {
        Text(
            text = count.toString(),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
        )
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
        )
    }
}