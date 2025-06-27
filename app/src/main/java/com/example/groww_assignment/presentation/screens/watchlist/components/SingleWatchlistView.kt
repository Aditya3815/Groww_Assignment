package com.example.groww_assignment.presentation.screens.watchlist.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.groww_assignment.domain.model.Stock
import com.example.groww_assignment.domain.model.Watchlist
import com.example.groww_assignment.presentation.components.common.EmptyState
import com.example.groww_assignment.presentation.components.watchlist.WatchlistStockItem
import com.example.groww_assignment.utils.Dimensions

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SingleWatchlistView(
    watchlist: Watchlist,
    stocks: List<Stock>,
    onBackClick: () -> Unit,
    onStockClick: (String) -> Unit,
    onRemoveStock: (String) -> Unit,
    onSearchClick: () -> Unit,
    isUpdatingStocks: Boolean,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
        // Custom header
        TopAppBar(
            title = {
                Column {
                    Text(
                        text = watchlist.name,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "${stocks.size} stock${if (stocks.size != 1) "s" else ""}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back to watchlists"
                    )
                }
            },
            actions = {
                IconButton(onClick = onSearchClick) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search stocks"
                    )
                }
            }
        )

        if (stocks.isEmpty()) {
            EmptyWatchlistState(
                watchlistName = watchlist.name,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            LazyColumn(
                contentPadding = PaddingValues(vertical = Dimensions.paddingSmall),
                verticalArrangement = Arrangement.spacedBy(1.dp)
            ) {
                items(
                    items = stocks,
                    key = { stock -> stock.symbol }
                ) { stock ->
                    WatchlistStockItem(
                        stock = stock,
                        onStockClick = { onStockClick(stock.symbol) },
                        onRemoveClick = { onRemoveStock(stock.symbol) },
                        showRemoveOption = true
                    )

                    HorizontalDivider(
                        thickness = 0.5.dp,
                        color = MaterialTheme.colorScheme.outlineVariant
                    )
                }

                // Loading indicator at bottom if updating
                if (isUpdatingStocks) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(Dimensions.paddingMedium),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                strokeWidth = 2.dp
                            )
                        }
                    }
                }

                // Bottom spacing
                item {
                    Spacer(modifier = Modifier.height(Dimensions.paddingLarge))
                }
            }
        }
    }
}

@Composable
private fun EmptyWatchlistState(
    watchlistName: String,
    modifier: Modifier = Modifier
) {
    EmptyState(
        title = "Empty Watchlist",
        message = "No stocks in '$watchlistName' yet.\nAdd stocks from the stock detail screen.",
        modifier = modifier
    )
}