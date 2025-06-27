package com.example.groww_assignment.presentation.screens.explore.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.groww_assignment.domain.model.Stock
import com.example.groww_assignment.presentation.components.common.EmptyState
import com.example.groww_assignment.presentation.components.stock.StockCard
import com.example.groww_assignment.utils.Dimensions

@Composable
fun TopGainersSection(
    stocks: List<Stock>,
    onStockClick: (String) -> Unit,
    onViewAllClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        SectionHeader(
            title = "Top Gainers",
            subtitle = "Best performing stocks today",
            icon = Icons.Default.TrendingUp,
            onViewAllClick = onViewAllClick
        )

        Spacer(modifier = Modifier.height(Dimensions.paddingMedium))

        if (stocks.isEmpty()) {
            EmptyState(
                title = "No Gainers Data",
                message = "Top gainers information is not available right now",
                modifier = Modifier.height(200.dp)
            )
        } else {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(Dimensions.paddingSmall),
                contentPadding = PaddingValues(horizontal = Dimensions.paddingMedium)
            ) {
                items(
                    items = stocks,
                    key = { stock -> stock.symbol }
                ) { stock ->
                    StockCard(
                        stock = stock,
                        onClick = { onStockClick(stock.symbol) },
                        modifier = Modifier.width(280.dp)
                    )
                }
            }
        }
    }
}