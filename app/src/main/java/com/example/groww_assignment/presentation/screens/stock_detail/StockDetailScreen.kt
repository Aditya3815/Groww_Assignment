package com.example.groww_assignment.presentation.screens.stock_detail

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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.groww_assignment.domain.model.Stock
import com.example.groww_assignment.presentation.components.common.RefreshableContent
import com.example.groww_assignment.presentation.components.common.StocksSnackbarHost
import com.example.groww_assignment.presentation.components.watchlist.AddToWatchlistDialog
import com.example.groww_assignment.presentation.screens.stock_detail.components.StockChartSection
import com.example.groww_assignment.presentation.screens.stock_detail.components.StockHeader
import com.example.groww_assignment.presentation.screens.stock_detail.components.StockInfoSection
import com.example.groww_assignment.presentation.util.UiState
import com.example.groww_assignment.ui.theme.GreenPositive
import com.example.groww_assignment.utils.Dimensions
import java.util.Calendar
import com.example.groww_assignment.domain.util.formatAsPrice
import com.example.groww_assignment.presentation.util.SnackbarControllerEntryPoint
import dagger.hilt.android.EntryPointAccessors


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StockDetailScreen(
    symbol: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: StockDetailViewModel = hiltViewModel(),
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
                title = { Text(uiState.displayName) },
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
                        onClick = {
                            shareStock(
                                context = context,
                                symbol = symbol,
                                price = uiState.currentPrice,
                                change = uiState.priceChange
                            )
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share stock"
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
            RefreshableContent(
                uiState = when {
                    uiState.isLoading -> UiState.Loading
                    uiState.hasError -> UiState.Error(uiState.error!!)
                    uiState.isEmpty -> UiState.Empty
                    uiState.hasData -> UiState.Success(uiState.stockDetail!!)
                    else -> UiState.Idle
                },
                onRefresh = viewModel::onRefresh
            ) { stockDetail ->
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(Dimensions.paddingMedium),
                    verticalArrangement = Arrangement.spacedBy(Dimensions.paddingMedium)
                ) {
                    // Stock Header with price and watchlist button
                    item {
                        StockHeader(
                            symbol = stockDetail.symbol,
                            name = stockDetail.name,
                            currentPrice = uiState.currentPrice,
                            change = uiState.priceChange,
                            changePercent = uiState.priceChangePercent,
                            isPositive = uiState.isPositive,
                            isInAnyWatchlist = uiState.isInAnyWatchlist,
                            onWatchlistClick = viewModel::onAddToWatchlistClick
                        )
                    }

                    // Chart Section
                    item {
                        StockChartSection(
                            timeSeriesData = uiState.chartData,
                            selectedTimeRange = uiState.selectedTimeRange,
                            onTimeRangeSelected = viewModel::onTimeRangeSelected,
                            isLoading = uiState.isChartLoading,
                            error = uiState.chartError,
                            onRetry = { viewModel.loadStockDetail() }
                        )
                    }

                    // Company Information
                    item {
                        StockInfoSection(stockDetail = stockDetail)
                    }

                    // Market Status and Last Updated
                    item {
                        MarketStatusCard(
                            lastUpdated = uiState.lastUpdated,
                            symbol = stockDetail.symbol
                        )
                    }

                    // Additional spacing at bottom
                    item {
                        Spacer(modifier = Modifier.height(Dimensions.paddingLarge))
                    }
                }
            }

            // Add to Watchlist Dialog
            if (uiState.showAddToWatchlistDialog && uiState.stockDetail != null) {
                AddToWatchlistDialog(
                    stock = Stock(
                        symbol = uiState.stockDetail!!.symbol,
                        name = uiState.stockDetail!!.name,
                        price = uiState.currentPrice,
                        change = uiState.priceChange,
                        changePercent = uiState.priceChangePercent,
                        volume = uiState.stockDetail!!.volume
                    ),
                    watchlists = uiState.watchlists,
                    onDismiss = viewModel::onDismissWatchlistDialog,
                    onAddToExisting = viewModel::onAddToExistingWatchlist,
                    onCreateNew = viewModel::onCreateNewWatchlist
                )
            }
        }
    }
}

@Composable
private fun MarketStatusCard(
    lastUpdated: String,
    symbol: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimensions.paddingMedium)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Market Status",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text(
                    text = getMarketStatusText(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isMarketOpen())
                        GreenPositive
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }

            if (lastUpdated.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Last updated: $lastUpdated",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Data provided by Alpha Vantage",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            )
        }
    }
}

private fun shareStock(
    context: android.content.Context,
    symbol: String,
    price: Double,
    change: Double
) {
    val shareText = buildString {
        append("Check out $symbol stock!\n")
        append("Current Price: ${price.formatAsPrice()}\n")
        append("Change: ${if (change >= 0) "+" else ""}${change.formatAsPrice()}\n")
        append("\nShared from Stocks App")
    }

    val shareIntent = android.content.Intent().apply {
        action = android.content.Intent.ACTION_SEND
        putExtra(android.content.Intent.EXTRA_TEXT, shareText)
        type = "text/plain"
    }

    val chooser = android.content.Intent.createChooser(shareIntent, "Share $symbol")
    context.startActivity(chooser)
}

private fun getMarketStatusText(): String {
    val calendar = Calendar.getInstance()
    val hour = calendar.get(Calendar.HOUR_OF_DAY)
    val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)

    return when {
        dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY -> "Market Closed (Weekend)"
        hour in 9..16 -> "Market Open"
        hour < 9 -> "Pre-Market"
        else -> "After Hours"
    }
}

private fun isMarketOpen(): Boolean {
    val calendar = Calendar.getInstance()
    val hour = calendar.get(Calendar.HOUR_OF_DAY)
    val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)

    return dayOfWeek in Calendar.MONDAY..Calendar.FRIDAY && hour in 9..16
}
